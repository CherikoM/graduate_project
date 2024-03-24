package com.example.project.service;

import com.example.project.dataobject.ArtistHistoryDO;
import com.example.project.dataobject.LabelHistoryDO;
import com.example.project.model.ArtistHistory;
import com.example.project.model.Label;
import com.example.project.model.LabelHistory;

import java.util.List;
import java.util.Map;

public interface LabelService {
    Label getLabelById(Long id);

    List<Label> getLabelsByIds(List<Long> ids);

    int addLabelVersion(LabelHistoryDO labelHistoryDO);

    void getNotAuditLabelVersion();

    LabelHistory dispatchMission();

    int updateLabelVersion(LabelHistoryDO labelHistoryDO);

    boolean auditLabel(Map<String, Object> dataMap);

    List<LabelHistory> getAllLabelVersion(Long labelId, Long userId);

    void labelContributeThanksCount();
}
