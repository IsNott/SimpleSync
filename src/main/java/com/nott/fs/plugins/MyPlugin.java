package com.nott.fs.plugins;

import com.nott.fs.dao.PlayerDao;
import com.nott.fs.listener.EventListener;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.bukkit.plugin.java.JavaPlugin;
import javax.sql.DataSource;



public class MyPlugin extends JavaPlugin{

    private SqlSessionFactory sqlSessionFactory;
    private PlayerDao playerDao;

    @Override
    public void onEnable() {
        XMLConfigBuilder xmlConfigBuilder;
        Configuration configuration;
        SqlSessionFactory factory;

        // 设置 MyBatis 配置文件路径
        try {
            getLogger().info("ready to load mybatis");
             xmlConfigBuilder = new XMLConfigBuilder(getResource("mybatis-config.xml"));
             configuration = xmlConfigBuilder.parse();
            DataSource dataSource = configuration.getEnvironment().getDataSource();
            TransactionFactory transactionFactory = new JdbcTransactionFactory();
            Environment environment = new Environment("development", transactionFactory, dataSource);
            configuration.setEnvironment(environment);
            factory = new SqlSessionFactoryBuilder().build(configuration);
            sqlSessionFactory = factory;
            playerDao = new PlayerDao(sqlSessionFactory,this);
            getLogger().info("mybatis loaded");
        } catch (Exception e) {
            getLogger().info(e.getMessage());
        }

        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new EventListener(playerDao,this), this);

        getLogger().info("fs Plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled!");
    }

}