package com.nott.FastSync;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.nott.FastSync.entity.PlayerData;
import com.nott.FastSync.mapper.PlayerDataMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.yaml.snakeyaml.Yaml;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class MyPlugin extends JavaPlugin implements Listener {
    private DataSource dataSource;
    private PlayerDataMapper playerDataMapper;
    private MybatisSqlSessionFactoryBean sessionFactoryBean;

    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void onEnable() {
        // 连接到数据库
        connectToDatabase();

        // 获取数据源（这里假设你已经创建了连接池）
        DataSource dataSource = getDataSource();

        // 创建 MyBatis 的会话工厂
        sessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);

        // 设置 MyBatis 配置文件路径
        try {
            sessionFactoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("mybatis-config.xml"));
        } catch (Exception e) {
            getLogger().info(e.getMessage());
            throw e;
        }

        // 注册事件监听器
        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        // 检查数据库中是否存在该玩家的数据
        PlayerData playerData = playerDataMapper.selectById(uuid);
        if (playerData == null) {
            // 如果数据不存在，则创建一条新的玩家数据
            playerData = new PlayerData(uuid, player.getDisplayName(), player.getHealth(), player.getExp());
            playerDataMapper.insert(playerData);
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
        PlayerData playerData = playerDataMapper.selectById(uuid);
        if (playerData != null) {
            playerData.setHealth(player.getHealth());
            playerData.setExp(player.getExp());
            playerDataMapper.updateById(playerData);
        }
    }

    private void connectToDatabase() {
        // 读取 application.yml 文件
        InputStream inputStream = getResource("application.yml");

        // 使用 YAML 解析库加载配置文件
        Yaml yaml = new Yaml();
        Map<String, Object> config = yaml.load(inputStream);

        // 从配置文件中提取连接参数
        String driverClassName = (String) config.get("datasource.driverClassName");
        String url = (String) config.get("datasource.url");
        String username = (String) config.get("datasource.username");
        String password = (String) config.get("datasource.password");
        // 使用Durid连接池创建数据源
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(driverClassName);
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);

        // 设置其他连接池配置...

        // 将数据源设置为插件的数据源
        dataSource = druidDataSource;

        getLogger().info("Connected to the database!");
    }



    private void syncPlayerData(PlayerData playerData, Player player) {
        // 同步玩家数据到游戏中
        player.setDisplayName(playerData.getDisplayName());
        player.setHealth(playerData.getHealth());
        player.setExp(playerData.getExp());
    }
}