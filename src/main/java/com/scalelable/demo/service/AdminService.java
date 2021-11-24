package com.scalelable.demo.service;

import com.scalelable.demo.Mapper.*;
import com.scalelable.demo.domain.*;
import com.scalelable.demo.domain.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {
    private final AdminMapper adminMapper;
    private final ManagerMapper managerMapper;
    private final ProjectMapper projectMapper;
    private final UAInfoMapper uaInfoMapper;
    private final ContractorMapper contractorMapper;
    private final CompanyMapper companyMapper;
    private final ProcessMapper processMapper;


    @Autowired
    public AdminService(AdminMapper adminMapper, ManagerMapper managerMapper, ContractorMapper contractorMapper,
                        ProjectMapper projectMapper, UAInfoMapper uaInfoMapper, CompanyMapper companyMapper,
                        ProcessMapper processMapper) {
        this.adminMapper = adminMapper;
        this.managerMapper = managerMapper;
        this.contractorMapper = contractorMapper;
        this.projectMapper = projectMapper;
        this.uaInfoMapper = uaInfoMapper;
        this.companyMapper = companyMapper;
        this.processMapper = processMapper;
    }

    public Admin getAdminByUsername(String username) {
        return adminMapper.getAdminByUsername(username);
    }

    public String getAdminBucketByUsername(String username) {
        return adminMapper.getAdminBucketByUsername(username);
    }

    //Company
    public Company getCompanyByCompanyName(String companyName) {
        return companyMapper.getCompanyByCompanyName(companyName);
    }

    //Project
    public Project getProjectByNameAndCompanyId(String project, Integer companyId) {
        return projectMapper.getByNameAndCompanyId(project, companyId);
    }

    public Project getProjectById(Integer projectId) {
        return projectMapper.getById(projectId);
    }

    public List<Project> getProjects(String company) {
        return projectMapper.getByCompanyName(company);
    }

    public void createProject(Project project) {
        projectMapper.insertByCompanyName(project);
    }

    public void updateProject(Project project) {
        projectMapper.updateById(project);
    }

    public void delProjectById(Integer id) {
        projectMapper.delById(id);
    }


    //Contractor
    public List<Contractor> getContractorByCompany(String company) {
        return contractorMapper.getContractorByCompany(company);
    }

    public Contractor getContractorByNameAndCompany(String contractor, String company) {
        return contractorMapper.getContractorByNameAndCompany(contractor, company);
    }

    public void addContractor(Contractor contractor) {
        contractorMapper.insertContactor(contractor);
    }

    public void updateContractor(Contractor contractor) {
        contractorMapper.updateNameById(contractor);
    }

    public void deleteContractor(Integer id) {
        contractorMapper.deleteById(id);
    }

    //Manager
    public List<ManagerList> getManagerByCompany(String company) {
        return managerMapper.getManagerListByCompany(company);
    }

    public Manager getManagerById(Integer id) {
        return managerMapper.getById(id);
    }

    @Transactional
    public void addManager(String username, String password, String mobile, Integer contractorId, Integer projectId) {
        adminMapper.insertUser(username, password);
        Admin admin = adminMapper.getIdByName(username);
        uaInfoMapper.insertUserAuthorityByUserId(admin.getId());
        managerMapper.insertManagerByUsernameAndContractor(username, mobile, contractorId, projectId);
    }

    @Transactional
    public void updateManager(int id, Admin admin, String mobile,
                              Integer contractorId, Integer projectId) {
        adminMapper.updateAdmin(admin);
        managerMapper.updateByIdNoName(mobile, contractorId, projectId, id);
    }

    @Transactional
    public void deleteManager(Integer id) {
        Manager manager = managerMapper.getById(id);
        adminMapper.deleteByUsername(manager.getUsername());
    }

    //Process
    public List<Process> getProcessByTypeAndProject(Process process) {
        return processMapper.getProcessByTypeAndProject(process);
    }

    public List<Project> getProjectProcess(List<Project> projects, List<String> types) {
        for (Project project : projects) {
            float sum = 0;
            double d = project.getEnd() - project.getStart();
            for (int i = 0; i < types.size(); i++) {
                Process process = new Process();
                process.setType(i);
                process.setProjectId(project.getId());
                List<Process> processes = getProcessByTypeAndProject(process);
                if (processes.size() == 0) continue;
                for (Process p : MergeProcess(processes)) {
                    sum += (p.getEnd() - p.getStart()) * 100 / d;
                }
            }
            project.setProgress(sum / types.size());
        }
        return projects;
    }

    private ArrayList<Process> MergeProcess(List<Process> processes) {
        processes.sort((a, b) -> {
            if (a.getStart().equals(b.getStart())) return 0;
            return a.getStart() < b.getStart() ? -1 : 1;
        });

        ArrayList<Process> mergeDate = new ArrayList<>();
        float pre_start = processes.get(0).getStart();
        float pre_end = processes.get(0).getEnd();
        for (int i = 1; i < processes.size(); i++) {
            Process data = processes.get(i);
            if (data.getStart() > pre_end) {
                mergeDate.add(new Process(data.getStart(), data.getEnd()));
                pre_start = data.getStart();
                pre_end = data.getEnd();
                continue;
            }
            pre_end = Math.max(pre_end, data.getEnd());
        }
        mergeDate.add(new Process(pre_start, pre_end));
        return mergeDate;
    }


}
