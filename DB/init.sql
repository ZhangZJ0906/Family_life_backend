-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: family_db
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `calendar_events`
--

DROP TABLE IF EXISTS `calendar_events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `calendar_events` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '行事曆事件 ID',
  `event_batch_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '同一批活動識別碼',
  `group_id` bigint DEFAULT NULL COMMENT '所屬群組',
  `created_by` bigint NOT NULL COMMENT '建立者',
  `assigned_user_id` bigint DEFAULT NULL COMMENT '指派成員',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '活動名稱',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '活動描述',
  `event_time` datetime NOT NULL COMMENT '活動時間',
  `end_time` datetime DEFAULT NULL,
  `notify_before` int NOT NULL DEFAULT '0' COMMENT '提前幾分鐘通知',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
  `is_send_before_notify` tinyint DEFAULT '0',
  `is_send_start_notify` tinyint DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_calendar_events_created_by` (`created_by`),
  KEY `idx_calendar_events_event_time` (`event_time`),
  KEY `idx_calendar_events_batch_id` (`event_batch_id`),
  KEY `idx_calendar_group_user_time` (`group_id`,`assigned_user_id`,`event_time`),
  KEY `idx_calendar_batch_id` (`event_batch_id`),
  KEY `idx_calendar_before_notify` (`is_send_before_notify`,`event_time`,`notify_before`),
  KEY `idx_calendar_start_notify` (`is_send_start_notify`,`event_time`),
  CONSTRAINT `fk_calendar_events_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家庭行事曆';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `category_id` bigint NOT NULL AUTO_INCREMENT COMMENT '分類 ID',
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分類名稱',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分類圖示',
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `uk_categories_category_name` (`category_name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物品分類';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `expenses`
--

DROP TABLE IF EXISTS `expenses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `expenses` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '記帳 ID',
  `group_id` bigint DEFAULT NULL COMMENT '所屬群組',
  `user_id` bigint NOT NULL COMMENT '記帳者',
  `category_id` int NOT NULL COMMENT '支出分類',
  `related_item_id` bigint DEFAULT NULL COMMENT '關聯物品 ID',
  `related_item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` int NOT NULL COMMENT '價錢',
  `expense_date` date NOT NULL COMMENT '消費日期',
  `note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '備註',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
  PRIMARY KEY (`id`),
  KEY `idx_expenses_group_id` (`group_id`),
  KEY `idx_expenses_user_id` (`user_id`),
  KEY `idx_expenses_related_item_id` (`related_item_id`),
  KEY `idx_expenses_expense_date` (`expense_date`),
  CONSTRAINT `fk_expenses_related_item_id` FOREIGN KEY (`related_item_id`) REFERENCES `items` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_expenses_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家庭記帳';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group_chat_message`
--

DROP TABLE IF EXISTS `group_chat_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_chat_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `group_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  `message` text,
  `create_time` datetime DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `is_recall` tinyint NOT NULL DEFAULT '0',
  `reply_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group_chat_read`
--

DROP TABLE IF EXISTS `group_chat_read`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_chat_read` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `message_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `read_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_read` (`message_id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=242 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group_members`
--

DROP TABLE IF EXISTS `group_members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_members` (
  `group_id` bigint NOT NULL COMMENT '所屬群組',
  `user_id` bigint NOT NULL COMMENT '使用者 ID',
  `public_inventory` int NOT NULL DEFAULT '0' COMMENT '是否公開物品資訊給群組',
  PRIMARY KEY (`group_id`,`user_id`),
  KEY `idx_group_members_user_id` (`user_id`),
  KEY `idx_group_members_user_group` (`user_id`,`group_id`),
  CONSTRAINT `fk_group_members_group_id` FOREIGN KEY (`group_id`) REFERENCES `groups` (`group_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_group_members_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='群組成員';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groups`
--

DROP TABLE IF EXISTS `groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `groups` (
  `group_id` bigint NOT NULL AUTO_INCREMENT COMMENT '群組 ID',
  `group_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '家庭名稱',
  `invite_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邀請碼',
  `created_by` bigint NOT NULL COMMENT '建立者 user_id',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
  `avatar` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`group_id`),
  UNIQUE KEY `uk_groups_invite_code` (`invite_code`),
  KEY `idx_groups_created_by` (`created_by`),
  CONSTRAINT `fk_groups_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家庭群組';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invited_members`
--

DROP TABLE IF EXISTS `invited_members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invited_members` (
  `user_id` bigint NOT NULL,
  `group_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `items`
--

DROP TABLE IF EXISTS `items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `items` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '物品 ID',
  `group_id` bigint DEFAULT NULL COMMENT '所屬家庭群組',
  `category_id` bigint NOT NULL COMMENT '分類 ID',
  `created_by_id` bigint NOT NULL COMMENT '建立者',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '物品名稱',
  `quantity` int NOT NULL DEFAULT '0' COMMENT '目前數量',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '單位，例如包、瓶、盒',
  `location_id` bigint DEFAULT NULL COMMENT '存放位置',
  `purchase_date` date DEFAULT NULL COMMENT '購買日期',
  `expire_date` date DEFAULT NULL COMMENT '到期日',
  `safe_quantity` int NOT NULL DEFAULT '0',
  `price` int DEFAULT NULL COMMENT '購買金額',
  `notify` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否提醒',
  `note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '備註',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
  `unit_price` int DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `save_quantity` int DEFAULT '0' COMMENT '安全庫存量',
  `remind_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_items_group_id` (`group_id`),
  KEY `idx_items_category_id` (`category_id`),
  KEY `idx_items_created_by_id` (`created_by_id`),
  KEY `idx_items_location_id` (`location_id`),
  KEY `idx_items_expire_date` (`expire_date`),
  CONSTRAINT `fk_items_category_id` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_items_created_by_id` FOREIGN KEY (`created_by_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_items_location_id` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家庭物品';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `locations`
--

DROP TABLE IF EXISTS `locations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `locations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'location ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'location名稱',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'location圖示',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_locations_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='存放位置';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `medicines`
--

DROP TABLE IF EXISTS `medicines`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medicines` (
  `id` int NOT NULL AUTO_INCREMENT,
  `group_id` int NOT NULL COMMENT '所屬群組',
  `user_id` int NOT NULL COMMENT '建立者',
  `name` varchar(100) NOT NULL COMMENT '藥品名稱',
  `medicine_type` varchar(50) DEFAULT NULL COMMENT '藥品類型：錠劑/膠囊/藥水/藥膏/眼藥水/其他',
  `quantity` int DEFAULT '0' COMMENT '目前數量',
  `unit` varchar(20) DEFAULT NULL COMMENT '單位：顆/包/瓶/盒/條',
  `safe_quantity` int NOT NULL DEFAULT '0' COMMENT '安全庫存量',
  `purchase_date` date DEFAULT NULL COMMENT '購買日期',
  `expire_date` date NOT NULL COMMENT '藥品到期日',
  `dosage` varchar(100) DEFAULT NULL COMMENT '劑量，例如：一次 1 顆',
  `usage_method` varchar(255) DEFAULT NULL COMMENT '用法，例如：飯後服用、睡前使用',
  `location` varchar(100) DEFAULT NULL COMMENT '存放位置，例如：藥箱、冰箱',
  `source` varchar(100) DEFAULT NULL COMMENT '來源，例如：診所、藥局、醫院',
  `notify` tinyint(1) DEFAULT '1' COMMENT '是否提醒',
  `note` varchar(500) DEFAULT NULL COMMENT '備註',
  `status` varchar(20) DEFAULT '正常' COMMENT '正常/即將到期/已到期/庫存不足',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `unit_price` int NOT NULL DEFAULT '0' COMMENT '單價',
  `price` int NOT NULL DEFAULT '0' COMMENT '總價',
  `remind_message` varchar(255) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知 ID',
  `group_user_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接收通知的人，多人',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知標題',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知內容',
  `type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知類型',
  `related_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '關聯資料類型',
  `related_id` bigint DEFAULT NULL COMMENT '關聯資料 ID',
  `send_channel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知方式（email）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
  PRIMARY KEY (`id`),
  KEY `idx_notifications_related` (`related_type`,`related_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notify`
--

DROP TABLE IF EXISTS `notify`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notify` (
  `notify_id` bigint NOT NULL AUTO_INCREMENT,
  `send_id` bigint NOT NULL,
  `get_user_id` bigint NOT NULL,
  `content` varchar(225) DEFAULT NULL,
  `type` varchar(45) DEFAULT NULL,
  `is_read` tinyint NOT NULL DEFAULT '0',
  `send_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `target_group_id` bigint DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`notify_id`),
  KEY `idx_notify_user_read` (`get_user_id`,`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `shopping_list_items`
--

DROP TABLE IF EXISTS `shopping_list_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shopping_list_items` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '購物項目 ID',
  `shopping_list_id` bigint NOT NULL COMMENT '所屬購物清單',
  `created_by_id` bigint NOT NULL COMMENT '新增者',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
  `user_id` bigint DEFAULT NULL COMMENT '指定工作人',
  `category_id` bigint DEFAULT NULL COMMENT '分類',
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名稱',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '數量',
  `price` int DEFAULT NULL,
  `is_checked` tinyint NOT NULL DEFAULT '0',
  `checked_at` datetime DEFAULT NULL,
  `checked_by_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`,`shopping_list_id`),
  KEY `idx_shopping_list_items_shopping_list_id` (`shopping_list_id`),
  KEY `idx_shopping_list_items_created_by_id` (`created_by_id`),
  KEY `idx_shopping_list_items_user_id` (`user_id`),
  KEY `idx_shopping_list_items_category_id` (`category_id`),
  KEY `idx_shopping_list_items_list_id` (`shopping_list_id`),
  KEY `idx_shopping_list_items_list_checked` (`shopping_list_id`,`is_checked`),
  CONSTRAINT `fk_shopping_list_items_category_id` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_shopping_list_items_created_by_id` FOREIGN KEY (`created_by_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_shopping_list_items_shopping_list_id` FOREIGN KEY (`shopping_list_id`) REFERENCES `shopping_lists` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_shopping_list_items_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='購物項目';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `shopping_lists`
--

DROP TABLE IF EXISTS `shopping_lists`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shopping_lists` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '購物清單 ID',
  `group_id` bigint DEFAULT NULL COMMENT '所屬群組',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '清單名稱',
  `created_by_id` bigint NOT NULL COMMENT '建立者',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
  `check` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已購買',
  PRIMARY KEY (`id`),
  KEY `idx_shopping_lists_created_by_id` (`created_by_id`),
  KEY `idx_shopping_lists_group_id` (`group_id`),
  CONSTRAINT `fk_shopping_lists_created_by_id` FOREIGN KEY (`created_by_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='購物清單';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `subscriptions`
--

DROP TABLE IF EXISTS `subscriptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscriptions` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '訂閱 ID',
  `group_id` bigint NOT NULL COMMENT '所屬家庭',
  `user_id` bigint NOT NULL COMMENT '訂閱者',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '訂閱名稱',
  `price` int NOT NULL DEFAULT '0' COMMENT '每期費用',
  `billing_cycle` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '週期（月繳/年繳）',
  `next_billing_date` date NOT NULL COMMENT '下次扣款日',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
  `purchase_date` date DEFAULT NULL COMMENT '購買日期',
  `trial_end_date` date DEFAULT NULL COMMENT '試用結束日期',
  `notify` tinyint(1) DEFAULT '1' COMMENT '是否開啟提醒',
  `note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '備註',
  `remind_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_subscriptions_group_id` (`group_id`),
  KEY `idx_subscriptions_user_id` (`user_id`),
  KEY `idx_subscriptions_next_billing_date` (`next_billing_date`),
  CONSTRAINT `fk_subscriptions_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='訂閱服務';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '使用者唯一 ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '使用者名稱',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登入 Email',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '加密後密碼',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '頭像圖片網址',
  `is_notify_by_enddate` tinyint(1) NOT NULL DEFAULT '1',
  `is_notify_by_email` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否開啟通知',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
  `login_item_list_page_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `login_calendar_page_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `login_expense_page_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `email_verify` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_users_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='使用者';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `warranties`
--

DROP TABLE IF EXISTS `warranties`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `warranties` (
  `id` int NOT NULL AUTO_INCREMENT,
  `group_id` int NOT NULL,
  `user_id` int NOT NULL,
  `product_name` varchar(100) NOT NULL,
  `brand` varchar(100) DEFAULT NULL,
  `model` varchar(100) DEFAULT NULL,
  `serial_number` varchar(100) DEFAULT NULL,
  `purchase_date` date NOT NULL,
  `warranty_end_date` date NOT NULL,
  `store_name` varchar(100) DEFAULT NULL,
  `price` int DEFAULT '0',
  `notify` tinyint(1) DEFAULT '1',
  `note` varchar(500) DEFAULT NULL,
  `status` varchar(20) DEFAULT '正常',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `remind_message` varchar(255) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'family_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;



-- =====================================================================
-- 預設初始化資料 (INSERT DATA)
-- =====================================================================

-- 暫時關閉外鍵檢查，確保匯入順序不會干擾
SET FOREIGN_KEY_CHECKS = 0;

-- 1. 預設分類資料 (categories)
LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` (`category_id`, `category_name`, `icon`) VALUES
(1, '食品', 'food'),
(2, '藥品', 'medicine'),
(3, '日常用品', 'daily_supplies'),
(4, '訂閱', 'subscription'),
(5, '清潔用品', 'cleaning_supplies'),
(6, '保固', 'warranty');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

-- 2. 預設位置資料 (locations)
LOCK TABLES `locations` WRITE;
/*!40000 ALTER TABLE `locations` DISABLE KEYS */;
INSERT INTO `locations` (`id`, `name`, `icon`) VALUES
(1, '冰箱', 'fridge'),
(2, '櫥櫃', 'cabinet'),
(3, '藥箱', 'medicine_box'),
(4, '其他', 'other');
/*!40000 ALTER TABLE `locations` ENABLE KEYS */;
UNLOCK TABLES;

-- 恢復外鍵檢查
SET FOREIGN_KEY_CHECKS = 1;

-- Dump completed on 2026-06-28 14:13:24
