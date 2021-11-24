package com.scalelable.demo.Mapper;

import com.scalelable.demo.domain.Contractor;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ContractorMapper {
    @Select("SELECT * FROM `t_contractor` WHERE company=#{company}")
    List<Contractor> getContractorByCompany(String company);

    @Select("SELECT tc.* FROM t_contractor AS tc INNER JOIN t_company AS tp\n" +
            "ON tc.company = tp.company_name WHERE tc.`name` =#{contractor}  AND tp.company_name =#{company} ")
    Contractor getContractorByNameAndCompany(String contractor, String company);

    @Select("SELECT tc.* FROM t_contractor AS tc INNER JOIN t_manager \n" +
            "AS tm ON tc.id = tm.contractor_id WHERE tm.username =#{username}")
    Contractor getContractorByName(String username);

    @Select("SELECT * FROM t_contractor WHERE id=#{id}")
    Contractor getById(Integer id);

    @Insert("INSERT t_contractor (`name`,contact,mobile,company) VALUES(#{name},#{contact},#{mobile},#{company})")
    void insertContactor(Contractor contractor);

    @Delete("DELETE FROM t_contractor WHERE id=#{id}")
    void deleteById(Integer id);

    @Update("UPDATE t_contractor SET `name`=#{name},contact=#{contact},mobile=#{mobile} WHERE id=#{id}")
    void updateNameById(Contractor contractor);
}
