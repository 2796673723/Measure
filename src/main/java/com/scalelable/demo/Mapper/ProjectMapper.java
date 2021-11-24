package com.scalelable.demo.Mapper;


import com.scalelable.demo.domain.Project;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProjectMapper {
    @Select("SELECT tp.* FROM t_project AS tp INNER JOIN t_company AS tm \n" +
            "ON tp.company_id = tm.id WHERE tm.company_name =#{company}")
    List<Project> getByCompanyName(String company);

    @Select("SELECT tp.* FROM t_project AS tp INNER JOIN t_company AS tc ON tp.company_id = tc.id \n" +
            "INNER JOIN t_manager AS tm INNER JOIN t_manager_project AS tmp\n" +
            "ON tm.id = tmp.manager_id AND tp.id = tmp.project_id WHERE\n" +
            "tm.username =#{username} AND tc.company_name =#{company} AND tp.`name` =#{project}")
    Project getByNames(String username, String company, String project);

    @Select("SELECT * FROM t_project AS t WHERE t.`name`=#{project} AND t.company_id=#{companyId}")
    Project getByNameAndCompanyId(String project, Integer companyId);

    @Select("SELECT * FROM t_project AS t WHERE t.id=#{id}")
    Project getById(Integer id);

    @Insert("INSERT INTO t_project(`name`,`start`,`end`,company_id) VALUES(#{name},#{start},#{end},#{companyId});")
    void insertByCompanyName(Project project);

    @Update("UPDATE t_project SET `name`=#{name},`start`=#{start},`end`=#{end} WHERE id=#{id}")
    void updateById(Project project);

    @Delete("DELETE FROM t_project WHERE id=#{id}")
    void delById(Integer id);
}
