package com.github.sshw.crypt;
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

import java.security.Key;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class EncryptionBean {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Bean
    public EncryptionBean publicInstance() {
        return new EncryptionBean();
    }

    public String encrypt(String message, String password) throws Exception {
        Cipher encrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Key key = keyFromPassword(password);
        IvParameterSpec ivb = new IvParameterSpec(key.getEncoded());
        encrypt.init(Cipher.ENCRYPT_MODE, key, ivb);
        byte[] cb = encrypt.doFinal(message.getBytes());
        String c = Base64.encodeBase64URLSafeString(cb);
        return c;
    }

    public String decrypt(String message, String password) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Key key = keyFromPassword(password);
        byte[] cb = Base64.decodeBase64(message);
        IvParameterSpec ivb = new IvParameterSpec(key.getEncoded());
        cipher.init(Cipher.DECRYPT_MODE, key, ivb);
        String plaintext = new String(cipher.doFinal(cb));
        return plaintext;
    }

    public Key keyFromPassword(String password) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), password.getBytes(), 1000, 128);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
        return secret;
    }

}
