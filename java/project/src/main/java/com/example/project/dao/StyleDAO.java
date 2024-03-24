package com.example.project.dao;

import com.example.project.dataobject.ArtistDO;
import com.example.project.dataobject.StyleDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StyleDAO {
    List<StyleDO> getAll();

    List<StyleDO> getAllGenre();

    List<StyleDO> getGenreAndPrimary();

    List<StyleDO> getAllStyle();

    List<StyleDO> getStyleByEnName(@Param("enName")String enName);

    StyleDO getStyleById(@Param("id") Long id);

    List<StyleDO> getStyleBranch(@Param("enName")String enName);

    int updateStyle(StyleDO styleDO);

    int addStyle(StyleDO styleDO);
}
