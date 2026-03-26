package com.pushcode.backend.websocket;

import com.pushcode.backend.model.ExecutionSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.*;

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
        // Use a character buffer to catch partial output (like prompts)
        try (InputStreamReader reader = new InputStreamReader(is)) {
            char[] buffer = new char[1024];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                String output = new String(buffer, 0, read);
                exec.updateActivity();

                if (ws.isOpen()) {
                    ws.sendMessage(new TextMessage(output));
                }
            }
        } catch (Exception e) {
            // Process ended or socket closed
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String sessionId = getSessionId(session);

        ExecutionSession exec = manager.get(sessionId);
        Process process = exec.getProcess();

        // 🔥 CHECK IF PROCESS IS ALIVE
        if (!process.isAlive()) {
            session.sendMessage(new TextMessage("\n[Process already terminated]\n"));
            return;
        }

        try {
            OutputStream os = process.getOutputStream();
            // REMOVE the +"\n". Just send the raw payload.
            os.write(message.getPayload().getBytes());
            os.flush();
        } catch (IOException e) {
            session.sendMessage(new TextMessage("\n[Input failed: process closed]\n"));
        }
    }

    private String getSessionId(WebSocketSession session) {
        return session.getUri().getQuery().split("=")[1];
    }
}