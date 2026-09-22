package com.community.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 公告实体类
 */
@Data
@TableName("notice")
@ApiModel(description = "公告")
public class Notice {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "公告ID")
    private Integer noticeId;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "浏览次数")
    private Integer viewCount;

    @ApiModelProperty(value = "状态：1-已发布 2-已下架")
    private Integer status;

    @ApiModelProperty(value = "封面图地址")
    private String coverUrl;

    @ApiModelProperty(value = "是否置顶：1-是 2-否")
    private Integer isTop;

    @ApiModelProperty(value = "发布时间")
    private Date publishTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
