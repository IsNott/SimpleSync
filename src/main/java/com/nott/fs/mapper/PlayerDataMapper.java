package com.nott.fs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nott.fs.entity.PlayerData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PlayerDataMapper extends BaseMapper<PlayerData> {

    PlayerData getPlayerDataByUUID(@Param(value = "uuid") String uuid);

    void insertPlayerData(PlayerData playerData);

    int updatePlayerByUUID(PlayerData playerData);

    int deletePlayer(String uuid);
}
