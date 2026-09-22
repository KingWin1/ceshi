package com.community.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 房屋居民认证查询参数DTO
 */
@Data
@ApiModel(description = "认证信息查询参数")
public class HouseResidentAuthQueryDTO {

    @ApiModelProperty(value = "审核状态：1-待审核 2-已通过 3-已拒绝")
    private Integer auditStatus;

    @ApiModelProperty(value = "楼栋ID")
    private Integer buildingId;

    @ApiModelProperty(value = "单元号")
    private String unitNo;

    @ApiModelProperty(value = "房间号")
    private String houseNumber;

    @ApiModelProperty(value = "姓名/身份证/电话/备注（模糊查询）")
    private String keyword;

    @ApiModelProperty(value = "创建时间-开始日期")
    private String startDate;

    @ApiModelProperty(value = "创建时间-结束日期")
    private String endDate;

    @ApiModelProperty(value = "当前页码", required = true)
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小", required = true)
    private Integer pageSize = 10;
}
