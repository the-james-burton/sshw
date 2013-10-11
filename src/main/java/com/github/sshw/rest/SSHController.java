package com.github.sshw.rest;

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
	
    protected final Logger  log = LoggerFactory.getLogger(getClass());

	SSHService sshService;
	
	@Autowired
	public SSHController(SSHService sshService) {
		this.sshService = sshService;
	}

    @RequestMapping(value="login", params={"user", "passwd"})
    @ResponseBody
    public boolean login(@RequestParam("user") String user, @RequestParam("passwd") String passwd) {
        log.info("login");
        return sshService.login(user, passwd);
    }
    
    @RequestMapping(value="logout")
    @ResponseBody
    public boolean logout() {
        log.info("logout");
        return sshService.logout();
    }
    
}
