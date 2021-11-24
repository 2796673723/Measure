package com.scalelable.demo.controller;

import com.scalelable.demo.domain.Contractor;
import com.scalelable.demo.domain.Manager;
import com.scalelable.demo.domain.Project;
import com.scalelable.demo.service.AdminService;
import com.scalelable.demo.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Collection;


@Controller
public class HelloController {
    private final AdminService adminService;
    private final ManagerService managerService;

    @Autowired
    public HelloController(AdminService adminService, ManagerService managerService) {
        this.adminService = adminService;
        this.managerService = managerService;
    }

    @GetMapping("/")
    public String index(Authentication authentication, Model model, Principal principal) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities.contains(new SimpleGrantedAuthority("ROLE_root"))) {
            return root();
        } else {
            return admin(model, principal);
        }
    }

    @GetMapping("/root")
    public String root() {
        return "root/index";
    }

    @GetMapping("/admin")
    public String admin(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("company", adminService.getAdminByUsername(principal.getName()).getCompany());
        return "admin/index";
    }

    @GetMapping("/manager")
    public String manager(Model model, Principal principal) {
        Manager manager = managerService.getManagerByName(principal.getName());
        model.addAttribute("username", manager.getUsername());
        Contractor c = managerService.getContractorById(manager.getContractorId());
        model.addAttribute("contractor", c.getName());
        model.addAttribute("project", managerService.getProjectById(manager.getProjectId()).getName());
        model.addAttribute("company", c.getCompany());
        return "manager/index";
    }

    @GetMapping("/common")
    public String common() {
        return "common";
    }

    @GetMapping("/userLogin")
    public String login() {
        return "login";
    }
}
