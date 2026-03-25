package com.pushcode.backend.websocket;

import com.pushcode.backend.websocket.TerminalSessionManager;
import com.pushcode.backend.model.ExecutionSession;
import com.pushcode.backend.util.StreamGobbler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

        new Thread(() -> stream(process.getInputStream(), session, exec)).start();

        new Thread(() -> stream(process.getErrorStream(), session, exec)).start();
    }

    private void stream(InputStream is, WebSocketSession ws, ExecutionSession exec) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            while ((line = reader.readLine()) != null) {

                exec.updateActivity(); // 🔥 IMPORTANT

                ws.sendMessage(new TextMessage(line + "\n"));
            }

        } catch (Exception ignored) {}
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String sessionId = getSessionId(session);

        ExecutionSession exec = manager.get(sessionId);
        Process process = exec.getProcess();


        exec.updateActivity();

        OutputStream os = process.getOutputStream();
        os.write(message.getPayload().getBytes());
        os.flush();
    }

    private String getSessionId(WebSocketSession session) {
        return session.getUri().getQuery().split("=")[1];
    }
}