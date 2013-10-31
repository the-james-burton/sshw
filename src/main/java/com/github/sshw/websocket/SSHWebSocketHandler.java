package com.github.sshw.websocket;
/*

The MIT License (MIT)

Copyright (c) 2013 The Authors

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.TextWebSocketHandlerAdapter;

public class SSHWebSocketHandler extends TextWebSocketHandlerAdapter {

    protected final Logger          log = LoggerFactory.getLogger(getClass());

    @Autowired
    private SSHSessionManager sshSessionManager;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String messageText = message.getPayload();
        SSHSession sshSession = sshSessionManager.sessionsByWebsocketID.get(session.getId());
        if (sshSession == null) {
            log.info("linking {}:{}", session.getId(), messageText);
            // TODO is there a better way to do this?
            // Can the client send the websocket session id and username in a REST call to link them up?
            sshSession = sshSessionManager.sessionsByUsername.get(messageText);
            sshSession.setWebSocketSession(session);
            sshSessionManager.sessionsByWebsocketID.put(session.getId(), sshSession);
        } else {
            log.debug("message in {}:{}", session.getId(), messageText);
            sshSession.getSSHOutput().write((messageText + '\n').getBytes());
            sshSession.getSSHOutput().flush();
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("websocket connection established");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("websocket connection closed: {}", status.getReason());
        SSHSession sshSession = sshSessionManager.sessionsByWebsocketID.get(session.getId());
        sshSession.logout();
        sshSessionManager.sessionsByWebsocketID.remove(session.getId());
    }

}
