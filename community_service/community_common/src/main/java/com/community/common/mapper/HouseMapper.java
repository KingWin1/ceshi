package com.community.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.dto.HouseQueryDTO;
import com.community.common.vo.HouseDetailVO;
import com.community.common.vo.HouseVO;
import com.community.common.pojo.House;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 房屋Mapper接口
 */
@Mapper
public interface HouseMapper extends BaseMapper<House> {

    /**
     * 分页+条件查询房屋列表（多表联查）
     * @param page 分页对象
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<HouseVO> selectHousePage(Page<HouseVO> page, @Param("query") HouseQueryDTO queryDTO);

    /**
     * 根据楼栋ID、单元号、房间号查询房屋ID数组
     * @param buildingId 楼栋ID
     * @param unitNo 单元号
     * @param houseNumber 房间号
     * @return 房屋ID数组
     */
    List<Integer> selectHouseIdsByBuildingIdAndUnitNoAndHouseNumber(
            @Param("buildingId") Integer buildingId,
            @Param("unitNo") String unitNo,
            @Param("houseNumber") String houseNumber);

    /**
     * 根据房屋ID数组查询房屋信息集合
     * @param houseIds 房屋ID数组
     * @return 房屋信息集合
     */
    List<House> selectHouseListByIds(@Param("houseIds") List<Integer> houseIds);

    /**
     * 根据房屋ID查询房屋详情（联查户型室/厅/卫，用于编辑回显）
     * @param houseId 房屋ID
     * @return 房屋详情
     */
    HouseDetailVO selectHouseDetailById(@Param("houseId") Integer houseId);

    /**
     * 根据楼栋ID查询该楼栋下的房屋列表
     * @param buildingId 楼栋ID
     * @return 房屋列表
     */
    List<House> selectByBuildingId(@Param("buildingId") Integer buildingId);

    /**
     * 根据楼栋ID和单元号查询对应的房屋信息集合
     * @param buildingId 楼栋ID
     * @param unitNo 单元号
     * @return 房屋信息集合
     */
    List<House> selectByBuildingIdAndUnitNo(@Param("buildingId") Integer buildingId,
                                            @Param("unitNo") String unitNo);

    /**
     * 查询全部房屋信息
     * @return 房屋信息集合
     */
    List<House> selectAll();

    /**
     * 根据楼栋ID统计该楼栋下的房屋数量（判断楼栋是否被房屋占用）
     * @param buildingId 楼栋ID
     * @return 房屋数量
     */
    int countByBuildingId(@Param("buildingId") Integer buildingId);

    /**
     * 根据楼栋ID数组查询被房屋占用的楼栋ID集合
     * @param buildingIds 楼栋ID数组
     * @return 被占用的楼栋ID集合
     */
    List<Integer> selectOccupiedBuildingIds(@Param("buildingIds") List<Integer> buildingIds);
}
