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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.PublicKey;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Shell;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.socket.WebSocketSession;

import com.github.sshw.security.SSHAuthenticationException;

public class SSHSessionImpl implements SSHSession {

    protected final Logger   log = LoggerFactory.getLogger(getClass());

    private SSHClient        ssh;
    private Session          session;
    private Shell            shell;
    private String           username;
    
    private BufferedReader   input;
    private OutputStream     output;

    private SSHSessionOutput sender;
    private Thread           thread;

    @Override
    public boolean login(String username, String password) throws AuthenticationException {
        try {
            logout();
            // ssh.authPublickey(System.getProperty("user.name"));
            log.info("new SSHClient");
            ssh = new SSHClient();
            log.info("verify all hosts");
            ssh.addHostKeyVerifier(new HostKeyVerifier() {
                public boolean verify(String arg0, int arg1, PublicKey arg2) {
                    return true; // don't bother verifying
                }
            });
            log.info("connecting");
            ssh.connect("127.0.0.1");
            log.info("authenticating: {}", username);
            ssh.authPassword(username, password);
            log.info("starting session");
            session = ssh.startSession();
            log.info("allocating PTY");
            session.allocateDefaultPTY();
            log.info("starting shell");
            shell = session.startShell();
            log.info("SSH session established");
            this.username = username;
            input = new BufferedReader(new InputStreamReader(shell.getInputStream()));
            output = shell.getOutputStream();
            sender = new SSHSessionOutput(input);
        } catch (Exception e) {
            log.error(e.getMessage());
            finalize();
            throw new SSHAuthenticationException(e.getMessage(), e);
        }
        return true;

    }

    @Override
    public BufferedReader getSSHInput() {
        return input;
    }

    @Override
    public OutputStream getSSHOutput() {
        return output;
    }

    @Override
    protected void finalize() {
        try {
            shell.close();
        } catch (Throwable e) {
        }
        try {
            session.close();
        } catch (Throwable e) {
        }
        try {
            ssh.disconnect();
        } catch (Throwable e) {
        }
        log.info("session finalized");
    }

    @Override
    public void setWebSocketSession(WebSocketSession session) {
        this.sender.setWebSocketSession(session);
        thread = new Thread(sender);
        log.info("starting sending thread");
        thread.start();
    }

    @Override
    public boolean logout() {
        log.info("logout: {}", username);
        try {
            // output.write("exit".getBytes());
            finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }

}
