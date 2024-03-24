package com.example.project.dao;

import com.example.project.dataobject.ArtistHistoryDO;
import com.example.project.dataobject.ReleaseHistoryDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArtistHistoryDAO {

    int addArtistVersion(ArtistHistoryDO artistHistoryDO);

    List<ArtistHistoryDO> getNotAuditArtistVersion();

    int isAudit(@Param("artistHistoryId") Long artistHistoryId);

    int updateArtistVersion(ArtistHistoryDO artistHistoryDO);

    List<ArtistHistoryDO> getAllArtistVersion(@Param("artistId") Long artistId);

    int updateArtistThanks(@Param("thankMap") Map thankMap);

    long getTodayAuditVersion();

    long getTodayYouAuditVersion(@Param("id")Long id);

}
