package com.nott.FastSync.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nott.FastSync.entity.PlayerData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PlayerDataMapper extends BaseMapper<PlayerData> {

    @Select("select * from sys_player_data where uuid = #{uuid}")
    PlayerData getPlayerDataByUUID(@Param(value = "uuid") String uuid);


    void insertPlayerData(PlayerData playerData);
}
