package com.scalelable.demo.Mapper;

import com.scalelable.demo.domain.Person;
import com.scalelable.demo.domain.PersonList;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PersonMapper {
    @Select("select * from t_person")
    List<Person> getPerson();

    @Select("select * from t_person where username=#{username} and password=#{password}")
    Person getPersonByUsernameAndPassword(Person person);

    @Select("SELECT p.id,p.username,p.`password`,p.mobile,j.`name` as project\n"+
            "FROM t_person AS p INNER JOIN t_manager AS m ON p.manager_id = m.id\n" +
            "INNER JOIN t_project AS j ON m.project_id = j.id WHERE m.username = #{username}")
    List<PersonList> getPersonListByUsername(String username);

    @Select("select * from t_person where id=#{id}")
    Person getById(Integer id);

    @Insert("INSERT INTO t_person (username,password,mobile,manager_id) values " +
            "(#{username},#{password},#{mobile},#{managerId})")
    void insertPerson(Person person);

    @Delete("delete from t_person where id=#{id}")
    int deletePerson(Person person);

    @Update("update t_person set username=#{username},password=#{password},mobile=#{mobile} where id=#{id}")
    int updatePerson(Person person);
}
