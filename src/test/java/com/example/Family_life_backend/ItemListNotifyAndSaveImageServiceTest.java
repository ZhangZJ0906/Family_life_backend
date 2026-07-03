package com.example.Family_life_backend;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockMultipartFile;

import com.example.Family_life_backend.service.ItemListNotifyAndSaveImageService;

/**
 * 測試重點： 1. 路徑穿越 (Path Traversal)：確認惡意檔名不會被拿去當實際存檔路徑 2. 副檔名白名單：非圖片副檔名應被拒絕 3.
 * 檔案大小限制：超過 5MB 應被拒絕
 *
 * 注意：這支測試直接操作實際檔案系統的 /app/uploads， 如果你的 store() 之後改成可注入路徑（建議這樣改，見最下方備註），
 * 測試會更好寫、也更安全（不用碰真實磁碟路徑）。
 */
class ItemListNotifyAndSaveImageServiceTest {

	private ItemListNotifyAndSaveImageService service;
	private static final Path UPLOAD_DIR = Paths.get("/app/uploads");

	@BeforeEach
	void setUp() {
		service = new ItemListNotifyAndSaveImageService();
	}

	@AfterEach
	void cleanUp() throws IOException {
		// 測試後清掉這次產生的檔案，避免污染環境（依實際情況調整或改用暫存目錄）
		if (Files.exists(UPLOAD_DIR)) {
			try (Stream<Path> files = Files.list(UPLOAD_DIR)) {
				files.filter(p -> p.getFileName().toString().length() > 30) // UUID 檔名較長
						.forEach(p -> {
							try {
								Files.deleteIfExists(p);
							} catch (IOException ignored) {
							}
						});
			}
		}
	}

	// ────────────────────────────────
	// 1. 路徑穿越測試：核心攻擊面
	// ────────────────────────────────

	@ParameterizedTest(name = "惡意檔名應被淨化：{0}")
	@ValueSource(strings = { "../../../../etc/cron.d/evil.png", "..\\..\\..\\windows\\system32\\evil.png",
			"../../../application.properties.png", "....//....//etc/passwd.png", "/etc/passwd.png" })
	void store_maliciousFilename_shouldNotEscapeUploadDir(String maliciousName) throws IOException {
		// Arrange：偽造一個檔名帶有路徑穿越字元、但 contentType 合法的檔案
		MockMultipartFile maliciousFile = new MockMultipartFile("avatar", maliciousName, "image/png",
				"fake-image-bytes".getBytes());

		// Act
		String resultPath = service.store(maliciousFile);

		// Assert 1：回傳的路徑不應包含任何路徑穿越符號
		assertNotNull(resultPath);
		assertFalse(resultPath.contains(".."), "回傳路徑不應包含 '..'：" + resultPath);
		assertFalse(resultPath.contains("etc"), "回傳路徑不應洩漏原始惡意路徑片段：" + resultPath);
		assertTrue(resultPath.startsWith("/uploads/"), "回傳路徑應該固定在 /uploads/ 底下：" + resultPath);

		// Assert 2：實際落地的檔案，路徑必須真的在 /app/uploads 底下，不能跑到外面
		String fileName = resultPath.replace("/uploads/", "");
		Path actualFile = UPLOAD_DIR.resolve(fileName).normalize();
		assertTrue(actualFile.startsWith(UPLOAD_DIR), "存檔位置被跳脫到預期目錄之外了！實際路徑：" + actualFile);

		// Assert 3：確認外部沒有真的多出被攻擊的檔案（示範對 /etc 底下的檢查）
		Path suspiciousEtcFile = Paths.get("/etc/cron.d/evil.png");
		assertFalse(Files.exists(suspiciousEtcFile), "偵測到疑似路徑穿越成功寫入 /etc/cron.d/！");
	}

	// ────────────────────────────────
	// 2. 副檔名白名單測試
	// ────────────────────────────────

	@ParameterizedTest(name = "不允許的副檔名應丟出例外：{0}")
	@ValueSource(strings = { "evil.exe", "shell.php", "backdoor.jsp", "script.sh", "payload.html", "noextension" })
	void store_disallowedExtension_shouldThrow(String fileName) {
		MockMultipartFile file = new MockMultipartFile("avatar", fileName, "image/png", "fake-bytes".getBytes());

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.store(file));
		assertEquals("不支援的檔案格式", ex.getMessage());
	}

	@ParameterizedTest(name = "允許的圖片副檔名應成功存檔：{0}")
	@ValueSource(strings = { "photo.png", "photo.jpg", "photo.jpeg", "photo.gif", "photo.webp", "PHOTO.PNG" })
	void store_allowedExtension_shouldSucceed(String fileName) {
		MockMultipartFile file = new MockMultipartFile("avatar", fileName, "image/png", "fake-bytes".getBytes());

		assertDoesNotThrow(() -> {
			String result = service.store(file);
			assertNotNull(result);
			assertTrue(result.startsWith("/uploads/"));
		});
	}

	// ────────────────────────────────
	// 3. UUID 檔名唯一性測試（避免覆蓋別人的檔案）
	// ────────────────────────────────

	@Test
	void store_sameOriginalName_shouldGenerateDifferentFileNames() {
		MockMultipartFile file1 = new MockMultipartFile("avatar", "same.png", "image/png", "a".getBytes());
		MockMultipartFile file2 = new MockMultipartFile("avatar", "same.png", "image/png", "b".getBytes());

		String path1 = service.store(file1);
		String path2 = service.store(file2);

		assertNotEquals(path1, path2, "相同原始檔名應該產生不同的實際存檔檔名（UUID），避免互相覆蓋");
	}

	// ────────────────────────────────
	// 4. validateImage() 檔案大小限制測試
	// ────────────────────────────────

	@Test
	void validateImage_overSizeLimit_shouldThrow() {
		byte[] bigContent = new byte[6 * 1024 * 1024]; // 6MB，超過 5MB 限制
		MockMultipartFile bigFile = new MockMultipartFile("avatar", "big.png", "image/png", bigContent);

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> service.validateImage(bigFile));
		assertEquals("圖片大小不能超過 5MB", ex.getMessage());
	}

	@Test
	void validateImage_wrongContentType_shouldThrow() {
		MockMultipartFile notImage = new MockMultipartFile("avatar", "resume.pdf", "application/pdf",
				"pdf-bytes".getBytes());

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> service.validateImage(notImage));
		assertEquals("只能上傳圖片檔案", ex.getMessage());
	}

	@Test
	void validateImage_nullOrEmpty_shouldNotThrow() {
		assertDoesNotThrow(() -> service.validateImage(null));

		MockMultipartFile emptyFile = new MockMultipartFile("avatar", "empty.png", "image/png", new byte[0]);
		assertDoesNotThrow(() -> service.validateImage(emptyFile));
	}
}

/*
 * ──────────────────────────────────────────────────────────── 備註：如果想讓 store()
 * 更好測試（不用直接碰 /app/uploads 真實路徑）， 建議把上傳目錄抽成可注入的欄位，例如：
 *
 * @Value("${app.upload-dir:/app/uploads}") private String uploadDir;
 *
 * 測試時就能用 @TestPropertySource 或建構子注入指到暫存目錄（如 JUnit 的 @TempDir）， 不會污染正式的
 * /app/uploads，也讓測試在 CI 環境（沒有 /app 目錄權限）能順利跑。
 * ────────────────────────────────────────────────────────────
 */