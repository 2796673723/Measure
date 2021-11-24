package com.scalelable.demo;

import com.scalelable.demo.domain.Admin;
import com.scalelable.demo.domain.Company;
import com.scalelable.demo.service.AdminService;
import com.scalelable.demo.service.CompanyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private AdminService adminService;

    @Autowired
    private CompanyService companyService;

    @Test
    void contextLoads() {
    }

}
