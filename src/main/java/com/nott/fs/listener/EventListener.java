package com.nott.fs.listener;

import com.nott.fs.dao.PlayerDao;
import com.nott.fs.entity.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

/**
 * @author Nott
 * @Date 2023/6/29
 */


public class EventListener implements Listener {

    private PlayerDao playerDao;

    private Plugin plugin;

    public EventListener(PlayerDao playerDao) {
        this.playerDao = playerDao;
    }

    public EventListener(PlayerDao playerDao, Plugin plugin) {
        this.playerDao = playerDao;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        plugin.getLogger().info(String.format("player %s join,UUID [%s]",player.getDisplayName(),uuid));
        // 检查数据库中是否存在该玩家的数据
        PlayerData playerData = playerDao.getPlayerDataByUUID(uuid);
        if (playerData == null) {
            // 如果数据不存在，则创建一条新的玩家数据
            playerData = new PlayerData(uuid, player.getDisplayName(), player.getHealth(), player.getExp());
            playerDao.insertPlayerData(playerData);
        } else {
            // 如果数据存在，则从数据库中加载玩家数据并同步到游戏中
            syncPlayerData(playerData, player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        // 更新玩家数据到数据库
        PlayerData playerData = playerDao.getPlayerDataByUUID(uuid);
        if (playerData != null) {
            playerData.setHealth(player.getHealth());
            playerData.setExp(player.getExp());
            playerDao.updatePlayerById(playerData);
        }
    }

    private void syncPlayerData(PlayerData playerData, Player player) {
        // 同步玩家数据到游戏中
        player.setDisplayName(playerData.getDisplayName());
        player.setHealth(playerData.getHealth());
        player.setExp(playerData.getExp());
    }
}
