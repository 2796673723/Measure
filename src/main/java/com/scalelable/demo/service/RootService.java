package com.scalelable.demo.service;

import com.scalelable.demo.Mapper.*;
import com.scalelable.demo.domain.Admin;
import com.scalelable.demo.domain.Contractor;
import com.scalelable.demo.domain.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RootService {
    private final AdminMapper adminMapper;
    private final ContractorMapper contractorMapper;

    @Autowired
    public RootService(AdminMapper adminMapper, ContractorMapper contractorMapper) {
        this.adminMapper = adminMapper;
        this.contractorMapper = contractorMapper;
    }

    // Admin
    public List<Admin> getAdmin() {
        return adminMapper.getAdmin();
    }

    @Transactional
    public void addAdmin(Admin admin) {
        adminMapper.addAdmin(admin);
        Admin addAdmin = adminMapper.getAdminByUsername(admin.getUsername());
        adminMapper.addAuthority(addAdmin);
    }

    @Transactional
    public void deleteAdmin(Admin admin) {
        adminMapper.deleteAdmin(admin);
        adminMapper.deleteAuthority(admin);
    }

    public void updateAdmin(Admin admin) {
        adminMapper.updateAdmin(admin);
    }

    //Contractor
    public void addContractor(Contractor contractor) {
        contractorMapper.insertContactor(contractor);
    }
}
