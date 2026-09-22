package com.community.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 广告实体类
 */
@Data
@TableName("advertisement")
@ApiModel(description = "广告")
public class Advertisement {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "广告ID")
    private Integer adId;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "广告类型：1-启动页 2-首页轮播图 3-弹窗广告")
    private Integer adType;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "展示次数")
    private Integer showCount;

    @ApiModelProperty(value = "点击次数")
    private Integer clickCount;

    @ApiModelProperty(value = "状态：1-启用 2-停用")
    private Integer status;

    @ApiModelProperty(value = "图片地址")
    private String imageUrl;

    @ApiModelProperty(value = "链接类型：1-网页 2-App内页")
    private Integer linkType;

    @ApiModelProperty(value = "链接地址")
    private String linkUrl;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
