package com.example.project.dao;

import com.example.project.dataobject.ArtistDO;
import com.example.project.dataobject.ReleaseDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArtistDAO {

    List<ArtistDO> search(
            @Param("keyword") String keyword,
            @Param("order") int order
    );

    ArtistDO getArtistById(@Param("id") Long id);

    List<ArtistDO> getArtistsByIds(@Param("ids") List<Long> ids);

    int updateArtist(ArtistDO artistDO);

    int addArtist(ArtistDO artistDO);



}
/*
    @Param和@RequestParam的区别
    @Param是Mapper层，为了解决Mybatis字段与在后端Java字段不一致的情况，相当于解决后端名字间的映射关系
    @RequestParam是Controller层，为了解决前端（表单提交）传入数据与在后端Java字段不一致的情况，相当于解决前端名字间的映射关系
 */