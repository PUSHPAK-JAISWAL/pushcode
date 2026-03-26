package com.pushcode.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class ExecutionSession {
    private String sessionId;
    private Process process;
    private volatile long lastActivityTime;

    public ExecutionSession(String sessionId, Process process) {
        this.sessionId = sessionId;
        this.process = process;
        this.lastActivityTime = System.currentTimeMillis();
    }

    public void updateActivity() {
        this.lastActivityTime = System.currentTimeMillis();
    }

}
