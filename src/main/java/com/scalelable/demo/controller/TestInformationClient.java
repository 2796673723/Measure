package com.scalelable.demo.controller;

import com.scalelable.demo.domain.Admin;
import com.scalelable.demo.domain.Process;
import com.scalelable.demo.domain.Project;
import com.scalelable.demo.service.AdminService;
import com.scalelable.demo.service.ManagerService;
import com.scalelable.demo.utils.MinioService;
import com.scalelable.demo.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("/test")
public class TestInformationClient {

    private final MinioService minioService;
    private final ResponseUtils responseUtils;
    private final ManagerService managerService;
    private final AdminService adminService;
    @Value("${minio.server.InformationFile}")
    private String InformationFile;
    private final List<String> types;

    @Autowired
    public TestInformationClient(MinioService minioService, ResponseUtils responseUtils,
                                 ManagerService managerService, AdminService adminService) {
        this.minioService = minioService;
        this.responseUtils = responseUtils;
        this.managerService = managerService;
        this.adminService = adminService;
        this.types = minioService.types;

        minioService.setClientConnect();
    }

    @ResponseBody
    @PostMapping("/upload")
    public Map<String, Object> UploadFile(@RequestParam MultipartFile[] files) {
        String bucketName = InformationFile;
        String prefix = "1";
        return responseUtils.uploadMultiFile(files, bucketName, prefix, minioService);
    }

    @ResponseBody
    @PostMapping("list_process")
    public List<Process> processList(@RequestParam Integer type, @RequestParam Integer projectId) {
        Process process = new Process();
        process.setType(type);
        process.setProjectId(projectId);
        return managerService.getProcessByTypeAndProject(process);
    }

//    private final List<String> types = Arrays.asList("平面-CPⅠ", "平面-CPⅡ", "平面-CPⅢ", "水准-线下水准控制点", "水准-线上加密点", "水准-CPⅢ水准点");

    @ResponseBody
    @PostMapping("/list_process_total")
    public List<Project> processListTotal() {
        List<Project> projects = adminService.getProjects("测绘单位00");
        return adminService.getProjectProcess(projects, types);
    }

    @ResponseBody
    @PostMapping("add_process")
    public void addProcess(@RequestParam Integer type, @RequestParam Float start, @RequestParam Float end) {
        Process process = new Process();
        process.setType(type);
        process.setStart(start);
        process.setEnd(end);
        process.setProjectId(2);
        managerService.addProcess(process);
    }

    @ResponseBody
    @PostMapping("update_process")
    public void updateProcess(@RequestParam Integer id, @RequestParam Float start, @RequestParam Float end) {
        Process process = new Process();
        process.setId(id);
        process.setStart(start);
        process.setEnd(end);
        managerService.UpdateProcess(process);
    }

    @ResponseBody
    @PostMapping("delete_process")
    public void deleteProcess(@RequestParam Integer id) {
        Process process = new Process();
        process.setId(id);
        managerService.deleteProcessById(process);
    }

}
