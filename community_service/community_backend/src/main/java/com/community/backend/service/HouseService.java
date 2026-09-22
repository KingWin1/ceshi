package com.community.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.common.dto.HouseQueryDTO;
import com.community.backend.dto.HouseSaveDTO;
import com.community.common.vo.HouseDetailVO;
import com.community.common.vo.HouseVO;
import com.community.common.pojo.House;

import java.util.List;

/**
 * 房屋Service接口
 */
public interface HouseService extends IService<House> {

    /**
     * 分页+条件查询房屋列表（多表联查）
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<HouseVO> selectHousePage(HouseQueryDTO queryDTO);

    /**
     * 根据楼栋ID查询该楼栋下的房屋列表
     * @param buildingId 楼栋ID
     * @return 房屋列表
     */
    List<House> listByBuildingId(Integer buildingId);

    /**
     * 根据楼栋ID和单元号查询对应的房屋信息集合
     * @param buildingId 楼栋ID
     * @param unitNo 单元号
     * @return 房屋信息集合
     */
    List<House> listByBuildingIdAndUnitNo(Integer buildingId, String unitNo);

    /**
     * 根据房屋ID数组查询指定房屋信息
     * @param houseIds 房屋ID数组
     * @return 房屋信息集合
     */
    List<House> listByIds(List<Integer> houseIds);

    /**
     * 根据楼栋ID、单元号、房间号查询房屋ID数组
     * @param buildingId 楼栋ID
     * @param unitNo 单元号
     * @param houseNumber 房间号
     * @return 房屋ID数组
     */
    List<Integer> selectHouseIds(Integer buildingId, String unitNo, String houseNumber);

    /**
     * 查询全部房屋信息
     * @return 房屋信息集合
     */
    List<House> listAll();

    /**
     * 根据楼栋ID、单元号、居民姓名/电话/房间号查询对应房屋信息
     * @param buildingId 楼栋ID
     * @param unitNo 单元号
     * @param keyword 居民姓名/电话/房间号
     * @return 房屋信息集合
     */
    List<HouseVO> listByCondition(Integer buildingId, String unitNo, String keyword);

    /**
     * 添加房屋信息（根据室/厅/卫自动解析户型ID）
     * @param saveDTO 添加参数
     */
    void saveHouse(HouseSaveDTO saveDTO);

    /**
     * 修改房屋信息（根据室/厅/卫自动解析户型ID）
     * @param saveDTO 修改参数
     */
    void updateHouse(HouseSaveDTO saveDTO);

    /**
     * 根据房屋ID查询房屋详情（含户型室/厅/卫，用于编辑回显）
     * @param houseId 房屋ID
     * @return 房屋详情
     */
    HouseDetailVO getHouseDetail(Integer houseId);

    /**
     * 删除房屋信息（同步删除其认证记录）
     * @param houseId 房屋ID
     */
    void deleteHouse(Integer houseId);

    /**
     * 批量删除房屋信息（同步删除其认证记录）
     * @param houseIds 房屋ID数组
     */
    void deleteHouseBatch(List<Integer> houseIds);
}
