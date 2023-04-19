package com.isxcode.star.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isxcode.star.api.exception.SparkYunException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JwtUtils {

  private static Key key;

  public void init() {

    JwtUtils.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
  }

  /**
   * jwt加密.
   */
  public static String encrypt(String aesKey, Object obj, String jwtKey, Integer minutes)
    throws SparkYunException {

    Map<String, Object> claims = new HashMap<>(1);

    try {

      String claimsStr = new ObjectMapper().writeValueAsString(obj);
      if (aesKey != null) {
        claimsStr = AesUtils.encrypt(aesKey, claimsStr);
      }
      claims.put("CLAIM", claimsStr);

      JwtBuilder jwtBuilder = Jwts.builder();
      if (jwtKey == null) {
        jwtBuilder = jwtBuilder.signWith(key);
      } else {
        jwtBuilder =
          jwtBuilder.signWith(Keys.hmacShaKeyFor(Arrays.copyOf(jwtKey.getBytes(), 1 << 5)));
      }

      jwtBuilder.setClaims(claims).setIssuedAt(new Date()).setId(String.valueOf(UUID.randomUUID()));

      if (minutes > 0) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, minutes);
        jwtBuilder = jwtBuilder.setExpiration(calendar.getTime());
      }

      return jwtBuilder.compact();

    } catch (JsonProcessingException e) {

      throw new SparkYunException(e.getMessage());
    }
  }

  /**
   * jwt解密.
   */
  public static <A> A decrypt(String aesKey, String jwtString, String jwtKey, Class<A> targetClass) {

    JwtParserBuilder jwtParserBuilder = Jwts.parserBuilder();

    if (jwtKey == null) {
      jwtParserBuilder = jwtParserBuilder.setSigningKey(key);
    } else {
      jwtParserBuilder =
        jwtParserBuilder.setSigningKey(
          Keys.hmacShaKeyFor(Arrays.copyOf(jwtKey.getBytes(), 1 << 5)));
    }

    String claimStr =
      jwtParserBuilder
        .build()
        .parseClaimsJws(jwtString)
        .getBody()
        .get("CLAIM", String.class);

    String targetJsonStr = claimStr;
    if (aesKey != null) {
      targetJsonStr = AesUtils.decrypt(aesKey, claimStr);
    }

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(targetJsonStr, targetClass);
    } catch (JsonProcessingException e) {
      throw new SparkYunException(e.getMessage());
    }
  }
}
