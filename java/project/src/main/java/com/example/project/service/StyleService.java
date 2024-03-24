package com.example.project.service;

import com.example.project.common.Result;
import com.example.project.dataobject.ArtistHistoryDO;
import com.example.project.dataobject.StyleHistoryDO;
import com.example.project.model.ArtistHistory;
import com.example.project.model.Style;
import com.example.project.model.StyleHistory;

import java.util.List;
import java.util.Map;

public interface StyleService {

    List<Map> getAllGenre();

    List<Map> getAllStyle();

    List<Map> getGenreAndPrimary();

    List<Style> styleGrouper();

    Style getStyleByEnName(String enName);

    Style getStyleById(Long id);

    int addStyleVersion(StyleHistoryDO styleHistoryDO);

    void getNotAuditStyleVersion();

    StyleHistory dispatchMission();

    int updateStyleVersion(StyleHistoryDO styleHistoryDO);

    boolean auditStyle(Map<String, Object> dataMap);

    List<StyleHistory> getAllStyleVersion(String styleName, Long userId);

    void styleContributeThanksCount();
}
