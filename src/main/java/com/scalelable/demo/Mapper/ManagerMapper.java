package com.scalelable.demo.Mapper;

import com.scalelable.demo.domain.Manager;
import com.scalelable.demo.domain.ManagerList;
import com.scalelable.demo.domain.Project;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ManagerMapper {
    @Select("SELECT tm.id AS id,tm.username AS username,u.`password` AS `password`,tm.mobile AS mobile,tc.`name` AS contractor,tp.`name` AS project\n" +
            "FROM t_project AS tp INNER JOIN t_manager AS tm ON tm.project_id = tp.id INNER JOIN t_contractor AS tc\n" +
            "ON tm.contractor_id = tc.id INNER JOIN t_user AS u ON tm.username = u.username WHERE tc.company =#{company}")
    List<ManagerList> getManagerListByCompany(String company);

    @Select("SELECT * FROM t_manager WHERE username=#{username}")
    Manager getByName(String username);

    @Select("SELECT * FROM t_manager WHERE id=#{id}")
    Manager getById(Integer id);

    //Project

    // Manager
    @Insert("INSERT INTO t_manager(username,mobile,contractor_id,project_id) VALUES (#{username},#{mobile},#{contractorId},#{projectId});")
    void insertManagerByUsernameAndContractor(String username, String mobile, Integer contractorId, Integer projectId);

    @Update("UPDATE t_manager SET mobile=#{mobile},contractor_id=#{contractorId},project_id=#{projectId} WHERE id=#{id}")
    void updateByIdNoName(String mobile, Integer contractorId, Integer projectId, Integer id);
}
