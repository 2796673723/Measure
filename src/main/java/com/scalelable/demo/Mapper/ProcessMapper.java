package com.scalelable.demo.Mapper;

import com.scalelable.demo.domain.Process;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface ProcessMapper {
    @Select("SELECT p.* FROM t_process AS p WHERE p.type=#{type} AND p.project_id=#{projectId}")
    List<Process> getProcessByTypeAndProject(Process process);

    @Insert("INSERT INTO t_process(`start`,`end`,type,project_id,date) VALUES(#{start},#{end},#{type},#{projectId},#{date})")
    void insert(Process process);

    @Insert("INSERT INTO t_process(`start`,`end`,type,date,project_id) VALUES(#{start},#{end},#{type},#{date},#{projectId})")
    void insertWithDate(Float start, Float end, Integer type, Integer projectId, Date date);

    @Update("UPDATE t_process SET `start`=#{start},`end`=#{end} WHERE id=#{id}")
    void update(Process process);

    @Delete("DELETE FROM t_process WHERE id=#{id}")
    void deleteById(Process process);
}
