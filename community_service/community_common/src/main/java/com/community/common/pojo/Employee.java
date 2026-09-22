package com.community.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 员工实体类
 */
@Data
@TableName("employee")
@ApiModel(description = "员工")
public class Employee {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "员工ID")
    private Integer employeeId;

    @ApiModelProperty(value = "员工姓名")
    private String employeeName;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "性别：1-男 2-女 3-未知")
    private Integer gender;

    @ApiModelProperty(value = "部门ID")
    private Integer deptId;

    @ApiModelProperty(value = "岗位ID")
    private Integer positionId;

    @ApiModelProperty(value = "状态：1-在职 2-离职")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
