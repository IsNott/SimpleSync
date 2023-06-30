package com.nott.fs.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.Data;
import org.apache.ibatis.annotations.ConstructorArgs;

@Data
@TableName("sys_player_data")
public class PlayerData {

    @TableId(value = "uuid")
    private String UUID;

    private Float exp;

    private Double health;

    private String displayName;

    private int level;

    private int foodLevel;

    private String items;

    public PlayerData() {

    }

    public PlayerData(String UUID, String displayName, Double health, Float exp) {
        this.UUID = UUID;
        this.exp = exp;
        this.health = health;
        this.displayName = displayName;
    }

    public PlayerData(String UUID, Float exp, Double health, String displayName, int level, int foodLevel, String items) {
        this.UUID = UUID;
        this.exp = exp;
        this.health = health;
        this.displayName = displayName;
        this.level = level;
        this.foodLevel = foodLevel;
        this.items = items;
    }
}
