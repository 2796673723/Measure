package com.scalelable.demo.controller;

import com.scalelable.demo.domain.*;
import com.scalelable.demo.domain.Process;
import com.scalelable.demo.service.AdminService;
import com.scalelable.demo.utils.MinioService;
import com.scalelable.demo.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final MinioService minioService;
    private final AdminService adminService;
    private final ResponseUtils responseUtils;
    @Value("${minio.server.InformationFile}")
    private String InformationFile;
    private final List<String> types;

    @Autowired
    public AdminController(MinioService minioService, ResponseUtils responseUtils, AdminService adminService) {
        this.minioService = minioService;
        this.adminService = adminService;
        this.responseUtils = responseUtils;
        this.minioService.setClientConnect();
        this.types = minioService.types;
    }

    @GetMapping("/person")
    public String personList() {
        return "admin/person";
    }

    @ResponseBody
    @PostMapping("/get_person")
    public List<ManagerList> getPerson(Principal principal) {
        Admin admin = adminService.getAdminByUsername(principal.getName());
        return adminService.getManagerByCompany(admin.getCompany());
    }

    @ResponseBody
    @PostMapping("/add_person")
    public Map<String, Object> addPerson(@RequestParam String username, @RequestParam String password,
                                         @RequestParam String mobile, @RequestParam String contractor,
                                         @RequestParam String project, Principal principal) {
        Map<String, Object> result = new HashMap<>();
        try {
            Admin admin = adminService.getAdminByUsername(principal.getName());
            Company company = adminService.getCompanyByCompanyName(admin.getCompany());
            Project p = adminService.getProjectByNameAndCompanyId(project.trim(), company.getId());
            Contractor c = adminService.getContractorByNameAndCompany(contractor.trim(), admin.getCompany());
            adminService.addManager(username.trim(), password, mobile.trim(), c.getId(), p.getId());
            result.put("status", true);
        } catch (DuplicateKeyException e) {
            result.put("status", false);
            result.put("msg", "用户名存在！");
        } catch (NullPointerException e) {
            result.put("status", false);
            result.put("msg", "公司或工程不存在！");
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            result.put("status", false);
            result.put("msg", "创建失败");
        }
        return result;
    }

    @ResponseBody
    @PostMapping("/update_person")
    public Map<String, Object> updatePerson(@RequestParam int id, @RequestParam String username,
                                            @RequestParam String password, @RequestParam String mobile,
                                            @RequestParam String contractor, @RequestParam String project,
                                            Principal principal) {
        Map<String, Object> result = new HashMap<>();
        try {
            Admin user = adminService.getAdminByUsername(principal.getName());
            Contractor c = adminService.getContractorByNameAndCompany(contractor.trim(), user.getCompany());
            Company company = adminService.getCompanyByCompanyName(user.getCompany());
            Project p = adminService.getProjectByNameAndCompanyId(project.trim(), company.getId());
            Manager manager = adminService.getManagerById(id);
            Admin m = adminService.getAdminByUsername(manager.getUsername());
            m.setUsername(username.trim());
            m.setPassword(password);
            adminService.updateManager(id, m, mobile.trim(), c.getId(), p.getId());
            result.put("status", true);
        } catch (DuplicateKeyException e) {
            result.put("status", false);
            result.put("msg", "用户名存在！");
        } catch (NullPointerException e) {
            result.put("status", false);
            result.put("msg", "公司或工程不存在！");
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            result.put("status", false);
            result.put("msg", "编辑失败");
        }
        return result;
    }

    @ResponseBody
    @PostMapping("/delete_person")
    public Map<String, Object> deletePerson(@RequestParam int id) {
        Map<String, Object> result = new HashMap<>();
        try {
            adminService.deleteManager(id);
            result.put("status", true);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            result.put("status", false);
        }
        return result;
    }

    //Contractor
    @GetMapping("/contractor")
    public String contractorList() {
        return "admin/contractor";
    }

    @ResponseBody
    @PostMapping("/get_contractor")
    public List<Contractor> getContractor(Principal principal) {
        Admin admin = adminService.getAdminByUsername(principal.getName());
        return adminService.getContractorByCompany(admin.getCompany());
    }

    @ResponseBody
    @PostMapping("/add_contractor")
    public Map<String, Object> addContractor(@RequestParam String contractor, @RequestParam String contact,
                                             @RequestParam String mobile, Principal principal) {
        Map<String, Object> result = new HashMap<>();
        try {
            Admin admin = adminService.getAdminByUsername(principal.getName());
            Contractor c = new Contractor();
            c.setName(contractor.trim());
            c.setContact(contact.trim());
            c.setMobile(mobile.trim());
            c.setCompany(admin.getCompany());
            adminService.addContractor(c);
            result.put("status", true);
        } catch (DuplicateKeyException e) {
            result.put("status", false);
            result.put("msg", "分包单位名存在！");
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            result.put("status", false);
            result.put("msg", "创建失败");
        }
        return result;
    }

    @ResponseBody
    @PostMapping("/update_contractor")
    public Map<String, Object> updateContractor(@RequestParam int id, @RequestParam String contractor,
                                                @RequestParam String contact, @RequestParam String mobile) {
        Map<String, Object> result = new HashMap<>();
        try {
            Contractor c = new Contractor();
            c.setId(id);
            c.setName(contractor.trim());
            c.setContact(contact.trim());
            c.setMobile(mobile.trim());
            adminService.updateContractor(c);
            result.put("status", true);
        } catch (DuplicateKeyException e) {
            result.put("status", false);
            result.put("msg", "分包单位名存在！");
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            result.put("status", false);
            result.put("msg", "创建失败");
        }
        return result;
    }

    @ResponseBody
    @PostMapping("/delete_contractor")
    public Map<String, Object> deleteContractor(@RequestParam int id) {
        Map<String, Object> result = new HashMap<>();
        try {
            adminService.deleteContractor(id);
            result.put("status", true);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            result.put("status", false);
        }
        return result;
    }

    //Project Total
    @GetMapping("/project_total")
    public String projectTotal() {
        return "admin/project_total";
    }

    @ResponseBody
    @PostMapping("/get_project_total")
    public List<Project> getProjectTotal(Principal principal) {
        Admin admin = adminService.getAdminByUsername(principal.getName());
        return adminService.getProjects(admin.getCompany());
    }

    @ResponseBody
    @PostMapping("create_project_total")
    public Map<String, Object> createProject(@RequestParam String name, @RequestParam Float start,
                                             @RequestParam Float end, Principal principal) {
        Map<String, Object> result = new HashMap<>();
        name = name.trim();
        try {
            Admin admin = adminService.getAdminByUsername(principal.getName());
            Company c = adminService.getCompanyByCompanyName(admin.getCompany());
            Project p = new Project();
            p.setName(name);
            p.setStart(start);
            p.setEnd(end);
            p.setCompanyId(c.getId());
            adminService.createProject(p);
            p = adminService.getProjectByNameAndCompanyId(name, c.getId());
            minioService.createProject(c.getCompanyBucket(), p.getId().toString(), c.getCompanyName());
            result.put("status", true);
            return result;
        } catch (DuplicateKeyException e) {
            result.put("status", false);
            result.put("msg", "项目名存在！");
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            result.put("status", false);
            result.put("msg", "创建失败");
        }
        return result;
    }

    @ResponseBody
    @PostMapping("update_project_total")
    public Map<String, Object> createProject(@RequestParam Float start, @RequestParam Float end,
                                             @RequestParam String name, @RequestParam Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Project p = new Project();
            p.setName(name);
            p.setId(id);
            p.setStart(start);
            p.setEnd(end);
            adminService.updateProject(p);
            result.put("status", true);
            return result;
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            result.put("status", false);
            result.put("msg", "修改失败");
        }
        return result;
    }

    @ResponseBody
    @PostMapping("del_project_total")
    public Map<String, Object> delProject(@RequestParam Integer id, Principal principal) {
        Map<String, Object> result = new HashMap<>();
        try {
            String bucket = adminService.getAdminBucketByUsername(principal.getName());
            Project p = adminService.getProjectById(id);
            minioService.deleteProject(bucket, String.valueOf(p.getId()));
            adminService.delProjectById(id);
            result.put("status", true);
            return result;
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            result.put("status", false);
        }
        return result;
    }

    //Project
    @GetMapping("/project")
    public String project() {
        return "admin/project";
    }

    @ResponseBody
    @PostMapping("/get_project_info")
    public Project getProjectInfo(@RequestParam Integer id) {
        return adminService.getProjectById(id);
    }

    @ResponseBody
    @PostMapping("/get_project")
    public List<Map<String, Object>> getProject(Principal principal, String prefix) {
        try {
            String bucket = adminService.getAdminBucketByUsername(principal.getName());
            return minioService.listObjects(bucket, prefix);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return null;
        }
    }

    @ResponseBody
    @GetMapping("/download_project")
    public ResponseEntity<InputStreamResource> downloadProject(@RequestParam String prefix,
                                                               Principal principal, HttpServletRequest request) {
        String bucketName = adminService.getAdminBucketByUsername(principal.getName());
        return responseUtils.getMinioFiles(bucketName, prefix, minioService, request);
    }

    private static final SimpleDateFormat sdf2str = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat sdf2date = new SimpleDateFormat("MM/dd/yyyy");

    private String formatDateInDay(Date date) {
        return sdf2str.format(date);
    }

    private Date formatDateString(String date) {
        try {
            return sdf2date.parse(date);
        } catch (ParseException e) {
            return new Date();
        }
    }

    //上传文件到当日类型目录
    @ResponseBody
    @PostMapping("/upload_project")
    public Map<String, Object> uploadProject(@RequestParam MultipartFile[] files, @RequestParam String prefix,
                                             @RequestParam String type, Principal principal) {
        String bucketName = adminService.getAdminBucketByUsername(principal.getName());
        String formatDate = formatDateInDay(new Date());
        prefix = String.format("%s/%s/%s", prefix, type, formatDate);
        return responseUtils.uploadMultiFile(files, bucketName, prefix, minioService);
    }

    //上传文件到指定目录
    @ResponseBody
    @PostMapping("/upload_assign_project")
    public Map<String, Object> assignProject(@RequestParam MultipartFile[] files, @RequestParam String prefix,
                                             @RequestParam String type, @RequestParam String date, Principal principal) {
        String bucketName = adminService.getAdminBucketByUsername(principal.getName());
        String formatDate = formatDateInDay(formatDateString(date));
        prefix = String.format("%s/%s/%s", prefix, type, formatDate);
        return responseUtils.uploadMultiFile(files, bucketName, prefix, minioService);
    }

    //删除文件
    @ResponseBody
    @PostMapping("delete_project")
    public Map<String, Object> deleteProject(@RequestParam String prefix, Principal principal) {
        String bucketName = adminService.getAdminBucketByUsername(principal.getName());
        return responseUtils.deleteObject(minioService, bucketName, prefix);
    }

    //预览文件
    @GetMapping("preview_project")
    public String previewProject(@RequestParam String prefix, Model model, Principal principal) {
        try {
            String bucketName = adminService.getAdminBucketByUsername(principal.getName());
            Map<String, Object> map = minioService.getFiles(bucketName, prefix);
            ByteArrayOutputStream files = (ByteArrayOutputStream) map.get("files");
            String[] split = prefix.split("/");
            model.addAttribute("filename", split[split.length - 1]);
            model.addAttribute("text", files.toString());
            return "admin/preview";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    //information
    @GetMapping("/information")
    public String information() {
        return "admin/information";
    }

    @ResponseBody
    @PostMapping("/get_information_total")
    public List<Contractor> getInformation(Principal principal) {
        Admin admin = adminService.getAdminByUsername(principal.getName());
        return adminService.getContractorByCompany(admin.getCompany());
    }

    @ResponseBody
    @PostMapping("/get_information")
    public List<Map<String, Object>> getInformation(@RequestParam String prefix) {
        try {
            return minioService.listObjects(InformationFile, prefix + "/");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
    }

    @ResponseBody
    @GetMapping("/download_information")
    public ResponseEntity<InputStreamResource> downloadInformation(@RequestParam String prefix, HttpServletRequest request) {
        return responseUtils.getMinioFiles(InformationFile, prefix, minioService, request);
    }

    @ResponseBody
    @GetMapping("/preview_information")
    public ResponseEntity<InputStreamResource> previewInformation(@RequestParam String prefix, HttpServletRequest request) {
        ResponseEntity<InputStreamResource> entity = responseUtils.getMinioFiles(InformationFile, prefix, minioService, request);
        HttpHeaders headers = entity.getHeaders();
        return ResponseEntity.ok()
                .header("Content-Type", String.valueOf(headers.getContentType()))
                .body(entity.getBody());
    }

    @ResponseBody
    @PostMapping("/upload_information")
    public Map<String, Object> uploadInformation(@RequestParam MultipartFile[] files, @RequestParam String prefix) {
        return responseUtils.uploadMultiFile(files, InformationFile, prefix + "/", minioService);
    }

    @ResponseBody
    @PostMapping("/delete_information")
    public Map<String, Object> deleteInformation(@RequestParam String prefix) {
        return responseUtils.deleteObject(minioService, InformationFile, prefix);
    }

    @GetMapping("/process")
    public String process() {
        return "admin/process";
    }


    @ResponseBody
    @PostMapping("/list_process_total")
    public List<Project> processListTotal(Principal principal) {
        Admin admin = adminService.getAdminByUsername(principal.getName());
        List<Project> projects = adminService.getProjects(admin.getCompany());
        return adminService.getProjectProcess(projects, types);
    }

    @ResponseBody
    @PostMapping("list_process")
    public List<Process> processList(@RequestParam Integer id, @RequestParam Integer type) {
        Process process = new Process();
        process.setType(type);
        process.setProjectId(id);
        return adminService.getProcessByTypeAndProject(process);
    }

}
