package com.nott.FastSync.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nott.FastSync.entity.PlayerData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PlayerDataMapper extends BaseMapper<PlayerData> {
    @Select("")
    PlayerData getPlayerDataByUUID(String uuid);

    @Select("")
    void insertPlayerData(PlayerData playerData);
}
