package com.scalelable.demo.Mapper;

import com.scalelable.demo.domain.Company;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CompanyMapper {

    @Select("select * from t_company where company_name!='root'")
    List<Company> getCompany();

    @Select("select * from t_company where id=#{id}")
    Company getCompanyById(Company company);

    @Select("select * from t_company where company_name=#{companyName}")
    Company getCompanyByCompanyName(String companyName);

    @Select("SELECT tp.* FROM t_manager AS tm INNER JOIN t_contractor AS tc ON tm.contractor_id = tc.id\n" +
            "INNER JOIN t_company AS tp ON tc.company = tp.company_name WHERE tm.username = #{username}")
    Company getByManagerName(String username);

    @Insert("insert into t_company (company_name, company_bucket) VALUES (#{companyName},#{companyBucket})")
    void addCompany(Company company);

    @Update("update t_company set company_name=#{companyName} where id=#{id}")
    void updateCompany(Company company);

    @Delete("delete from t_company where id=#{id}")
    void deleteCompany(Company company);

}
