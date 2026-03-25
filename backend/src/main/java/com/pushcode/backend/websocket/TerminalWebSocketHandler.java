package com.pushcode.backend.websocket;

import com.pushcode.backend.websocket.TerminalSessionManager;
import com.pushcode.backend.model.ExecutionSession;
import com.pushcode.backend.util.StreamGobbler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.OutputStream;

@Component
public class TerminalWebSocketHandler extends TextWebSocketHandler {

    private final TerminalSessionManager manager;

    public TerminalWebSocketHandler(TerminalSessionManager manager) {
        this.manager = manager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        String sessionId = getSessionId(session);

        ExecutionSession exec = manager.get(sessionId);
        Process process = exec.getProcess();

        new Thread(new StreamGobbler(process.getInputStream(), session)).start();
        new Thread(new StreamGobbler(process.getErrorStream(), session)).start();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String sessionId = getSessionId(session);

        Process process = manager.get(sessionId).getProcess();

        OutputStream os = process.getOutputStream();
        os.write(message.getPayload().getBytes());
        os.flush();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = getSessionId(session);
        manager.remove(sessionId);
    }

    private String getSessionId(WebSocketSession session) {
        return session.getUri().getQuery().split("=")[1];
    }
}