package com.scalelable.demo.Mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UAInfoMapper {
    @Insert("INSERT INTO t_user_authority(user_id,authority_id) VALUES (#{id},3);")
    void insertUserAuthorityByUserId(Integer id);

    @Delete("DELETE FROM t_user_authority WHERE user_id=#{id}")
    void deleteUserAuthorityByUserId(Integer id);
}
