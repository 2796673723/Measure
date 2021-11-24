package com.scalelable.demo.controller;

import com.scalelable.demo.domain.Company;
import com.scalelable.demo.domain.Person;
import com.scalelable.demo.domain.Process;
import com.scalelable.demo.domain.Project;
import com.scalelable.demo.service.CompanyService;
import com.scalelable.demo.utils.MinioService;
import com.scalelable.demo.service.PersonService;
import com.scalelable.demo.utils.JWTUtils;
import com.scalelable.demo.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/client")
public class ClientController {
    private final PersonService personService;
    private final MinioService minioService;
    private final JWTUtils jwtUtils;

    @Autowired
    public ClientController(PersonService personService, MinioService minioService, JWTUtils jwtUtils) {
        this.personService = personService;
        this.minioService = minioService;
        this.jwtUtils = jwtUtils;
        this.minioService.setClientConnect();
    }

    @GetMapping("/login")
    public Map<String, Object> login(Person person) {
        Map<String, Object> result = new HashMap<>();

        Person login = personService.login(person);
        if (login != null) {
            Map<String, String> payload = new HashMap<>();
            Project project = personService.getProject(login);
            Company company = personService.getCompany(login);
            payload.put("id", login.getId().toString());
            payload.put("username", login.getUsername());
            payload.put("companyBucket", company.getCompanyBucket());
            payload.put("project", project.getName());
            payload.put("projectId", String.valueOf(project.getId()));
            String token = jwtUtils.getToken(payload);
            //put response
            result.put("status", true);
            result.put("token", token);
            result.put("company", company.getCompanyName());
            result.put("project", project.getName());
        } else {
            result.put("status", false);
        }
        return result;
    }

    @PostMapping("/test")
    public Map<String, Object> test(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", true);
        System.out.println(request.getAttribute("companyBucket"));
        System.out.println(request.getAttribute("project"));
        return result;
    }

    @PostMapping("uploadZIP")
    public Map<String, Object> uploadZIP(@RequestParam MultipartFile file, HttpServletRequest request,
                                         @RequestParam Integer type, @RequestParam Float start, @RequestParam Float end) {
        Map<String, Object> result = new HashMap<>();
        String companyBucket = request.getAttribute("companyBucket").toString();
        String prefix = request.getAttribute("project").toString();
        String projectId = request.getAttribute("projectId").toString();
        try {
            minioService.uploadZipFile(companyBucket, prefix, projectId, file);
            Process process = new Process();
            process.setType(type);
            process.setProjectId(Integer.valueOf(projectId));
            process.setStart(start);
            process.setEnd(end);
            personService.addProcess(process);
            result.put("status", true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result.put("status", false);
        }
        return result;
    }

    @PostMapping("upload")
    public Map<String, Object> upload(@RequestParam MultipartFile file, @RequestParam String prefix,
                                      HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        String companyBucket = request.getAttribute("companyBucket").toString();
        String project = request.getAttribute("project").toString();
        prefix = String.format("%s/%s", project, prefix);
        try {
            minioService.uploadFile(companyBucket, prefix, file);
            result.put("status", true);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            result.put("status", false);
        }
        return result;
    }

    @GetMapping("list")
    public Map<String, Object> list(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        String companyBucket = request.getAttribute("companyBucket").toString();
        String prefix = request.getAttribute("project").toString();
        System.out.println(companyBucket + "\n" + prefix);
        try {

            List<Map<String, Object>> objects = minioService.listObjects(companyBucket, prefix);
            result.put("objects", objects);
            result.put("status", true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result.put("status", false);
        }
        return result;
    }
}
