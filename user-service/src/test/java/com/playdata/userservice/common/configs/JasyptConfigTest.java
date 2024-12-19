package com.playdata.userservice.common.configs;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JasyptConfigTest {

    @Autowired
    @Qualifier("jasyptEncrypter")
    StringEncryptor encryptor;

    @Test
    @DisplayName("redis의 host와 port를 암호화 해 보자")
    void stringEncypt() {
        String host = "localhost";
        String port = "6379";
        System.out.println("\n\n\n");
        System.out.println(jasyptEncoding(host));
        System.out.println(jasyptEncoding(port));
        System.out.println("\n\n\n");
    }

    // 암호화 로직을 메서드로 따로 추출
    public String jasyptEncoding(String value) {
        return encryptor.encrypt(value);
    }

}














