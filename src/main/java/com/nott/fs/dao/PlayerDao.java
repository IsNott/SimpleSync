package com.nott.fs.dao;

import com.nott.fs.entity.PlayerData;
import com.nott.fs.mapper.PlayerDataMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class PlayerDao {
    private final SqlSessionFactory sqlSessionFactory;

    public PlayerDao(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public PlayerData getPlayerDataByUUID(String uuid) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            System.out.println("openSession");
            PlayerDataMapper playerDataMapper = sqlSession.getMapper(PlayerDataMapper.class);
            System.out.println(playerDataMapper.toString());
            return playerDataMapper.getPlayerDataByUUID(uuid);
        }
    }

    public void insertPlayerData(PlayerData playerData) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            PlayerDataMapper playerDataMapper = sqlSession.getMapper(PlayerDataMapper.class);
            playerDataMapper.insertPlayerData(playerData);
        }
    }

    public void updatePlayerById(PlayerData playerData) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            PlayerDataMapper playerDataMapper = sqlSession.getMapper(PlayerDataMapper.class);
            playerDataMapper.updateById(playerData);
        }
    }
}
