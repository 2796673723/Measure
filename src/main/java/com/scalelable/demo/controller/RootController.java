package com.scalelable.demo.controller;

import com.scalelable.demo.domain.Admin;
import com.scalelable.demo.domain.Company;
import com.scalelable.demo.domain.Contractor;
import com.scalelable.demo.service.AdminService;
import com.scalelable.demo.service.CompanyService;
import com.scalelable.demo.service.RootService;
import org.apache.commons.codec.binary.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


@Controller
@RequestMapping("/root")
public class RootController {
    private final RootService rootService;
    private final CompanyService companyService;

    @Autowired
    public RootController(CompanyService companyService, RootService rootService) {
        this.companyService = companyService;
        this.rootService = rootService;
    }

    @GetMapping("/admin")
    public String personList() {
        return "root/person";
    }

    @ResponseBody
    @PostMapping("/get_admin")
    public List<Admin> getPerson() {
        return rootService.getAdmin();
    }

    @ResponseBody
    @PostMapping("/add_admin")
    public Map<String, Object> addAdmin(@RequestParam String username, @RequestParam String password,
                                        @RequestParam String company) {
        Map<String, Object> result = new HashMap<>();
        Admin admin = new Admin();

        admin.setUsername(username.trim());
        admin.setPassword(password);
        admin.setCompany(company.trim());
        try {
            rootService.addAdmin(admin);
            result.put("status", true);
        } catch (DuplicateKeyException e) {
            result.put("status", false);
            result.put("msg", "用户名存在！");
        } catch (DataIntegrityViolationException e) {
            result.put("status", false);
            result.put("msg", "公司名称不存在！");
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            result.put("status", false);
            result.put("msg", "创建失败");
        }
        return result;
    }

    @ResponseBody
    @PostMapping("/update_admin")
    public Map<String, Object> updateAdmin(@RequestParam String username, @RequestParam String password,
                                           @RequestParam String company, @RequestParam int id) {
        Map<String, Object> result = new HashMap<>();

        Admin admin = new Admin();

        admin.setId(id);
        admin.setUsername(username.trim());
        admin.setPassword(password);
        admin.setCompany(company.trim());
        try {
            rootService.updateAdmin(admin);
            result.put("status", true);
        } catch (DuplicateKeyException e) {
            result.put("status", false);
            result.put("msg", "用户名存在！");
        } catch (DataIntegrityViolationException e) {
            result.put("status", false);
            result.put("msg", "公司名称不存在！");
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            result.put("status", false);
        }
        return result;
    }

    @ResponseBody
    @PostMapping("/delete_admin")
    public Map<String, Object> deleteAdmin(@RequestParam int id) {
        Map<String, Object> result = new HashMap<>();
        Admin admin = new Admin();
        admin.setId(id);
        try {
            rootService.deleteAdmin(admin);
            result.put("status", true);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            result.put("status", false);
        }
        return result;
    }

    @GetMapping("/company")
    public String companyList() {
        return "root/company";
    }

    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(25);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    @ResponseBody
    @PostMapping("/get_company")
    public List<Company> getCompany() {
        return companyService.getCompany();
    }

    @ResponseBody
    @PostMapping("/add_company")
    public Map<String, Object> addCompany(@RequestParam String companyName) {
        Map<String, Object> result = new HashMap<>();
        companyName = companyName.trim();

        Company company = new Company();
        company.setCompanyName(companyName);

        Base32 base32 = new Base32();
        String bucketName = base32.encodeToString(companyName.getBytes()).replace("=", "").toLowerCase();
        int len = Math.min(bucketName.length(), 55);
        bucketName = bucketName.substring(0, len) + getRandomString(5);
        System.out.println(bucketName);
        company.setCompanyBucket(bucketName);

        Contractor contractor = new Contractor();
        contractor.setName(company.getCompanyName());
        contractor.setCompany(company.getCompanyName());
        try {
            companyService.addCompany(company);
            rootService.addContractor(contractor);
            result.put("status", true);
        } catch (DuplicateKeyException e) {
            result.put("status", false);
            result.put("msg", "名称已经存在！");
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            result.put("status", false);
            result.put("msg", "操作失败！");
        }
        return result;
    }

    @ResponseBody
    @PostMapping("/update_company")
    public Map<String, Object> updateCompany(@RequestParam String companyName, @RequestParam Integer id) {
        Map<String, Object> result = new HashMap<>();

        Company companyObj = new Company();

        companyObj.setId(id);
        companyObj.setCompanyName(companyName.trim());

        try {
            companyService.updateCompany(companyObj);
            result.put("status", true);
        } catch (DataIntegrityViolationException e) {
            result.put("status", false);
            result.put("msg", "公司名重复");
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            result.put("status", false);
        }
        return result;
    }

    @ResponseBody
    @PostMapping("/delete_company")
    public Map<String, Object> deleteCompany(@RequestParam Integer id) {
        Map<String, Object> result = new HashMap<>();

        Company companyObj = new Company();
        companyObj.setId(id);

        try {
            companyService.deleteCompany(companyObj);
            result.put("status", true);
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            result.put("status", false);
        }
        return result;
    }
}
