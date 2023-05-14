package com.isxcode.star.api.utils;

import com.isxcode.star.api.exception.SparkYunException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
public class AesUtils {

  /**
   * 对称加密.
   */
  public static String encrypt(String key, String data) throws SparkYunException {

    try {
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(
        Cipher.ENCRYPT_MODE,
        new SecretKeySpec(Arrays.copyOf(key.getBytes(), 1 << 5), "AES"));
      return Base64.getEncoder()
        .encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException
             | InvalidKeyException
             | NoSuchPaddingException
             | BadPaddingException
             | IllegalBlockSizeException e) {
      throw new SparkYunException(e.getMessage());
    }
  }

  /**
   * 对称解密.
   */
  public static String decrypt(String key, String data) {

    try {
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(
        Cipher.DECRYPT_MODE,
        new SecretKeySpec(Arrays.copyOf(key.getBytes(), 1 << 5), "AES"));
      return new String(cipher.doFinal(Base64.getDecoder().decode(data)), StandardCharsets.UTF_8);
    } catch (NoSuchPaddingException
             | NoSuchAlgorithmException
             | InvalidKeyException
             | BadPaddingException
             | IllegalBlockSizeException e) {
      throw new SparkYunException(e.getMessage());
    }
  }

  public static String decryptByte(String key, String data) {

    try {
      Cipher cipher = Cipher.getInstance("AES/CBC/ISO10126Padding");
      IvParameterSpec paramSpec = new IvParameterSpec("".getBytes());
      cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"), paramSpec);
      return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchPaddingException
             | NoSuchAlgorithmException
             | InvalidKeyException
             | BadPaddingException
             | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
      throw new SparkYunException(e.getMessage());
    }
  }
}
