package com.example.project.service;

import com.example.project.common.Paging;
import com.example.project.dataobject.MainReleaseDO;
import com.example.project.dataobject.ReleaseDO;
import com.example.project.dataobject.ReleaseHistoryDO;
import com.example.project.model.MainRelease;
import com.example.project.model.Release;
import com.example.project.model.ReleaseHistory;

import java.util.List;
import java.util.Map;

public interface ReleaseService {

    Release getReleaseById(Long id);

    List<Release> getReleasesByIds(List<Long> ids);

    Map getArtistReleaseCount(Long artistId);

    Paging<Release> getArtistRelease(Long artistId, int pageNum, int pageSize);

    Paging<Release> getArtistFeatured(Long artistId, int pageNum, int pageSize);

    Paging<Release> getArtistBehind(Long artistId, int pageNum, int pageSize);

    Paging<Release> getLabelRelease(Long labelId, int pageNum, int pageSize);

    MainRelease getMainReleaseById(Long id);

    Paging<MainRelease> getMainReleaseByKeyword(String keyword, int pageNum, int pageSize);

    int addReleaseVersion(ReleaseHistoryDO releaseHistoryDO);

    Long addMainRelease(MainReleaseDO mainReleaseDO);

    void getNotAuditReleaseVersion();

    ReleaseHistory dispatchMission();

    int updateReleaseVersion(ReleaseHistoryDO releaseHistoryDO);

    int updateRelease(ReleaseDO releaseDO);

    Long addRelease(ReleaseDO releaseDO);

    List<ReleaseHistory> getAllReleaseVersion(Long releaseId, Long userId);

    boolean auditRelease(Map<String, Object> dataMap);

    void releaseContributeThanksCount();
}
