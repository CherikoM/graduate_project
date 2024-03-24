package com.example.project.service;

import com.example.project.dataobject.ArtistHistoryDO;
import com.example.project.model.Artist;
import com.example.project.model.ArtistHistory;

import java.util.List;
import java.util.Map;

public interface ArtistService {

    Artist getArtistById(Long id);

    List<Artist> getArtistsByIds(List<Long> ids);

    int addArtistVersion(ArtistHistoryDO artistHistoryDO);

    void getNotAuditArtistVersion();

    ArtistHistory dispatchMission();

    int updateArtistVersion(ArtistHistoryDO artistHistoryDO);

    boolean auditArtist(Map<String, Object> dataMap);

    List<ArtistHistory> getAllArtistVersion(Long artistId, Long userId);

    void artistContributeThanksCount();
}
