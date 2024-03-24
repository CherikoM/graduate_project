package com.example.project.dao;

import com.example.project.dataobject.ArtistHistoryDO;
import com.example.project.dataobject.ReleaseHistoryDO;
import com.example.project.dataobject.StyleHistoryDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface StyleHistoryDAO {

    int addStyleVersion(StyleHistoryDO styleHistoryDO);

    List<StyleHistoryDO> getNotAuditStyleVersion();

    int isAudit(@Param("styleHistoryId") Long styleHistoryId);

    int updateStyleVersion(StyleHistoryDO styleHistoryDO);

    List<StyleHistoryDO> getAllStyleVersion(@Param("styleName") String styleName);

    int updateStyleThanks(@Param("thankMap") Map thankMap);

    long getTodayAuditVersion();

    long getTodayYouAuditVersion(@Param("id")Long id);
}
