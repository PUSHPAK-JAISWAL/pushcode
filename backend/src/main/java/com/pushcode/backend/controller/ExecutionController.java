package com.pushcode.backend.controller;

import com.pushcode.backend.dto.ExecutionRequest;
import com.pushcode.backend.dto.ExecutionResponse;
import com.pushcode.backend.service.ExecutionService;
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

    @Autowired
    public ExecutionController(ExecutionService executionService,
                               TerminalSessionManager terminalSessionManager) {
        this.executionService = executionService;
        this.terminalSessionManager = terminalSessionManager;
    }

    @PostMapping
    public ExecutionResponse execute(@RequestBody ExecutionRequest executionRequest) throws IOException {

        String sessionId = UUID.randomUUID().toString();

        Process process = executionService.execute(
                executionRequest.getLanguage(),
                executionRequest.getCode(),
                sessionId
        );

        terminalSessionManager.create(sessionId,process);

        return new ExecutionResponse(sessionId);

    }

}
