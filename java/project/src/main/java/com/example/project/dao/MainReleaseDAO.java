package com.example.project.dao;

import com.example.project.dataobject.MainReleaseDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MainReleaseDAO {
    MainReleaseDO getMainReleaseById(@Param("id")Long id);

    List<MainReleaseDO> search(@Param("keyword")String keyword);

    int addMainRelease(MainReleaseDO mainReleaseDO);
}
