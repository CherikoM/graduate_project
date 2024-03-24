package com.example.project.dao;

import com.example.project.dataobject.ArtistDO;
import com.example.project.dataobject.LabelDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LabelDAO {
    List<LabelDO> search(
            @Param("keyword") String keyword,
            @Param("order") int order
    );

    LabelDO getLabelById(@Param("id") Long id);

    List<LabelDO> getChildrenById(@Param("id") Long id);

    List<LabelDO> getLabelsByIds(@Param("ids") List<Long> ids);

    int updateLabel(LabelDO labelDO);

    int addLabel(LabelDO labelDO);

}
