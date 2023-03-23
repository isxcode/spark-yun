package com.isxcode.star.backend.module.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/** 只负责数据库对象映射. */
@Data
@Entity
@Table(name = "sy_users")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class UserEntity {

  @Id private String id;

  private String account;

  private String password;

  private String email;
}
