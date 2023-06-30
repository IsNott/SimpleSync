package com.nott.fs.dao;

import com.nott.fs.entity.PlayerData;
import com.nott.fs.mapper.PlayerDataMapper;
import com.nott.fs.plugins.MyPlugin;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class PlayerDao {
    private final SqlSessionFactory sqlSessionFactory;

    private MyPlugin myPlugin;

    public PlayerDao(SqlSessionFactory sqlSessionFactory,MyPlugin myPlugin) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.myPlugin = myPlugin;
    }

    public PlayerData getPlayerDataByUUID(String uuid) {
        PlayerDataMapper playerDataMapper = null;
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            playerDataMapper = sqlSession.getMapper(PlayerDataMapper.class);
        } catch (Exception e){
            myPlugin.getLogger().info("get playerData error :"+e.getMessage());
            throw e;
        }
        return playerDataMapper.getPlayerDataByUUID(uuid);
    }

    public void insertPlayerData(PlayerData playerData) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            PlayerDataMapper playerDataMapper = sqlSession.getMapper(PlayerDataMapper.class);
            playerDataMapper.insertPlayerData(playerData);
        } catch (Exception e){
            myPlugin.getLogger().info("insert playerData error :"+e.getMessage());
            throw e;
        }
    }

    public void updatePlayerById(PlayerData playerData) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            PlayerDataMapper playerDataMapper = sqlSession.getMapper(PlayerDataMapper.class);
            playerDataMapper.updateById(playerData);
        } catch (Exception e){
            myPlugin.getLogger().info("update playerData error :"+e.getMessage());
            throw e;
        }
    }
}
