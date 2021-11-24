package com.scalelable.demo.Mapper;

import com.scalelable.demo.domain.Admin;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminMapper {
    @Select("select * from t_user where username=#{username}")
    Admin getAdminByUsername(String username);

    @Select("select c.company_bucket from t_company c,t_user u where u.company=c.company_name and u.username=#{username}")
    String getAdminBucketByUsername(String username);

    @Select("SELECT u.* FROM t_user AS u INNER JOIN t_user_authority AS a ON u.id = a.user_id WHERE a.authority_id = 2")
    List<Admin> getAdmin();

    @Insert("insert into t_user (username, password, valid, company) values (#{username},#{password},1,#{company})")
    void addAdmin(Admin admin);

    @Insert("insert into t_user_authority (user_id, authority_id) values (#{id},2)")
    void addAuthority(Admin admin);

    @Delete("delete from t_user where id=#{id}")
    void deleteAdmin(Admin admin);

    @Delete("delete from t_user_authority where user_id=#{id}")
    void deleteAuthority(Admin admin);

    @Update("update t_user set username=#{username},password=#{password},company=#{company} where id=#{id}")
    void updateAdmin(Admin admin);

    @Select("SELECT * FROM t_user WHERE username =#{username}")
    Admin getIdByName(String username);

    @Select("SELECT * FROM t_user WHERE id =#{id}")
    Admin getMangerById(Integer id);

    @Insert("INSERT INTO t_user(username,`password`,valid) VALUES (#{username},#{password},1);")
    void insertUser(String username, String password);

    @Delete("DELETE FROM t_user WHERE username=#{username}")
    void deleteByUsername(String username);
}
