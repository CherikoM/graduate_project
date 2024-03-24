package com.example.project.dao;

import com.example.project.dataobject.StyleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

@Mapper
public interface SensitiveWordDAO {
    Set<String> getWords();
}




