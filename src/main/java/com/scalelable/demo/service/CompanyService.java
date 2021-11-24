package com.scalelable.demo.service;

import com.scalelable.demo.Mapper.CompanyMapper;
import com.scalelable.demo.domain.Company;
import com.scalelable.demo.utils.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyMapper companyMapper;
    private final MinioService minioService;

    @Autowired
    public CompanyService(CompanyMapper companyMapper, MinioService minioService) {
        this.companyMapper = companyMapper;
        this.minioService = minioService;
        this.minioService.setClientConnect();
    }

    public List<Company> getCompany() {
        return companyMapper.getCompany();
    }

    public void addCompany(Company company) throws Exception {
        minioService.createBucket(company.getCompanyBucket());
        companyMapper.addCompany(company);

    }


    public void updateCompany(Company company) throws Exception {
        companyMapper.updateCompany(company);
    }


    public void deleteCompany(Company company) throws Exception {
        Company companyById = companyMapper.getCompanyById(company);
        minioService.deleteBucket(companyById.getCompanyBucket());
        companyMapper.deleteCompany(company);
    }

}
