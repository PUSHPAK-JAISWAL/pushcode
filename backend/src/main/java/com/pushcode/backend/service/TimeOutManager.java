package com.pushcode.backend.service;
import com.pushcode.backend.model.ExecutionSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TimeOutManager {

    @Value("${execution.max-time-seconds}")
    private int maxTime;

    @Value("${execution.idle-timeout-seconds}")
    private int idleTimeout;

    public void monitor(ExecutionSession session) {

        Process process = session.getProcess();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        long startTime = System.currentTimeMillis();

        scheduler.scheduleAtFixedRate(() -> {

            if (!process.isAlive()) {
                scheduler.shutdown();
                return;
            }

            long now = System.currentTimeMillis();

            if ((now - startTime) > maxTime * 1000L) {
                process.destroyForcibly();
                scheduler.shutdown();
                return;
            }

            if ((now - session.getLastActivityTime()) > idleTimeout * 1000L) {
                process.destroyForcibly();
                scheduler.shutdown();
            }

        }, 1, 1, TimeUnit.SECONDS);
    }
}
