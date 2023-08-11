package com.isxcode.star.common.utils.jwt;

import cn.hutool.crypto.SecureUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isxcode.star.backend.api.base.exceptions.SparkYunException;
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

/** jwt加密工具类. */
public class JwtUtils {

  private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

  /** jwt加密. */
  public static String encrypt(String aesKey, Object obj, String jwtKey, Integer minutes) {

    Map<String, Object> claims = new HashMap<>(1);

    String claimsStr = null;
    try {
      claimsStr = new ObjectMapper().writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new SparkYunException("jwt加密异常");
    }
    if (aesKey != null) {
      claimsStr = SecureUtil.aes(Arrays.copyOf(aesKey.getBytes(), 1 << 5)).encryptBase64(claimsStr);
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
  }

  /** jwt解密. */
  public static <A> A decrypt(
      String aesKey, String jwtString, String jwtKey, Class<A> targetClass) {

    JwtParserBuilder jwtParserBuilder = Jwts.parserBuilder();

    if (jwtKey == null) {
      jwtParserBuilder = jwtParserBuilder.setSigningKey(key);
    } else {
      jwtParserBuilder =
          jwtParserBuilder.setSigningKey(
              Keys.hmacShaKeyFor(Arrays.copyOf(jwtKey.getBytes(), 1 << 5)));
    }

    String claimStr =
        jwtParserBuilder.build().parseClaimsJws(jwtString).getBody().get("CLAIM", String.class);

    String targetJsonStr = claimStr;
    if (aesKey != null) {
      targetJsonStr = SecureUtil.aes(Arrays.copyOf(aesKey.getBytes(), 1 << 5)).decryptStr(claimStr);
    }

    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.readValue(targetJsonStr, targetClass);
    } catch (JsonProcessingException e) {
      throw new SparkYunException("jwt解密异常");
    }
  }
}
