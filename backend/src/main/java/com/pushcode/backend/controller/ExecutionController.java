package com.pushcode.backend.controller;

import com.pushcode.backend.dto.ExecutionRequest;
import com.pushcode.backend.dto.ExecutionResponse;
import com.pushcode.backend.exceptions.ExecutionException;
import com.pushcode.backend.model.ExecutionSession;
import com.pushcode.backend.service.ExecutionService;
import com.pushcode.backend.service.TimeOutManager;
import com.pushcode.backend.websocket.TerminalSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/execute")
public class ExecutionController {

    private final ExecutionService executionService;
    private final TerminalSessionManager terminalSessionManager;
    private final TimeOutManager timeOutManager;

    @Autowired
    public ExecutionController(ExecutionService executionService,
                               TerminalSessionManager terminalSessionManager,
                               TimeOutManager timeOutManager) {
        this.executionService = executionService;
        this.terminalSessionManager = terminalSessionManager;
        this.timeOutManager = timeOutManager;
    }

    @PostMapping
    public ExecutionResponse execute(@RequestBody ExecutionRequest request) throws IOException, ExecutionException {

        String sessionId = UUID.randomUUID().toString();

        ExecutionSession session = executionService.execute(
                request.getLanguage(),
                request.getCode(),
                sessionId
        );

        terminalSessionManager.create(sessionId, session.getProcess());

        timeOutManager.monitor(session);

        return new ExecutionResponse(sessionId);
    }

}
