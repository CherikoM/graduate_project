package com.example.project.dao;

import com.example.project.dataobject.ArtistDO;
import com.example.project.dataobject.LabelDO;
import com.example.project.dataobject.ReleaseDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReleaseDAO {
    List<ReleaseDO> search(
            @Param("keyword") String keyword,
            @Param("genre") List<String> genre,
            @Param("style") List<String> style,
            @Param("format") List<String> format,
            @Param("form") List<String> form,
            @Param("country") List<String> country,
            @Param("decadeMap")Map<String, String> decadeMap,
            @Param("order") int order
    );

    ReleaseDO getReleaseById(@Param("id") Long id);

    List<ReleaseDO> getReleasesByIds(@Param("ids") List<Long> ids);

    List<ReleaseDO> getReleaseVersions(@Param("mainId") Long id);

    List<ReleaseDO> getArtistRelease(@Param("artistId") Long artistId);

    List<ReleaseDO> getArtistFeatured(@Param("artistId") Long artistId);

    List<ReleaseDO> getArtistBehind(@Param("artistId") Long artistId);

    int getArtistReleasesCount(@Param("artistId") Long artistId);

    int getArtistFeaturedCount(@Param("artistId") Long artistId);

    int getArtistBehindCount(@Param("artistId") Long artistId);

    List<ReleaseDO> getLabelRelease(@Param("labelId") Long labelId);

    int updateRelease(ReleaseDO releaseDO);

    int addRelease(ReleaseDO releaseDO);
}
