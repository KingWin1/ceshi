package com.community.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 菜单实体类
 */
@Data
@TableName("menu")
@ApiModel(description = "菜单")
public class Menu {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "菜单ID")
    private Integer menuId;

    @ApiModelProperty(value = "菜单名称")
    private String menuName;

    @ApiModelProperty(value = "父级菜单ID")
    private Integer parentId;

    @ApiModelProperty(value = "路由路径")
    private String path;

    @ApiModelProperty(value = "菜单图标")
    private String icon;

    @ApiModelProperty(value = "菜单类型：1-目录 2-菜单 3-按钮")
    private Integer type;

    @ApiModelProperty(value = "排序号")
    private Integer sort;

    @ApiModelProperty(value = "是否启用：1-启用 2-禁用")
    private Integer enabled;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
