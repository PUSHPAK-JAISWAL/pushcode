package com.pushcode.backend.websocket;

import com.pushcode.backend.exceptions.SessionNotFoundException;
import com.pushcode.backend.model.ExecutionSession;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TerminalSessionManager {

    private final Map<String, ExecutionSession> sessions = new ConcurrentHashMap<>();

    public void create(String id, Process process) {
        sessions.put(id, new ExecutionSession(id, process));
    }

    public ExecutionSession get(String id) {
        ExecutionSession session = sessions.get(id);

        if (session == null) {
            throw new SessionNotFoundException("Session not found: " + id);
        }

        return session;
    }

    public void remove(String id) {
        sessions.remove(id);
    }

}
