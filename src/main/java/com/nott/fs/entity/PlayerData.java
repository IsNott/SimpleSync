package com.nott.fs.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.bukkit.entity.Player;

@Data
@TableName("sys_player_data")
@ToString
public class PlayerData {

    @TableId(value = "uuid")
    private String UUID;

    private Float exp;

    private Double health;

    private String displayName;

    private int level;

    private int foodLevel;

    private String items;

    private int expToLevel;

    private String armorItems;

    public PlayerData() {

    }


    public PlayerData(Player player){
        if(player == null){
            throw new RuntimeException("player cannot be null");
        }
        this.UUID = player.getUniqueId().toString();
        this.exp = player.getExp() >=  0.0F ? player.getExp() : 0;
        this.displayName = player.getDisplayName();
        this.expToLevel = (player.getExpToLevel() >= 0 ? player.getExpToLevel() : 0);
        this.health = player.getHealth();
        this.foodLevel = player.getFoodLevel() >= 0 ? player.getFoodLevel() : 0;
    }
}
