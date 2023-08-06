package com.isxcode.star.utils.md5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** md5加密工具类. */
public class Md5Utils {

  /** md5加密. */
  public static String hashStr(String input) {
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    byte[] messageDigest = md.digest(input.getBytes());

    StringBuilder hexString = new StringBuilder();
    for (byte b : messageDigest) {
      String hex = Integer.toHexString(0xFF & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }
}
