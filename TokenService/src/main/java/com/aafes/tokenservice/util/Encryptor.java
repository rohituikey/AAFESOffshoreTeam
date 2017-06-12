package com.aafes.tokenservice.util;

import org.keyczar.Crypter;
import org.keyczar.exceptions.KeyczarException;

public class Encryptor {

    private final String keysPath;

    public Encryptor(String keysPath, String logConfigPath) {
        this.keysPath = keysPath;
        System.setProperty("log4j.configuration", logConfigPath);
    }

    public String decrypt(String ciphertext) {
        String plaintext = null;
        try {
            Crypter crypter = new Crypter(keysPath);
            plaintext = crypter.decrypt(ciphertext);
        } catch (KeyczarException ex) {
            log("Keyczar is having trouble decrypting.");
            log(ex.toString());
        }
        return plaintext;
    }

    public String encrypt(String plaintext) {
        String ciphertext = null;
        try {
            Crypter crypter = new Crypter(keysPath);
            ciphertext = crypter.encrypt(plaintext);
        } catch (KeyczarException ex) {
            log("Keyczar is having trouble encrypting.");
            log(ex.toString());
        }
        return ciphertext;
    }

    public byte[] decrypt(byte[] cipherBytes) {
        byte[] plainBytes = null;
        try {
            Crypter crypter = new Crypter(keysPath);
            plainBytes = crypter.decrypt(cipherBytes);
        } catch (KeyczarException ex) {
            log("Keyczar is having trouble decrypting a byte array.");
            log(ex.toString());
        }
        return plainBytes;
    }

    public byte[] encrypt(byte[] plainBytes) {
        byte[] cipherBytes = null;
        try {
            Crypter crypter = new Crypter(keysPath );
            cipherBytes = crypter.encrypt(plainBytes);
        } catch (KeyczarException ex) {
            log("Keyczar is having trouble encrypting a byte array.");
            log(ex.toString());
        }
        return cipherBytes;
    }

    private void log(String s) {
        System.err.println(s);
    }

}
