package com.ppp.common;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.junit.jupiter.api.Test;

public class JasyptTest {

    int poolSize = 2;
    String algorithm = "PBEWithMD5AndDES";
    String stringOutputType = "base64";
    int keyObtentionIterations = 10000;
    String password = "..."; //jasypt-pass 에 있는 값

    @Test
    void testJasyptEncryptionAndDecryption() {

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setPoolSize(poolSize);
        encryptor.setAlgorithm(algorithm);
        encryptor.setPassword(password);
        encryptor.setStringOutputType(stringOutputType);
        encryptor.setKeyObtentionIterations(keyObtentionIterations);

        String plain = "변경할 값";

        // 암호화
        String encryptedValue = encryptor.encrypt(plain);

        // 복호화
        String decryptedValue = encryptor.decrypt(encryptedValue);


        System.out.println(encryptedValue);

        System.out.println(decryptedValue);
    }
}
