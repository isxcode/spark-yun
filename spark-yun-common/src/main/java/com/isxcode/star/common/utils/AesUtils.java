package com.isxcode.star.common.utils;

import com.isxcode.star.api.exceptions.SparkYunException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

/** 对称加密工具类. */
@Slf4j
public class AesUtils {

  /** 对称加密. */
  public static String encrypt(String key, String data) {

    if (Strings.isEmpty(data)) {
      return data;
    }

    try {
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(
          Cipher.ENCRYPT_MODE, new SecretKeySpec(Arrays.copyOf(key.getBytes(), 1 << 5), "AES"));
      return Base64.getEncoder()
          .encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    } catch (Exception e) {
      throw new SparkYunException("加密异常");
    }
  }

  /** 对称解密. */
  public static String decrypt(String key, String data) {

    if (Strings.isEmpty(data)) {
      return data;
    }

    try {
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(
          Cipher.DECRYPT_MODE, new SecretKeySpec(Arrays.copyOf(key.getBytes(), 1 << 5), "AES"));
      return new String(cipher.doFinal(Base64.getDecoder().decode(data)), StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new SparkYunException("解密异常");
    }
  }
}
