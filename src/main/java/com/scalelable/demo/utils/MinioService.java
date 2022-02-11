package com.scalelable.demo.utils;

import com.google.api.client.util.IOUtils;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Component
public class MinioService {
    private MinioClient minioClient = null;

    // TODO: 2020/10/15 using 127.0.0.1 when it run at server
    @Value("${minio.client.endpoint}")
    private String endpoint;
    private static int file_number = 0;
    private static final Calendar cal = Calendar.getInstance();
    public final List<String> types = Arrays.asList("平面-CPⅠ网", "平面-CPⅡ网", "平面-CPⅢ网", "水准-CPⅢ高程网", "水准-线上加密水准网", "水准-线下水准基点网");

    public void setClientConnect() {
        if (minioClient == null) {
            try {
                this.minioClient = new MinioClient(endpoint, "minioadmin", "minioadmin");
                System.out.println("Connect Success");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, Object> getFiles(String bucketName, String prefix) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Map<String, Object> map = new HashMap<>();
        try {
            InputStream inputStream = minioClient.getObject(bucketName, prefix);
            ObjectStat objectStat = minioClient.statObject(bucketName, prefix);
            map.put("files", inputStream);
            map.put("length", objectStat.length());
            map.put("contentType", objectStat.contentType());
            map.put("isZIP", "");
            return map;
        } catch (ErrorResponseException e) {
            ZipOutputStream zip = new ZipOutputStream(outputStream);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            int fileNum = 0;
            String[] split = prefix.split("/");
            String folderName = split[split.length - 1];
            for (Result<Item> result : minioClient.listObjects(bucketName, prefix)) {
                Item item = result.get();
                String name = item.objectName().split(prefix, 2)[1];
                if (!name.equals("临时文件.txt")) {
                    InputStream inputStream = minioClient.getObject(bucketName, item.objectName());
                    IOUtils.copy(inputStream, stream, true);
                    zip.putNextEntry(new ZipEntry(folderName + "/" + name));
                    zip.write(stream.toByteArray());
                    fileNum++;
                    stream.reset();
                }
            }
            stream.close();
            zip.close();
            if (fileNum == 0) {
                return null;
            }
            map.put("files", new ByteArrayInputStream(outputStream.toByteArray()));
            map.put("length", (long) outputStream.size());
            map.put("contentType", "application/zip");
            map.put("isZIP", ".zip");
            return map;
        } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException | IOException | InvalidKeyException | NoResponseException | XmlPullParserException | InternalException | InvalidArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void uploadZipFile(String bucketName, String prefix, String projectId, MultipartFile file) throws Exception {
        ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream(), Charset.forName("gbk"));
        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            String objectName = entry.getName();
            if (!Objects.equals(prefix, objectName.split("/")[0])) {
                continue;
            }
            if (!entry.isDirectory()) {
                objectName = objectName.replaceFirst(prefix, projectId);
                minioClient.putObject(bucketName, objectName, zipInputStream, "application/octet-stream");
            }
            zipInputStream.closeEntry();
        }
        zipInputStream.close();
    }

    public void uploadFile(String bucketName, String prefix, MultipartFile file) throws Exception {
        prefix = prefix.endsWith("/") ? prefix : prefix + "/";
        String objectName = prefix + file.getOriginalFilename();
        minioClient.putObject(bucketName, objectName, file.getInputStream(), file.getContentType());
    }

    public List<Map<String, Object>> listObjects(String bucketName, String prefix) throws Exception {
        List<Map<String, Object>> results = new LinkedList<>();
        if (file_number < 0) {
            file_number = 0;
        }

        for (Result<Item> object : minioClient.listObjects(bucketName, prefix, false)) {
            Map<String, Object> map = new HashMap<>();
            Item item = object.get();
            String[] split = item.objectName().split("/");
            String name = split[split.length - 1];
            if (!name.equals("临时文件.txt")) {
                map.put("id", file_number++);
                map.put("name", name);
                map.put("path", item.objectName());
                map.put("size", this.getFileSize(item.objectSize()));
                if (item.isDir()) {
                    List<Map<String, Object>> children = listObjects(bucketName, item.objectName());
                    map.put("lastModified", "");
                    map.put("iconCls", "icon-folder");
                    map.put("size", "");
                    if (children.size() != 0) {
                        map.put("children", children);
                        map.put("state", "closed");
                    }
                } else {
                    map.put("lastModified", this.formatDate(item.lastModified()));
                }
                results.add(map);
            }
        }
        return results;
    }

    private String getFileSize(long size) {
        if (size < 1024) {
            return String.format("%dB", size);
        } else if (size < 1024 * 1024) {
            return String.format("%dKB", size / 1024);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%dMB", size / 1024 / 1024);
        } else {
            return String.format("%dGB", size / 1024 / 1024 / 1024);
        }
    }

    private String formatDate(Date date) {
        cal.setTime(date);
        return String.format("%d-%d-%d %d:%d:%d",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
    }

    public void deleteProject(String bucketName, String prefix) throws Exception {
        for (Result<Item> object : minioClient.listObjects(bucketName, prefix)) {
            Item item = object.get();
            minioClient.removeObject(bucketName, item.objectName());
        }
    }

    public void createProject(String bucketName, String name, String people) throws Exception {
//        String[] types = {"平面-CPⅠ网", "平面-CPⅡ网", "平面-CPⅢ网", "水准-线上加密水准网", "水准-线下水准基点网", "水准-CPⅢ高程网"};
        for (String type : types) {
            String tmpFile = String.format("%s/%s/临时文件.txt", name, type);
            minioClient.putObject(bucketName, tmpFile, new ByteArrayInputStream("".getBytes()), "application//octet-stream");
        }
    }

    public void createBucket(String bucketName) throws Exception {
        boolean exists = minioClient.bucketExists(bucketName);
        if (!exists) {
            minioClient.makeBucket(bucketName);
        }
    }

    public void deleteBucket(String bucketName) throws Exception {
        boolean exists = minioClient.bucketExists(bucketName);
        if (exists) {
            minioClient.removeBucket(bucketName);
        }
    }
}
