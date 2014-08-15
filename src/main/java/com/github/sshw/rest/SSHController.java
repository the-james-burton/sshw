package com.github.sshw.rest;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("service")
public class SSHController {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    SSHService             sshService;

    @Autowired
    public SSHController(SSHService sshService) {
        this.sshService = sshService;
    }

    @Deprecated
    @RequestMapping(value = "login", params = { "user", "passwd" })
    @ResponseBody
    public boolean login(@RequestParam("user") String user, @RequestParam("passwd") String passwd) {
        log.info("login");
        return sshService.login(user, passwd);
    }

    @Deprecated
    @RequestMapping(value = "logout", params = { "user" })
    @ResponseBody
    public boolean logout(@RequestParam("user") String user) {
        log.info("logout");
        return sshService.logout(user);
    }

}
