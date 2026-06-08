package com.isxcode.spark.common.locker;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sy_locker")
public class LockerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200, nullable = false)
    private String name;

    private String box;

    @Column(name = "lock_owner")
    private String owner;

    private LocalDateTime expireTime;

    private LocalDateTime createDateTime;
}
