//package EventListener;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//
//import com.example.Family_life_backend.controller.ChatWebSocketController;
//
//import Manager.WsSessionManager;
//
//@Component
//public class WebSocketEventListener {
//
//    @Autowired
//    private WsSessionManager sessionManager;
//
//    @Autowired
//    private ChatWebSocketController chatController;
//
//    @EventListener
//    public void handleDisconnect(SessionDisconnectEvent event) {
//
//        String sessionId = event.getSessionId();
//
//        sessionManager.removeSession(sessionId);
//
//        chatController.broadcastAllOnline();
//    }
//}
