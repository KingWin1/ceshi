package com.community.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.backend.dto.BillAddDTO;
import com.community.backend.dto.BillExportDTO;
import com.community.backend.dto.BillImportRowDTO;
import com.community.backend.dto.BillQueryDTO;
import com.community.backend.service.BillService;
import com.community.backend.vo.BillVO;
import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 账单管理接口
 */
@RestController
@RequestMapping("/api/bill")
@Api(tags = "账单管理")
public class BillController {

    @Autowired
    private BillService billService;

    /** 日期格式化（yyyy-MM-dd），局部实例避免线程安全问题 */
    private String formatDate(Date date) {
        return date == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    @GetMapping("/page")
    @ApiOperation("分页+条件查询账单列表")
    public Result<IPage<BillVO>> page(BillQueryDTO queryDTO) {
        IPage<BillVO> page = billService.selectBillPage(queryDTO);
        return Result.success(page);
    }

    @GetMapping("/detail")
    @ApiOperation("根据账单ID查询账单详情")
    public Result<BillVO> detail(
            @ApiParam(value = "账单ID", required = true) @RequestParam Integer billId) {
        BillVO vo = billService.getBillDetail(billId);
        if (vo == null) {
            return Result.error("40001", "账单不存在");
        }
        return Result.success(vo);
    }

    @PostMapping("/batchDelete")
    @ApiOperation("根据账单ID数组批量删除账单信息")
    public Result<String> batchDelete(
            @ApiParam(value = "账单ID数组", required = true) @RequestBody List<Integer> billIds) {
        billService.removeByIds(billIds);
        return Result.success("批量删除成功");
    }

    @PostMapping("/add")
    @ApiOperation("添加账单信息")
    public Result<String> add(@RequestBody BillAddDTO addDTO) {
        billService.addBill(addDTO);
        return Result.success("添加成功");
    }

    @PostMapping("/batchAdd")
    @ApiOperation("批量添加账单信息")
    public Result<String> batchAdd(@RequestBody List<BillAddDTO> addDTOList) {
        billService.batchAddBill(addDTOList);
        return Result.success("批量添加成功");
    }

    @PostMapping("/pay")
    @ApiOperation("根据账单ID缴费")
    public Result<String> pay(
            @ApiParam(value = "账单ID", required = true) @RequestParam Integer billId,
            @ApiParam(value = "支付方式：1-微信 2-支付宝 3-现金 4-银行转账", required = true) @RequestParam Integer payMethod,
            @ApiParam(value = "支付流水号") @RequestParam(required = false) String paySerialNo) {
        billService.payBill(billId, payMethod, paySerialNo);
        return Result.success("缴费成功");
    }

    @PostMapping("/export")
    @ApiOperation("导出账单信息")
    public void export(@RequestBody BillExportDTO exportDTO, HttpServletResponse response) {
        try {
            List<BillVO> list;

            // 根据导出范围获取数据
            if (exportDTO.getExportRange() == 1) {
                // 当前页
                BillQueryDTO queryDTO = exportDTO.getQueryDTO();
                if (queryDTO == null) queryDTO = new BillQueryDTO();
                queryDTO.setPageNum(exportDTO.getPageNum() != null ? exportDTO.getPageNum() : 1);
                queryDTO.setPageSize(exportDTO.getPageSize() != null ? exportDTO.getPageSize() : 10);
                IPage<BillVO> page = billService.selectBillPage(queryDTO);
                list = page.getRecords();
            } else if (exportDTO.getExportRange() == 2) {
                // 全部数据
                list = billService.getBillList(new BillQueryDTO());
            } else {
                // 按筛选条件
                list = billService.getBillList(exportDTO.getQueryDTO());
            }

            List<String> fields = exportDTO.getExportFields();

            if (exportDTO.getFileFormat() == 1) {
                // 导出Excel
                exportExcel(response, list, fields);
            } else {
                // 导出CSV
                exportCsv(response, list, fields);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/downloadTemplate")
    @ApiOperation("下载账单导入Excel模板")
    public void downloadTemplate(HttpServletResponse response) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("账单导入模板");

            // 表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"账单编号", "楼栋名称", "单元号", "房间号", "费用类型名称",
                    "计费周期", "计费周期值", "计费开始日期", "计费结束日期", "账单金额", "优惠金额"};
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 填写说明
            Row tipRow = sheet.createRow(1);
            tipRow.createCell(0).setCellValue("填写说明：楼栋名称/费用类型名称须与系统一致；单元号填数字；计费周期填 month/quarter/year；"
                    + "计费周期值按月填1-12、按季度填1-4、按年可留空；日期格式 yyyy-MM-dd；导入前请删除本行");

            // 示例数据
            Row dataRow = sheet.createRow(2);
            String[] sampleData = {"B2026090001", "1号楼", "1", "101", "物业费", "month", "9", "2026-09-01", "2026-09-30", "300.00", "0.00"};
            for (int i = 0; i < sampleData.length; i++) {
                dataRow.createCell(i).setCellValue(sampleData[i]);
            }

            // 设置列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, 4000);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("账单导入模板.xlsx", "UTF-8"));

            OutputStream os = response.getOutputStream();
            workbook.write(os);
            os.flush();
            os.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/import")
    @ApiOperation("导入账单信息")
    public Result<String> importBill(
            @ApiParam(value = "Excel文件", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(value = "是否覆盖重复账单") @RequestParam(defaultValue = "false") Boolean overwrite) {
        try {
            // 验证文件类型
            String filename = file.getOriginalFilename();
            if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
                return Result.error("40001", "只支持xlsx/xls格式的文件");
            }

            // 验证文件大小（5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return Result.error("40002", "文件大小不能超过5MB");
            }

            Workbook workbook = createWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);

            // 逐行读取Excel列数据，交由service完成匹配与批量添加
            List<BillImportRowDTO> rowList = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isEmptyRow(row)) {
                    continue;
                }
                BillImportRowDTO rowDTO = new BillImportRowDTO();
                rowDTO.setBillNo(getCellStringValue(row.getCell(0)));
                rowDTO.setBuildingName(getCellStringValue(row.getCell(1)));
                rowDTO.setUnitNo(getCellStringValue(row.getCell(2)));
                rowDTO.setHouseNumber(getCellStringValue(row.getCell(3)));
                rowDTO.setFeeTypeName(getCellStringValue(row.getCell(4)));
                rowDTO.setBillingPeriod(getCellStringValue(row.getCell(5)));
                rowDTO.setBillingPeriodValue(getCellIntValue(row.getCell(6)));
                rowDTO.setStartDate(parseDate(getCellStringValue(row.getCell(7)), sdf));
                rowDTO.setEndDate(parseDate(getCellStringValue(row.getCell(8)), sdf));
                rowDTO.setBillAmount(getCellDecimalValue(row.getCell(9)));
                rowDTO.setDiscountAmount(getCellDecimalValue(row.getCell(10)));
                rowList.add(rowDTO);
            }
            workbook.close();

            String msg = billService.importBills(rowList, Boolean.TRUE.equals(overwrite));
            return Result.success(msg);
        } catch (RuntimeException e) {
            return Result.error("40003", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("50001", "文件读取失败：" + e.getMessage());
        }
    }

    /**
     * 按文件后缀创建Workbook（xls用HSSF，xlsx用XSSF）
     */
    private Workbook createWorkbook(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename != null && filename.endsWith(".xls")) {
            return new org.apache.poi.hssf.usermodel.HSSFWorkbook(file.getInputStream());
        }
        return new XSSFWorkbook(file.getInputStream());
    }

    /**
     * 判断整行为空：只检查楼栋~优惠金额列（1-10），
     * 兼容模板中首列为"填写说明"文字但其余列为空的提示行
     */
    private boolean isEmptyRow(Row row) {
        for (int i = 1; i <= 10; i++) {
            if (StringUtils.hasText(getCellStringValue(row.getCell(i)))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 读取单元格整数（文本/数字均兼容）
     */
    private Integer getCellIntValue(Cell cell) {
        String value = getCellStringValue(cell);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return (int) Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 读取单元格金额
     */
    private BigDecimal getCellDecimalValue(Cell cell) {
        String value = getCellStringValue(cell);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 解析日期文本（yyyy-MM-dd），失败返回null
     */
    private Date parseDate(String value, SimpleDateFormat sdf) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return sdf.parse(value.trim());
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 导出Excel
     */
    private void exportExcel(HttpServletResponse response, List<BillVO> list, List<String> fields) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("账单信息");

        // 表头
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        Map<String, String> fieldMap = getFieldMap();
        int colIndex = 0;
        for (String field : fields) {
            if (fieldMap.containsKey(field)) {
                Cell cell = headerRow.createCell(colIndex++);
                cell.setCellValue(fieldMap.get(field));
                cell.setCellStyle(headerStyle);
            }
        }

        // 数据行
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int rowIndex = 1;
        for (BillVO vo : list) {
            Row row = sheet.createRow(rowIndex++);
            colIndex = 0;
            for (String field : fields) {
                if (!fieldMap.containsKey(field)) continue;
                Cell cell = row.createCell(colIndex++);
                setCellValue(cell, vo, field, sdf);
            }
        }

        // 设置列宽
        for (int i = 0; i < fields.size(); i++) {
            sheet.setColumnWidth(i, 4000);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("账单信息.xlsx", "UTF-8"));

        OutputStream os = response.getOutputStream();
        workbook.write(os);
        os.flush();
        os.close();
        workbook.close();
    }

    /**
     * 导出CSV
     */
    private void exportCsv(HttpServletResponse response, List<BillVO> list, List<String> fields) throws IOException {
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("账单信息.csv", "UTF-8"));

        Map<String, String> fieldMap = getFieldMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 添加BOM头，防止Excel打开中文乱码
        OutputStream os = response.getOutputStream();
        os.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));

        // 表头
        StringBuilder header = new StringBuilder();
        for (String field : fields) {
            if (fieldMap.containsKey(field)) {
                if (header.length() > 0) header.append(",");
                header.append(fieldMap.get(field));
            }
        }
        writer.println(header.toString());

        // 数据行
        for (BillVO vo : list) {
            StringBuilder line = new StringBuilder();
            for (String field : fields) {
                if (!fieldMap.containsKey(field)) continue;
                if (line.length() > 0) line.append(",");
                line.append(getCsvValue(vo, field, sdf));
            }
            writer.println(line.toString());
        }

        writer.flush();
        writer.close();
    }

    /**
     * 字段名映射（英文字段 -> 中文表头）
     */
    private Map<String, String> getFieldMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("billNo", "账单编号");
        map.put("buildingName", "楼栋名称");
        map.put("unitNo", "单元");
        map.put("houseNumber", "房间号");
        map.put("fullHouseNo", "完整房号");
        map.put("residentName", "业主姓名");
        map.put("phone", "业主电话");
        map.put("feeTypeName", "费用类型");
        map.put("feeName", "费用名称");
        map.put("billingPeriod", "计费周期");
        map.put("startDate", "计费开始日期");
        map.put("endDate", "计费结束日期");
        map.put("area", "计费面积");
        map.put("unitPrice", "单价");
        map.put("billAmount", "账单金额");
        map.put("discountAmount", "优惠金额");
        map.put("payableAmount", "应付金额");
        map.put("paidAmount", "已付金额");
        map.put("status", "状态");
        map.put("payMethod", "支付方式");
        map.put("payTime", "支付时间");
        map.put("paySerialNo", "支付流水号");
        map.put("remindCount", "催缴次数");
        map.put("lastRemindTime", "最后催缴时间");
        map.put("remark", "备注");
        map.put("createTime", "生成时间");
        return map;
    }

    /**
     * 计费周期文案：month-2026-09 / quarter-2026-Q3 / year-2026
     */
    private String formatPeriodText(BillVO vo) {
        String period = vo.getBillingPeriod();
        if (period == null || period.isEmpty() || vo.getStartDate() == null) {
            return "";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(vo.getStartDate());
        int year = cal.get(Calendar.YEAR);
        Integer value = vo.getBillingPeriodValue();
        if ("month".equals(period)) {
            int month = value != null ? value : cal.get(Calendar.MONTH) + 1;
            return year + "-" + (month < 10 ? "0" + month : String.valueOf(month));
        }
        if ("quarter".equals(period)) {
            int q = value != null ? value : (cal.get(Calendar.MONTH) / 3 + 1);
            return year + "-Q" + q;
        }
        return String.valueOf(year);
    }

    /**
     * 设置Excel单元格值
     */
    private void setCellValue(Cell cell, BillVO vo, String field, SimpleDateFormat sdf) {
        switch (field) {
            case "billNo": cell.setCellValue(vo.getBillNo() != null ? vo.getBillNo() : ""); break;
            case "buildingName": cell.setCellValue(vo.getBuildingName() != null ? vo.getBuildingName() : ""); break;
            case "unitNo": cell.setCellValue(vo.getUnitNo() != null ? vo.getUnitNo() + "单元" : ""); break;
            case "houseNumber": cell.setCellValue(vo.getHouseNumber() != null ? vo.getHouseNumber() : ""); break;
            case "fullHouseNo": cell.setCellValue(vo.getFullHouseNo() != null ? vo.getFullHouseNo() : ""); break;
            case "residentName": cell.setCellValue(vo.getResidentName() != null ? vo.getResidentName() : ""); break;
            case "phone": cell.setCellValue(vo.getPhone() != null ? vo.getPhone() : ""); break;
            case "feeTypeName": cell.setCellValue(vo.getFeeTypeName() != null ? vo.getFeeTypeName() : ""); break;
            case "feeName": cell.setCellValue(vo.getFeeTypeName() != null ? vo.getFeeTypeName() : ""); break;
            case "unitPrice": cell.setCellValue(vo.getUnitPrice() != null ? vo.getUnitPrice().doubleValue() : 0); break;
            case "area": cell.setCellValue(vo.getArea() != null ? vo.getArea().doubleValue() : 0); break;
            case "billAmount": cell.setCellValue(vo.getBillAmount() != null ? vo.getBillAmount().doubleValue() : 0); break;
            case "discountAmount": cell.setCellValue(vo.getDiscountAmount() != null ? vo.getDiscountAmount().doubleValue() : 0); break;
            case "payableAmount": cell.setCellValue(vo.getPayableAmount() != null ? vo.getPayableAmount().doubleValue() : 0); break;
            case "paidAmount": cell.setCellValue(vo.getPaidAmount() != null ? vo.getPaidAmount().doubleValue() : 0); break;
            case "payMethod": cell.setCellValue(vo.getPayMethod() != null ? getPayMethodName(vo.getPayMethod()) : ""); break;
            case "payTime": cell.setCellValue(vo.getPayTime() != null ? sdf.format(vo.getPayTime()) : ""); break;
            case "paySerialNo": cell.setCellValue(vo.getPaySerialNo() != null ? vo.getPaySerialNo() : ""); break;
            case "billingPeriod": cell.setCellValue(formatPeriodText(vo)); break;
            case "startDate": cell.setCellValue(formatDate(vo.getStartDate())); break;
            case "endDate": cell.setCellValue(formatDate(vo.getEndDate())); break;
            case "remindCount": cell.setCellValue(vo.getRemindCount() != null ? vo.getRemindCount() : 0); break;
            case "lastRemindTime": cell.setCellValue(vo.getLastRemindTime() != null ? sdf.format(vo.getLastRemindTime()) : ""); break;
            case "status": cell.setCellValue(vo.getStatus() != null ? (vo.getStatus() == 1 ? "已缴" : "未缴") : ""); break;
            case "createTime": cell.setCellValue(vo.getCreateTime() != null ? sdf.format(vo.getCreateTime()) : ""); break;
            case "remark": cell.setCellValue(""); break;
            default: cell.setCellValue(""); break;
        }
    }

    /**
     * 获取CSV字段值
     */
    private String getCsvValue(BillVO vo, String field, SimpleDateFormat sdf) {
        switch (field) {
            case "billNo": return vo.getBillNo() != null ? vo.getBillNo() : "";
            case "buildingName": return vo.getBuildingName() != null ? vo.getBuildingName() : "";
            case "unitNo": return vo.getUnitNo() != null ? vo.getUnitNo() + "单元" : "";
            case "houseNumber": return vo.getHouseNumber() != null ? vo.getHouseNumber() : "";
            case "fullHouseNo": return vo.getFullHouseNo() != null ? vo.getFullHouseNo() : "";
            case "residentName": return vo.getResidentName() != null ? vo.getResidentName() : "";
            case "phone": return vo.getPhone() != null ? vo.getPhone() : "";
            case "feeTypeName": return vo.getFeeTypeName() != null ? vo.getFeeTypeName() : "";
            case "feeName": return vo.getFeeTypeName() != null ? vo.getFeeTypeName() : "";
            case "unitPrice": return vo.getUnitPrice() != null ? vo.getUnitPrice().toString() : "0";
            case "area": return vo.getArea() != null ? vo.getArea().toString() : "0";
            case "billAmount": return vo.getBillAmount() != null ? vo.getBillAmount().toString() : "0";
            case "discountAmount": return vo.getDiscountAmount() != null ? vo.getDiscountAmount().toString() : "0";
            case "payableAmount": return vo.getPayableAmount() != null ? vo.getPayableAmount().toString() : "0";
            case "paidAmount": return vo.getPaidAmount() != null ? vo.getPaidAmount().toString() : "0";
            case "payMethod": return vo.getPayMethod() != null ? getPayMethodName(vo.getPayMethod()) : "";
            case "payTime": return vo.getPayTime() != null ? sdf.format(vo.getPayTime()) : "";
            case "paySerialNo": return vo.getPaySerialNo() != null ? vo.getPaySerialNo() : "";
            case "billingPeriod": return formatPeriodText(vo);
            case "startDate": return formatDate(vo.getStartDate());
            case "endDate": return formatDate(vo.getEndDate());
            case "remindCount": return String.valueOf(vo.getRemindCount() != null ? vo.getRemindCount() : 0);
            case "lastRemindTime": return vo.getLastRemindTime() != null ? sdf.format(vo.getLastRemindTime()) : "";
            case "status": return vo.getStatus() != null ? (vo.getStatus() == 1 ? "已缴" : "未缴") : "";
            case "createTime": return vo.getCreateTime() != null ? sdf.format(vo.getCreateTime()) : "";
            case "remark": return "";
            default: return "";
        }
    }

    /**
     * 获取支付方式名称
     */
    private String getPayMethodName(Integer payMethod) {
        switch (payMethod) {
            case 1: return "微信支付";
            case 2: return "支付宝";
            case 3: return "现金";
            case 4: return "银行转账";
            default: return "";
        }
    }

    /**
     * 获取单元格字符串值
     */
    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return "";
        }
    }
}
