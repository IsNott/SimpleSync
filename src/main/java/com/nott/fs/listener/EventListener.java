package com.nott.fs.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.nott.fs.dao.PlayerDao;
import com.nott.fs.entity.PlayerData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

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
            playerData = new PlayerData();
            playerData.setUUID(uuid);
            playerData.setDisplayName(player.getDisplayName());
            playerDao.insertPlayerData(playerData);
        } else {
            // 如果数据存在，则从数据库中加载玩家数据并同步到游戏中
            syncPlayerData(playerData, player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        double health = player.getHealth();
        float exp = player.getExp();
        String displayName = player.getDisplayName();
        String uuid = player.getUniqueId().toString();
        int level = player.getLevel();
        int foodLevel = player.getFoodLevel();
        PlayerInventory inventory = player.getInventory();
        ItemStack[] storageContents = inventory.getStorageContents();
        JSONArray itemArrays = new JSONArray();
        //todo 序列号和反序列化
        for (ItemStack content : storageContents) {
            JSONObject itemJson = new JSONObject();
            int amount = content.getAmount();
            String name = content.getType().name();
            short durability = content.getDurability();
            Map<Enchantment, Integer> map = content.getEnchantments();
            if(!map.isEmpty()){
                JSONArray enchArray = new JSONArray();
                Set<Enchantment> keySet = map.keySet();
                keySet.forEach(key -> {
                    JSONObject en = new JSONObject();
                    en.put("level",map.get(key));
                    en.put("enchantment",key);
                    enchArray.add(en);
                });
                itemJson.put("enchantmentList",enchArray);
            }
            itemJson.put("amount",amount);
            itemJson.put("durability",durability);
            itemJson.put("name",name);
            itemArrays.add(itemJson);
        }
        String itemsMetaJsonStr = JSON.toJSONString(itemArrays);
        // 更新玩家数据到数据库
        PlayerData playerData = playerDao.getPlayerDataByUUID(uuid);
        if (playerData != null) {
            playerData.setItems(itemsMetaJsonStr);
            playerData.setHealth(health);
            playerData.setExp(exp);
            playerData.setLevel(level);
            playerData.setFoodLevel(foodLevel);
            playerDao.updatePlayerById(playerData);
        } else {
            PlayerData newPlayerData = new PlayerData(uuid, exp, health, displayName, level, foodLevel, itemsMetaJsonStr);
            playerDao.insertPlayerData(newPlayerData);
        }
    }

    private void syncPlayerData(PlayerData playerData, Player player) {
        // 同步玩家数据到游戏中
        player.setDisplayName(playerData.getDisplayName());
        player.setHealth(playerData.getHealth());
        player.setExp(playerData.getExp());
        player.setLevel(playerData.getLevel());
        player.setFoodLevel(playerData.getFoodLevel());
        PlayerInventory inventory = player.getInventory();
        //todo 同步PlayerInventory
        String items = playerData.getItems();
        JSONArray itemArrays = JSON.parseArray(items);
    }
}
