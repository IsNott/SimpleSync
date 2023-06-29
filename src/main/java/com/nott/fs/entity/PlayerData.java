package com.nott.fs.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_player_data")
public class PlayerData {

    @TableId(value = "uuid")
    private String UUID;

    private Float exp;

    private Double health;

    private String displayName;

    public PlayerData(String UUID, String displayName, Double health,Float exp) {
        this.UUID = UUID;
        this.exp = exp;
        this.health = health;
        this.displayName = displayName;
    }
}
