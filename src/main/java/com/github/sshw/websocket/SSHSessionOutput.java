package com.github.sshw.websocket;

import java.io.BufferedReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class SSHSessionOutput implements Runnable {

    protected final Logger         log = LoggerFactory.getLogger(getClass());

    private final BufferedReader   in;
    private final WebSocketSession out;

    public SSHSessionOutput(BufferedReader in, WebSocketSession out) {
        log.info("created");
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        log.info("started");
        while (true) {
            try {
                // readLine will block if nothing to send
                out.sendMessage(new TextMessage(in.readLine()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
