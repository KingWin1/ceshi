package com.community.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 门店实体类
 */
@Data
@TableName("store")
@ApiModel(description = "门店")
public class Store {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "门店ID")
    private Integer storeId;

    @ApiModelProperty(value = "门店名称")
    private String storeName;

    @ApiModelProperty(value = "分类ID")
    private Integer categoryId;

    @ApiModelProperty(value = "联系电话")
    private String contactPhone;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "营业开始时间")
    private Date openTime;

    @ApiModelProperty(value = "营业结束时间")
    private Date closeTime;

    @ApiModelProperty(value = "浏览次数")
    private Integer viewCount;

    @ApiModelProperty(value = "状态：1-营业中 2-已下架")
    private Integer status;

    @ApiModelProperty(value = "推荐：1-推荐 2-普通")
    private Integer isRecommend;

    @ApiModelProperty(value = "门店logo地址")
    private String logoUrl;

    @ApiModelProperty(value = "门店图片地址")
    private String imageUrl;

    @ApiModelProperty(value = "门店介绍")
    private String introduction;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "经度")
    private BigDecimal longitude;

    @ApiModelProperty(value = "纬度")
    private BigDecimal latitude;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
