package com.github.sshw.crypt;

import java.security.Provider;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptionBeanTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Ignore
    @Test
    public void testShowProviders() {
        Provider BC = new BouncyCastleProvider();
        Security.addProvider(BC);
        for (Provider provider : Security.getProviders()) {
            for (Provider.Service service : provider.getServices()) {
                log.info("{}: {}", provider.getName(), service.getAlgorithm());
            }
        }
    }

    @Test
    public void testEncryptionBean() throws Exception {
        EncryptionBean eb = new EncryptionBean();
        String key = "mypassword";
        String encyrpted = eb.encrypt("hello world!", key);
        String decrypted = eb.decrypt(encyrpted, key);
        log.info(decrypted);
    }


}
