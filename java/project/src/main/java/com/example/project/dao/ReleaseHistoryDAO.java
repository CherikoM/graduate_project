package com.example.project.dao;

import com.example.project.dataobject.ArtistHistoryDO;
import com.example.project.dataobject.ReleaseHistoryDO;
import com.example.project.model.ReleaseHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReleaseHistoryDAO {

    int addReleaseVersion(ReleaseHistoryDO releaseHistoryDO);

    List<ReleaseHistoryDO> getNotAuditReleaseVersion();

    int isAudit(@Param("releaseHistoryId") Long releaseHistoryId);

    int updateReleaseVersion(ReleaseHistoryDO releaseHistoryDO);

    List<ReleaseHistoryDO> getAllReleaseVersion(@Param("releaseId") Long releaseId);

    int updateReleaseThanks(@Param("thankMap") Map thankMap);

    long getTodayAuditVersion();

    long getTodayYouAuditVersion(@Param("id")Long id);
}
