package com.scalelable.demo.controller;

import com.scalelable.demo.domain.*;
import com.scalelable.demo.domain.Process;
import com.scalelable.demo.service.ManagerService;
import com.scalelable.demo.utils.MinioService;
import com.scalelable.demo.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    public ManagerService managerService;
    public MinioService minioService;
    public ResponseUtils responseUtils;
    @Value("${minio.server.InformationFile}")
    private String InformationFile;
    private final List<String> types;

    @Autowired
    public ManagerController(ManagerService managerService, MinioService minioService, ResponseUtils responseUtils) {
        this.managerService = managerService;
        this.responseUtils = responseUtils;
        this.minioService = minioService;
        this.types = minioService.types;

        minioService.setClientConnect();
    }

    // Person
    @GetMapping("/person")
    public String personList() {
        return "manager/person";
    }

    @ResponseBody
    @PostMapping("/get_person")
    public List<PersonList> getPerson(Principal principal) {
        return managerService.getPersonListByUsername(principal.getName());
    }

    @ResponseBody
    @PostMapping("/add_person")
    public Map<String, Object> addPerson(@RequestParam String username, @RequestParam String password,
                                         @RequestParam String mobile, Principal principal) {
        Map<String, Object> result = new HashMap<>();
        username = username.trim();
        mobile = mobile.trim();
        Manager m = managerService.getManagerByName(principal.getName());

        Person person = new Person();

        person.setUsername(username);
        person.setPassword(password);
        person.setMobile(mobile);
        person.setManagerId(m.getId());
        try {
            managerService.addPerson(person);
            result.put("status", true);
        } catch (DuplicateKeyException e) {
            result.put("status", false);
            result.put("msg", "用户名存在！");
        } catch (Exception e) {
            result.put("status", false);
            result.put("msg", "创建失败");
        }
        return result;
    }

    @ResponseBody
    @PostMapping("/update_person")
    public Map<String, Object> updatePerson(@RequestParam int id, @RequestParam String username,
                                            @RequestParam String password, @RequestParam String mobile) {
        Map<String, Object> result = new HashMap<>();
        username = username.trim();
        mobile = mobile.trim();

        Person person = new Person();
        person.setId(id);
        person.setUsername(username);
        person.setPassword(password);
        person.setMobile(mobile);
        try {
            managerService.updatePerson(person);
            result.put("status", true);
        } catch (DuplicateKeyException e) {
            result.put("status", false);
            result.put("msg", "用户名存在！");
        } catch (Exception e) {
            result.put("status", false);
            result.put("msg", "创建失败");
        }
        return result;
    }

    @ResponseBody
    @PostMapping("/delete_person")
    public Map<String, Object> deletePerson(@RequestParam int id) {
        Map<String, Object> result = new HashMap<>();
        Person person = new Person();
        person.setId(id);
        try {
            managerService.deletePerson(person);
            result.put("status", true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result.put("status", false);
        }
        return result;
    }

    // project
    @GetMapping("/project")
    public String project() {
        return "manager/project";
    }

    public Project getManagerProject(Principal principal) {
        Manager manager = managerService.getManagerByName(principal.getName());
        return managerService.getProjectById(manager.getProjectId());
    }

    public Company getManagerCompany(Principal principal) {
        Project project = getManagerProject(principal);
        return managerService.getCompanyById(project.getCompanyId());
    }

    @ResponseBody
    @PostMapping("/get_project_info")
    public Project getProjectInfo(Principal principal) {
        return getManagerProject(principal);
    }

    @ResponseBody
    @PostMapping("/get_project")
    public List<Map<String, Object>> getProject(Principal principal) {
        Project project = getManagerProject(principal);
        Company company = getManagerCompany(principal);
        try {
            return minioService.listObjects(company.getCompanyBucket(), project.getId() + "/");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
    }

    @ResponseBody
    @GetMapping("/download_project")
    public ResponseEntity<byte[]> downloadProject(@RequestParam String prefix,
                                                  Principal principal, HttpServletRequest request) {
        String bucketName = getManagerCompany(principal).getCompanyBucket();
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
    public Map<String, Object> uploadProject(@RequestParam MultipartFile[] files, @RequestParam String type,
                                             @RequestParam Float start, @RequestParam Float end, Principal principal) {
        Project project = getManagerProject(principal);
        String bucketName = getManagerCompany(principal).getCompanyBucket();
        String formatDate = formatDateInDay(new Date());
        String prefix = String.format("%s/%s/%s", project.getId(), type, formatDate);
        addProcessImpl(formatDate, start, end, types.indexOf(type), principal);
        return responseUtils.uploadMultiFile(files, bucketName, prefix, minioService);
    }

    //上传文件到指定目录
    @ResponseBody
    @PostMapping("/upload_assign_project")
    public Map<String, Object> assignProject(@RequestParam MultipartFile[] files, @RequestParam String type, @RequestParam String date,
                                             @RequestParam Float start, @RequestParam Float end, Principal principal) {
        Project project = getManagerProject(principal);
        String bucketName = getManagerCompany(principal).getCompanyBucket();
        String formatDate = formatDateInDay(formatDateString(date));
        String prefix = String.format("%s/%s/%s", project.getId(), type, formatDate);
        addProcessImpl(formatDate, start, end, types.indexOf(type), principal);
        return responseUtils.uploadMultiFile(files, bucketName, prefix, minioService);
    }

    //删除文件
    @ResponseBody
    @PostMapping("delete_project")
    public Map<String, Object> deleteProject(@RequestParam String prefix, Principal principal) {
        String bucketName = getManagerCompany(principal).getCompanyBucket();
        return responseUtils.deleteObject(minioService, bucketName, prefix);
    }

    //预览文件
    @GetMapping("preview_project")
    public String previewProject(@RequestParam String prefix, Model model, Principal principal) {
        Company company = getManagerCompany(principal);
        try {
            Map<String, Object> map = minioService.getFiles(company.getCompanyBucket(), prefix);
            ByteArrayOutputStream files = (ByteArrayOutputStream) map.get("files");
            String[] split = prefix.split("/");
            model.addAttribute("filename", split[split.length - 1]);
            model.addAttribute("text", files.toString());
            return "manager/preview";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    //Information
    @GetMapping("/information")
    public String information() {
        return "manager/information";
    }

    @ResponseBody
    @PostMapping("/get_information")
    public List<Map<String, Object>> getInformation(Principal principal) {
        Manager m = managerService.getManagerByName(principal.getName());
        try {
            return minioService.listObjects(InformationFile, m.getContractorId().toString() + "/");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
    }

    @ResponseBody
    @GetMapping("/download_information")
    public ResponseEntity<byte[]> downloadInformation(@RequestParam String prefix, HttpServletRequest request) {
        return responseUtils.getMinioFiles(InformationFile, prefix, minioService, request);
    }

    @ResponseBody
    @GetMapping("/preview_information")
    public ResponseEntity<byte[]> previewInformation(@RequestParam String prefix, HttpServletRequest request) {
        ResponseEntity<byte[]> entity = responseUtils.getMinioFiles(InformationFile, prefix, minioService, request);
        HttpHeaders headers = entity.getHeaders();
        return ResponseEntity.ok()
                .header("Content-Type", String.valueOf(headers.getContentType()))
                .body(entity.getBody());
    }

    @ResponseBody
    @PostMapping("/upload_information")
    public Map<String, Object> uploadInformation(@RequestParam MultipartFile[] files, Principal principal) {
        Manager m = managerService.getManagerByName(principal.getName());
        return responseUtils.uploadMultiFile(files, InformationFile, m.getContractorId().toString() + "/", minioService);
    }

    @ResponseBody
    @PostMapping("/delete_information")
    public Map<String, Object> deleteInformation(@RequestParam String prefix) {
        Map<String, Object> result = new HashMap<>();
        try {
            minioService.deleteProject(InformationFile, prefix);
            result.put("status", true);
        } catch (Exception e) {
            result.put("status", false);
        }
        return result;
    }

    //Process
    private Float[] formatProcess(Float start, Float end, Project project) {
        start = start < project.getStart() ? project.getStart() : start;
        end = end > project.getEnd() ? project.getEnd() : end;
        return new Float[]{start, end};
    }

    @GetMapping("/process_total")
    public String processTotal() {
        return "manager/process_total";
    }

    @GetMapping("/process")
    public String process() {
        return "manager/process";
    }

    @ResponseBody
    @PostMapping("list_process")
    public List<Process> processList(@RequestParam Integer type, Principal principal) {
        Manager manager = managerService.getManagerByName(principal.getName());
        Process process = new Process();
        process.setType(type);
        process.setProjectId(manager.getProjectId());
        return managerService.getProcessByTypeAndProject(process);
    }

    @ResponseBody
    @PostMapping("add_process")
    public Map<String, Object> addProcess(@RequestParam Float start, @RequestParam Float end,
                                          @RequestParam Integer type, Principal principal) {
        Map<String, Object> result = new HashMap<>();
        try {
            addProcessImpl(formatDateInDay(new Date()), start, end, type, principal);
            result.put("status", true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result.put("status", false);
        }
        return result;
    }

    public void addProcessImpl(String date, Float start, Float end, Integer type, Principal principal) {
        Manager manager = managerService.getManagerByName(principal.getName());
        Process process = new Process();
        Float[] mileage = formatProcess(start, end, getManagerProject(principal));
        process.setStart(mileage[0]);
        process.setEnd(mileage[1]);
        process.setType(type);
        process.setProjectId(manager.getProjectId());
        process.setDate(date);
        managerService.addProcess(process);
    }

    @ResponseBody
    @PostMapping("update_process")
    public Map<String, Object> updateProcess(@RequestParam Integer id, @RequestParam Float start,
                                             @RequestParam Float end, Principal principal) {
        Process process = new Process();
        Float[] mileage = formatProcess(start, end, getManagerProject(principal));
        process.setId(id);
        process.setStart(mileage[0]);
        process.setEnd(mileage[1]);

        Map<String, Object> result = new HashMap<>();
        try {
            managerService.UpdateProcess(process);
            result.put("status", true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result.put("status", false);
        }
        return result;
    }

    @ResponseBody
    @PostMapping("delete_process")
    public Map<String, Object> deleteProcess(@RequestParam Integer id) {
        Process process = new Process();
        process.setId(id);

        Map<String, Object> result = new HashMap<>();
        try {
            managerService.deleteProcessById(process);
            result.put("status", true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result.put("status", false);
        }
        return result;
    }
}
