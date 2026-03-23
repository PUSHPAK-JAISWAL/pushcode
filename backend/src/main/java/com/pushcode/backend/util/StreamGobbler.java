package com.pushcode.backend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler implements Runnable {

    private final InputStream inputStream;

    private final WebSocketSession session;

    @Autowired
    public StreamGobbler(InputStream inputStream, WebSocketSession session) {
        this.inputStream = inputStream;
        this.session = session;
    }

    @Override
    public void run() {
        try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                session.sendMessage(new TextMessage(line +"\n"));
            }
        } catch (Exception ignored) {
            //This is empty for now
        }
    }
}
