package com.scalelable.demo.service;


import com.scalelable.demo.Mapper.*;
import com.scalelable.demo.domain.*;
import com.scalelable.demo.domain.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ManagerService {
    private final ManagerMapper managerMapper;
    private final PersonMapper personMapper;
    private final ContractorMapper contractorMapper;
    private final CompanyMapper companyMapper;
    private final ProjectMapper projectMapper;
    private final ProcessMapper processMapper;

    @Autowired
    public ManagerService(ManagerMapper managerMapper, CompanyMapper companyMapper, ProcessMapper processMapper,
                          PersonMapper personMapper, ContractorMapper contractorMapper, ProjectMapper projectMapper) {
        this.managerMapper = managerMapper;
        this.personMapper = personMapper;
        this.companyMapper = companyMapper;
        this.projectMapper = projectMapper;
        this.contractorMapper = contractorMapper;
        this.processMapper = processMapper;
    }

    //Manager
    public Manager getManagerByName(String manager) {
        return managerMapper.getByName(manager);
    }

    //Contractor
    public Contractor getContractorByUsername(String username) {
        return contractorMapper.getContractorByName(username);
    }

    public Contractor getContractorById(Integer contractorId) {
        return contractorMapper.getById(contractorId);
    }


    //Project and Company
    public Project getProjectById(Integer projectId) {
        return projectMapper.getById(projectId);
    }

    public Company getCompanyById(Integer companyId) {
        Company c = new Company();
        c.setId(companyId);
        return companyMapper.getCompanyById(c);
    }


    //Person
    public List<PersonList> getPersonListByUsername(String username) {
        return personMapper.getPersonListByUsername(username);
    }

    public void addPerson(Person person) {
        personMapper.insertPerson(person);
    }

    public void updatePerson(Person person) {
        personMapper.updatePerson(person);
    }

    public void deletePerson(Person person) {
        personMapper.deletePerson(person);
    }

    //Process
    public List<Process> getProcessByTypeAndProject(Process process) {
        return processMapper.getProcessByTypeAndProject(process);
    }

    public void deleteProcessById(Process process) {
        processMapper.deleteById(process);
    }

    public void UpdateProcess(Process process) {
        processMapper.update(process);
    }

    public void addProcess(Process process) {
        processMapper.insert(process);
    }
//
//    public void addProcessWithDate(Process process,Date date) {
//        processMapper.insertWithDate(process.getStart(),process.getEnd(),process.getType(),process.getProjectId(),date);
//    }


}
