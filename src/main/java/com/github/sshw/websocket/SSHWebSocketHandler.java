package com.github.sshw.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.TextWebSocketHandlerAdapter;

public class SSHWebSocketHandler extends TextWebSocketHandlerAdapter {

    protected final Logger  log = LoggerFactory.getLogger(getClass());

	private final SSHSession sshSession;
	
	@Autowired
	public SSHWebSocketHandler(SSHSession sshSession) {
		this.sshSession = sshSession;
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		//String echoMessage = this.sshService.command(message.getPayload());
        //session.sendMessage(new TextMessage(echoMessage));
	    log.info(message.getPayload());
	    sshSession.getSSHOutput().write((message.getPayload() + '\n').getBytes());
	    sshSession.getSSHOutput().flush();
	}

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("connection established");
        sshSession.setWebSocketSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("connection closed: {}", status.getReason());
        sshSession.logout();
    }

	
	
	
}
