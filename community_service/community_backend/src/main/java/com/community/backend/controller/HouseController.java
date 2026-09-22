package com.community.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.common.dto.HouseQueryDTO;
import com.community.backend.dto.HouseSaveDTO;
import com.community.backend.service.HouseService;
import com.community.common.vo.HouseDetailVO;
import com.community.common.vo.HouseVO;
import com.community.common.pojo.House;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房屋管理接口
 */
@RestController
@RequestMapping("/api/house")
@Api(tags = "房屋管理")
public class HouseController {

    @Autowired
    private HouseService houseService;

    @GetMapping("/page")
    @ApiOperation("分页+条件查询房屋列表")
    public Result<IPage<HouseVO>> page(HouseQueryDTO queryDTO) {
        IPage<HouseVO> page = houseService.selectHousePage(queryDTO);
        return Result.success(page);
    }

    @DeleteMapping("/delete")
    @ApiOperation("根据ID删除房屋信息")
    public Result<String> delete(
            @ApiParam(value = "房屋ID", required = true) @RequestParam Integer houseId) {
        houseService.deleteHouse(houseId);
        return Result.success("删除成功");
    }

    @PostMapping("/batchDelete")
    @ApiOperation("批量删除房屋信息")
    public Result<String> batchDelete(
            @ApiParam(value = "房屋ID数组", required = true) @RequestBody List<Integer> houseIds) {
        houseService.deleteHouseBatch(houseIds);
        return Result.success("批量删除成功");
    }

    @GetMapping("/listByBuildingId")
    @ApiOperation("根据楼栋ID查询该楼栋下的房屋列表")
    public Result<List<House>> listByBuildingId(
            @ApiParam(value = "楼栋ID", required = true) @RequestParam Integer buildingId) {
        List<House> list = houseService.listByBuildingId(buildingId);
        return Result.success(list);
    }

    @PostMapping("/add")
    @ApiOperation("添加房屋信息")
    public Result<String> add(@RequestBody HouseSaveDTO saveDTO) {
        houseService.saveHouse(saveDTO);
        return Result.success("添加成功");
    }

    @GetMapping("/getById")
    @ApiOperation("根据ID查询房屋信息")
    public Result<HouseDetailVO> getById(
            @ApiParam(value = "房屋ID", required = true) @RequestParam Integer houseId) {
        HouseDetailVO house = houseService.getHouseDetail(houseId);
        if (house == null) {
            return Result.error("40001", "房屋信息不存在");
        }
        return Result.success(house);
    }

    @PostMapping("/update")
    @ApiOperation("修改房屋信息")
    public Result<String> update(@RequestBody HouseSaveDTO saveDTO) {
        houseService.updateHouse(saveDTO);
        return Result.success("修改成功");
    }

    @GetMapping("/listByBuildingIdAndUnitNo")
    @ApiOperation("根据楼栋ID和单元号查询对应的房屋信息集合")
    public Result<List<House>> listByBuildingIdAndUnitNo(
            @ApiParam(value = "楼栋ID", required = true) @RequestParam Integer buildingId,
            @ApiParam(value = "单元号", required = true) @RequestParam String unitNo) {
        List<House> list = houseService.listByBuildingIdAndUnitNo(buildingId, unitNo);
        return Result.success(list);
    }

    @PostMapping("/listByIds")
    @ApiOperation("根据房屋ID数组查询指定房屋信息")
    public Result<List<House>> listByIds(
            @ApiParam(value = "房屋ID数组", required = true) @RequestBody List<Integer> houseIds) {
        List<House> list = houseService.listByIds(houseIds);
        return Result.success(list);
    }

    @GetMapping("/listAll")
    @ApiOperation("查询全部房屋信息")
    public Result<List<House>> listAll() {
        List<House> list = houseService.listAll();
        return Result.success(list);
    }

    @GetMapping("/listByCondition")
    @ApiOperation("根据楼栋ID、单元号、居民姓名/电话/房间号查询对应房屋信息")
    public Result<List<HouseVO>> listByCondition(
            @ApiParam(value = "楼栋ID") @RequestParam(required = false) Integer buildingId,
            @ApiParam(value = "单元号") @RequestParam(required = false) String unitNo,
            @ApiParam(value = "居民姓名/电话/房间号") @RequestParam(required = false) String keyword) {
        List<HouseVO> list = houseService.listByCondition(buildingId, unitNo, keyword);
        return Result.success(list);
    }
}
