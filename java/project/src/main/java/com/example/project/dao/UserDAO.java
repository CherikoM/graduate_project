package com.example.project.dao;

import com.example.project.dataobject.UserDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserDAO {

    UserDO getUserById(@Param("userId") Long userId);

    UserDO getUserByMailAdd(@Param("mail") String mail);

    int addUser(UserDO userDO);

    List<Map> getUserContribute(@Param("userId") Long userId);

    List<Map> getFriendList(@Param("ids") List ids);

    int updateContributePoint(@Param("userMap")Map userMap);

    int updateFollow(@Param("followMap")Map followMap);

    int updateFan(@Param("fanMap")Map fanMap);

    int updateUser(UserDO userDO);

}
