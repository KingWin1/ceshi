package com.community.backend.controller;

import com.community.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文件管理接口
 */
@RestController
@RequestMapping("/api/file")
@Api(tags = "文件管理")
public class FileController {

    @Value("${file.upload-path:./uploads}")
    private String uploadPath;

    /**
     * 允许上传的文件类型
     */
    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "application/pdf"
    );

    /**
     * 最大文件大小：10MB
     */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @PostMapping("/upload")
    @ApiOperation("上传文件（支持多文件上传，最多10个，支持JPG/PNG/PDF）")
    public Result<List<String>> upload(
            @ApiParam(value = "文件", required = true) @RequestParam("files") MultipartFile[] files) {
        // 验证文件数量
        if (files.length > 10) {
            return Result.error("40001", "最多只能上传10个文件");
        }

        List<String> filePaths = new ArrayList<>();

        for (MultipartFile file : files) {
            // 验证文件类型
            if (!ALLOWED_TYPES.contains(file.getContentType())) {
                return Result.error("40002", "只支持JPG/PNG/PDF格式的文件");
            }

            // 验证文件大小
            if (file.getSize() > MAX_FILE_SIZE) {
                return Result.error("40003", "文件大小不能超过10MB");
            }

            try {
                // 生成文件名（时间戳+原始文件名）
                String originalFilename = file.getOriginalFilename();
                String newFilename = System.currentTimeMillis() + "_" + originalFilename;

                // 创建上传目录
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // 保存文件
                File dest = new File(uploadPath + File.separator + newFilename);
                file.transferTo(dest);

                // 返回文件路径
                filePaths.add("/uploads/" + newFilename);
            } catch (IOException e) {
                e.printStackTrace();
                return Result.error("50001", "文件上传失败：" + e.getMessage());
            }
        }

        return Result.success(filePaths);
    }

    @GetMapping("/download")
    @ApiOperation("下载文件")
    public void download(
            @ApiParam(value = "文件路径", required = true) @RequestParam String filePath,
            HttpServletResponse response) {
        try {
            // 拼接完整文件路径
            String fullPath = uploadPath + filePath.replace("/uploads/", File.separator);
            File file = new File(fullPath);

            // 验证文件是否存在
            if (!file.exists()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // 设置响应头
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
            response.setContentLengthLong(file.length());

            // 输出文件流
            FileInputStream fis = new FileInputStream(file);
            OutputStream os = response.getOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
            fis.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
