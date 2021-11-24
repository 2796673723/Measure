package com.scalelable.demo.Mapper;

import com.scalelable.demo.domain.PCInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PCInfoMapper {
    @Select("SELECT tp.id AS id,tp.`name` AS `name`,tc.company_name FROM\tt_manager AS tm\n" +
            "INNER JOIN t_manager_project AS tmp ON tm.id = tmp.manager_id\n" +
            "INNER JOIN t_project AS tp ON tmp.project_id = tp.id\n" +
            "INNER JOIN t_company AS tc ON tp.company_id = tc.id \n" +
            "WHERE tm.username = #{manager}")
    List<PCInfo> getProjectAndCompanyByManagerName(String manager);
}
