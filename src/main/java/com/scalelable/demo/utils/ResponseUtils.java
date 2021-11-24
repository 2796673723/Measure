package com.scalelable.demo.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ResponseUtils {
    public ResponseEntity<byte[]> getMinioFiles(String bucketName, String prefix,
                                                MinioService minioService, HttpServletRequest request) {
        try {
            Map<String, Object> map = minioService.getFiles(bucketName, prefix);
            ByteArrayOutputStream files = (ByteArrayOutputStream) map.get("files");
            String isZIP = (String) map.get("isZIP");
            String contentType = (String) map.get("contentType");
            String[] split = prefix.split("/");
            String filename = getFilename(request, split[split.length - 1] + isZIP);
            return ResponseEntity.ok()
                    .header("Content-Disposition", String.format("attachment; filename=%s", filename))
                    .header("Content-Type", contentType)
                    .contentLength(files.size())
                    .body(files.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
        }
    }

    private String getFilename(HttpServletRequest request, String filename) throws Exception {
        String[] IEBrowserKeyWords = {"MSIE", "Trident", "Edge"};
        String userAgent = request.getHeader("User-Agent");
        for (String keyWord : IEBrowserKeyWords) {
            if (userAgent.contains(keyWord)) {
                return URLEncoder.encode(filename, "UTF-8").replace("+", " ");
            }
        }
        return new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
    }

    public Map<String, Object> uploadMultiFile(MultipartFile[] files, String bucketName,
                                               String prefix, MinioService minioService) {
        Map<String, Object> result = new HashMap<>();
        try {
            for (MultipartFile file : files) {
                minioService.uploadFile(bucketName, prefix, file);
            }
            result.put("status", true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result.put("status", false);
        }
        return result;
    }

    public Map<String, Object> deleteObject(MinioService minioService, String bucketName, String prefix) {
        Map<String, Object> result = new HashMap<>();
        try {
            minioService.deleteProject(bucketName, prefix);
            result.put("status", true);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            result.put("status", false);
        }
        return result;
    }
}
