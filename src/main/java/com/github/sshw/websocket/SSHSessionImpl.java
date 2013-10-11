/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.sshw.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.PublicKey;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Shell;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

public class SSHSessionImpl implements SSHSession {

    protected final Logger   log = LoggerFactory.getLogger(getClass());

    private SSHClient        ssh;
    private Session          session;
    private Shell            shell;

    private BufferedReader   input;
    private OutputStream     output;

    private WebSocketSession socket;
    private SSHSessionOutput sender;
    private Thread           thread;

    @Override
    public boolean login(String username, String password) {
        try {
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
            ssh.connect("styletools");
            log.info("authenticating: {}", username);
            ssh.authPassword(username, password);
            log.info("starting session");
            session = ssh.startSession();
            log.info("allocating PTY");
            session.allocateDefaultPTY();
            log.info("starting shell");
            shell = session.startShell();
            log.info("started");
            input = new BufferedReader(new InputStreamReader(shell.getInputStream()));
            output = shell.getOutputStream();
            sender = new SSHSessionOutput(input, socket);
            thread = new Thread(sender);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                log.info("disconnect");
                ssh.disconnect();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return false;
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
    protected void finalize() throws Throwable {
        shell.close();
        session.close();
        ssh.disconnect();
    }

    @Override
    public void setWebSocketSession(WebSocketSession session) {
        this.socket = session;
    }

    @Override
    public void logout() {
        try {
            output.write("exit".getBytes());
            finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
