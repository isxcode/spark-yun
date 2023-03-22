package com.isxcode.star.backend.module.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/*
 * 只负责数据库对象映射
 *
 * @ispong
 */
@Data
@Entity
@Table(name = "sy_users")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class UserEntity {

    @Id
    private String id;

    private String account;

    private String password;

    private String email;
}
