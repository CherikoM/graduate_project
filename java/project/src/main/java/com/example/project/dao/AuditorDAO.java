package com.example.project.dao;

import com.example.project.dataobject.AuditorDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface AuditorDAO {

    AuditorDO getAuditorById(Long id);

    AuditorDO getAuditorByUserId(Long id);

    int updateAuditPoint(@Param("auditorMap") Map auditorMap);
}
