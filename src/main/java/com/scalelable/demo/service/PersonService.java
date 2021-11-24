package com.scalelable.demo.service;

import com.scalelable.demo.Mapper.*;
import com.scalelable.demo.domain.Company;
import com.scalelable.demo.domain.Manager;
import com.scalelable.demo.domain.Person;
import com.scalelable.demo.domain.Process;
import com.scalelable.demo.domain.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {
    private final PersonMapper personMapper;
    private final ProjectMapper projectMapper;
    private final CompanyMapper companyMapper;
    private final ManagerMapper managerMapper;
    private final ProcessMapper processMapper;

    @Autowired
    public PersonService(PersonMapper personMapper, ProjectMapper projectMapper, ProcessMapper processMapper,
                         CompanyMapper companyMapper, ManagerMapper managerMapper) {
        this.personMapper = personMapper;
        this.projectMapper = projectMapper;
        this.companyMapper = companyMapper;
        this.managerMapper = managerMapper;
        this.processMapper = processMapper;
    }

    public List<Person> getPerson() {
        return personMapper.getPerson();
    }

    public Person login(Person person) {
        return personMapper.getPersonByUsernameAndPassword(person);
    }

    public Project getProject(Person person) {
        Manager manager = managerMapper.getById(person.getManagerId());
        return projectMapper.getById(manager.getProjectId());
    }

    //Company
    public Company getCompany(Person person) {
        Company company = new Company();
        company.setId(getProject(person).getCompanyId());
        return companyMapper.getCompanyById(company);
    }

    //Process
    public void addProcess(Process process) {
        processMapper.insert(process);
    }
}
