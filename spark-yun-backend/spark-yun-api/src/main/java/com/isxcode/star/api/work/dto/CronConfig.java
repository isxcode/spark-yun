package com.isxcode.star.api.work.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 调度配置信息.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CronConfig {

    private String setMode;

    private boolean enable;

    /**
     * 小时开始.
     */
    private LocalTime startDate;

    /**
     * 小时间隔.
     */
    private Integer hourNum;

    /**
     * 小时结束.
     */
    private LocalTime endDate;

    private String cron;

    private List<LocalDate> workDate;

    /**
     * 间隔类型.
     */
    private String range;

    /**
     * 分钟开始.
     */
    private LocalTime startDateMin;

    /**
     * 时间间隔分钟.
     */
    private Integer minNum;

    /**
     * 分钟结束.
     */
    private LocalTime endDateMin;

    /**
     * 每日定时.
     */
    private LocalTime scheduleDate;

    /**
     * 每周.
     */
    private String weekDate;

    /**
     * 每月.
     */
    private String monthDay;

    /**
     * 调度类型. 1. 单一调度 2. 离散调度
     */
    private String type;
}
