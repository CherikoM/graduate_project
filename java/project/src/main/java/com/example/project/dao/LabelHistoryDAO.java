package com.example.project.dao;

import com.example.project.dataobject.ArtistHistoryDO;
import com.example.project.dataobject.LabelHistoryDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface LabelHistoryDAO {

    int addLabelVersion(LabelHistoryDO labelHistoryDO);

    List<LabelHistoryDO> getNotAuditLabelVersion();

    int isAudit(@Param("labelHistoryId") Long labelHistoryId);

    int updateLabelVersion(LabelHistoryDO labelHistoryDO);

    List<LabelHistoryDO> getAllLabelVersion(@Param("labelId") Long labelId);

    int updateLabelThanks(@Param("thankMap") Map thankMap);

    long getTodayAuditVersion();

    long getTodayYouAuditVersion(@Param("id")Long id);
}
