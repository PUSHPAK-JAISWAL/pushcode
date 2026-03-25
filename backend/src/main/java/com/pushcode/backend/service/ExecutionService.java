package com.pushcode.backend.service;

import com.pushcode.backend.enums.Language;
import com.pushcode.backend.model.ExecutionSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ExecutionService {

    private final DockerCommandBuilder builder;
    private final ProcessExecutor executor;

    public ExecutionService(DockerCommandBuilder builder,
                            ProcessExecutor executor) {
        this.builder = builder;
        this.executor = executor;
    }

    public ExecutionSession execute(Language lang, String code, String sessionId) throws IOException {

        List<String> command = builder.build(lang, code, sessionId);

        Process process = executor.start(command);

        return new ExecutionSession(sessionId, process);
    }
}
