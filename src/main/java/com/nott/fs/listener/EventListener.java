package com.nott.fs.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.nott.fs.dao.PlayerDao;
import com.nott.fs.entity.PlayerData;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import javax.sound.midi.MetaEventListener;
import java.util.*;

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

        if(playerDao == null){
            throw new RuntimeException("playerDao is null!");
        }

        // 检查数据库中是否存在该玩家的数据
        PlayerData playerData = playerDao.getPlayerDataByUUID(uuid);
        if (playerData == null) {
            // 如果数据不存在，则创建一条新的玩家数据
            playerData = new PlayerData(player);
            playerDao.insertPlayerData(playerData);
            plugin.getLogger().info(String.format("Insert player [%s] data [%s]",player.getDisplayName(),playerData.toString()));
        } else {
            plugin.getLogger().info(String.format("Got player [%s] data [%s]",player.getDisplayName(),playerData.toString()));
            // 如果数据存在，则从数据库中加载玩家数据并同步到游戏中
            syncPlayerData(playerData, player);
        }
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,5F,0F);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        int expToLevel = player.getExpToLevel();
        double health = player.getHealth();
        float exp = player.getExp();
        String uuid = player.getUniqueId().toString();
        int level = player.getLevel();
        int foodLevel = player.getFoodLevel();

        PlayerInventory inventory = player.getInventory();
        ItemStack[] storageContents = inventory.getStorageContents();
        ItemStack[] armorContents = inventory.getArmorContents();
        ItemStack itemInMainHand = inventory.getItemInMainHand();
        JSONArray itemArrays = new JSONArray();
        JSONArray armorArrays = new JSONArray();
        // set playerData's storageContents
        for (ItemStack content : storageContents) {
           if(content != null){
               JSONObject itemJson = this.getItemJson(content,itemInMainHand);
               itemArrays.add(itemJson);
           }
        }
        // set playerData's armorContent
        for (ItemStack content : armorContents) {
            if(content != null){
                JSONObject itemJson = this.getItemJson(content,null);
                armorArrays.add(itemJson);
            }
        }
        String armorArrayJsonStr = JSON.toJSONString(armorArrays);
        String itemsMetaJsonStr = JSON.toJSONString(itemArrays);
        plugin.getLogger().info("itemsMetaJsonStr :"+ itemsMetaJsonStr);
        plugin.getLogger().info("armorArrayJsonStr :"+ armorArrayJsonStr);
        // 更新玩家数据到数据库
        PlayerData playerData = playerDao.getPlayerDataByUUID(uuid);
        if (playerData != null) {
            playerData.setItems(itemsMetaJsonStr);
            playerData.setHealth(health);
            playerData.setDisplayName(player.getDisplayName());
            playerData.setExp(exp);
            playerData.setLevel(level);
            playerData.setExpToLevel(expToLevel);
            playerData.setFoodLevel(foodLevel);
            playerData.setArmorItems(armorArrayJsonStr);
            playerDao.updatePlayerById(playerData);
        }
    }

    private JSONObject getItemJson(ItemStack content,ItemStack itemInMainHand) {
        plugin.getLogger().info("ItemStack :" + content.toString());
        JSONObject itemJson = new JSONObject();
        int amount = content.getAmount();
        String name = content.getType().name();
        boolean hasItemMeta = content.hasItemMeta();
        if(hasItemMeta){
            JSONObject metaJson = new JSONObject();
            ItemMeta meta = content.getItemMeta();
            plugin.getLogger().info("ItemMeta :" + meta.toString());
            boolean hasDisplayName = meta.hasDisplayName();
            boolean hasEnchants = meta.hasEnchants();
            boolean hasLore = meta.hasLore();
            boolean unbreakable = meta.isUnbreakable();
            if(hasEnchants){
                Map<Enchantment, Integer> map = meta.getEnchants();
                JSONArray enchArray = new JSONArray();
                Set<Enchantment> keySet = map.keySet();
                keySet.forEach(key -> {
                    JSONObject en = new JSONObject();
                    en.put("level",map.get(key));
                    en.put("enchantment",key);
                    enchArray.add(en);
                });
                metaJson.put("enchants",enchArray);
            }
            if(hasDisplayName){
                metaJson.put("displayName",meta.getDisplayName());
            }
            if(hasLore){
                //todo addLore
            }
            if(!unbreakable){
                short durability = content.getDurability();
                itemJson.put("durability",String.valueOf(durability));
            }
            itemJson.put("meta",metaJson);
        }
        if(itemInMainHand != null){
            if(content.equals(itemInMainHand)){
                itemJson.put("inHand","Y");
            }
        }
        itemJson.put("amount",amount);
        itemJson.put("name",name);
        return itemJson;
    }

    private void syncPlayerData(PlayerData playerData, Player player) {
        Double health = playerData.getHealth();
        Float exp = playerData.getExp();
        int foodLevel = playerData.getFoodLevel();
        ItemStack itemInHand = null;
        // 同步玩家数据到游戏中
        player.setDisplayName(playerData.getDisplayName());
        if(health > 0.0D){
            player.setHealth(health);
        }
        if (exp > 0.0f){
            player.setExp(exp);
        }
        if(foodLevel > 0){
            player.setFoodLevel(foodLevel);
        }
        player.setLevel(playerData.getLevel());

        String items = playerData.getItems();
        String armorItem = playerData.getArmorItems();
        PlayerInventory inventory = player.getInventory();
        // sync storageContents
        if(StringUtils.isNotEmpty(items)){
            JSONArray itemsArray = JSONArray.parseArray(items);
            Iterator<Object> iterator = itemsArray.iterator();
            ArrayList<ItemStack> itemStacks = new ArrayList<>();
            while (iterator.hasNext()){
                JSONObject itemJson = (JSONObject) iterator.next();
                ItemStack itemStack = deserializeFromDb(itemJson);

                if(itemJson.containsKey("inHand") && "Y".equals(itemJson.getString("inHand"))){
                    itemInHand = itemStack;
                }
                itemStacks.add(itemStack);
            }
            if(!itemStacks.isEmpty()){
                inventory.setContents(itemStacks.toArray(new ItemStack[itemStacks.size()]));
            }

            if(itemInHand != null){
                inventory.setItemInMainHand(itemInHand);
            }
        }
        // sync armorContents
        if(StringUtils.isNotEmpty(armorItem)){
            JSONArray itemsArray = JSONArray.parseArray(armorItem);
            Iterator<Object> iterator = itemsArray.iterator();
            ArrayList<ItemStack> armorItemStacks = new ArrayList<>();
            while (iterator.hasNext()){
                JSONObject itemJson = (JSONObject) iterator.next();
                ItemStack itemStack = deserializeFromDb(itemJson);
                armorItemStacks.add(itemStack);
            }
            if(!armorItemStacks.isEmpty()){
                for (ItemStack armorItemStack : armorItemStacks) {
                    String name = armorItemStack.getData().getItemType().name();
                    if(name.contains("CHESTPLATE")){
                        inventory.setChestplate(armorItemStack);
                    }
                    else if(name.contains("BOOT")){
                        inventory.setBoots(armorItemStack);
                    }
                    else if(name.contains("LEGGINGS")){
                        inventory.setLeggings(armorItemStack);
                    } else {
                        inventory.setHelmet(armorItemStack);
                    }
                }
            }

        }

    }

    private ItemStack deserializeFromDb(JSONObject itemJson) {
        boolean isUnBreakAble = itemJson.containsKey("durability") && StringUtils.isNotEmpty(itemJson.getString("durability"));
        boolean hasMeta = itemJson.containsKey("meta");
        ItemMeta itemMeta = null;
        Material material = Material.getMaterial(itemJson.getString("name"));
        if(hasMeta){
            plugin.getLogger().info(String.format("current item [%s] has meta",material.name()));
            JSONObject meta = itemJson.getJSONObject("meta");
            itemMeta = Bukkit.getItemFactory().getItemMeta(material);
            itemMeta.setDisplayName(meta.containsKey("displayName")? meta.getString("displayName") : null);
            plugin.getLogger().info(String.format("displayName: %s",itemMeta.getDisplayName()));
            if(meta.containsKey("enchants")){
                JSONArray enchants = meta.getJSONArray("enchants");
                Iterator<Object> enchantIterator = enchants.iterator();

                while (enchantIterator.hasNext()){
                    JSONObject enchant = (JSONObject) enchantIterator.next();
                    JSONObject enchantment = enchant.getJSONObject("enchantment");
                    Enchantment en = Enchantment.getByName(enchantment.getString("name"));
                    if(en != null){
                        plugin.getLogger().info(String.format("en: %s",en.toString()));
                        itemMeta.addEnchant(en,enchant.getIntValue("level"),false);
                    }
                }
            }
            plugin.getLogger().info(String.format("current meta [%s]",itemMeta.toString()));
        }
        String amount = itemJson.getString("amount");
        ItemStack itemStack = new ItemStack(material, Integer.parseUnsignedInt(amount));
        if(!isUnBreakAble){
            String durability = itemJson.getString("durability");
            if(StringUtils.isNotEmpty(durability)){
                itemStack.setDurability(Short.parseShort(durability));
            }
        }

        if(itemMeta != null){
            boolean applicable = Bukkit.getItemFactory().isApplicable(itemMeta, itemStack);
            plugin.getLogger().info(String.format("current meta applicable %s",applicable));
            boolean setItemMeta = itemStack.setItemMeta(applicable ? itemMeta : null);
            if(!setItemMeta){
                throw new RuntimeException("setItemMeta error");
            }
        }
        return itemStack;
    }
}
