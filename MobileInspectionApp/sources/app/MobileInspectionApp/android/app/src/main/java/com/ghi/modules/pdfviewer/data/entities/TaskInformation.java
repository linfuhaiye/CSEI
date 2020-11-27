package com.ghi.modules.pdfviewer.data.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 任务信息
 *
 * @author etrit
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class TaskInformation {
    /**
     * 设备号ID
     */
    private String eqpId;

    /**
     * 单位ID
     */
    private String unitId;

    /**
     * 设备号
     */
    private String eqpCode;

    /**
     * 使用单位名称
     */
    private String useUnitName;

    /**
     * 设备号编号
     */
    private String eqpMode;

    /**
     * 设备工厂编号
     */
    private String factoryCode;

    /**
     * 远程设备号data的路径
     */
    private String dataPath;

    /**
     * testlog.pdf模板号
     */
    private String logModule;

    /**
     * report.pdf模板号
     */
    private String repModule;

    /**
     * 业务类型
     */
    private String eqpType;

    /**
     * 复检状态
     */
    private String repIs;

    /**
     * 楼盘ID
     */
    private String buildId;

    /**
     * 可知
     */
    private String secuDeptId;

    /**
     * 限速器校验
     */
    private String ifLimit;

    /**
     * 审核人ID
     */
    private String chekUserId;

    /**
     * 审核日期
     */
    private String chekDate;

    /**
     * 审批人ID
     */
    private String apprUserId;

    /**
     * ispType
     */
    private String ispType;

    /**
     * 任务日期
     */
    private String taskDate;

    /**
     * 制造日期
     */
    private String makeDate;

    /**
     * 设计使用年限
     */
    private String designUserOverYear;

    /**
     * 检验日期
     */
    private String ispDate;

    /**
     * 操作类型
     */
    private String opeType;

    /**
     * 报告号
     */
    private String reportCode;
}
