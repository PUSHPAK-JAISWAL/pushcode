package com.pushcode.backend.service;

import com.pushcode.backend.enums.Language;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;

@Component
public class DockerCommandBuilder {

    @Value("${execution.memory-limit}")
    private String memory;

    @Value("${execution.cpu-limit}")
    private String cpu;

    @Value("${docker.images.python}")
    private String pythonImage;

    @Value("${docker.images.c}")
    private String cImage;

    @Value("${docker.images.cpp}")
    private String cppImage;

    @Value("${docker.images.java}")
    private String javaImage;

    public List<String> build(Language lang,String code,String sessionId) {

        String image = switch (lang) {
            case JAVA -> javaImage;
            case PYTHON -> pythonImage;
            case C -> cImage;
            case CPP -> cppImage;
        };

        String encodedCode = Base64.getEncoder().encodeToString(code.getBytes());

        return List.of(
                "docker", "run", "--rm", "-i",
                "--network", "none",
                "--memory", memory,
                "--cpus", cpu,
                "--pids-limit", "64",
                "--read-only",

                "--tmpfs", "/tmp:rw,exec,size=64m",
                "--security-opt", "no-new-privileges",
                "-e", "CODE=" + encodedCode,
                "-e", "PYTHONUNBUFFERED=1", // Fixes Python lag
                "-e", "TERM=dumb",         // Fixes "Not a TTY" warnings
                "--name", "exec-" + sessionId,
                image
        );

    }

}
