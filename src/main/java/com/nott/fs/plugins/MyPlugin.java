package com.nott.fs.plugins;

import com.alibaba.druid.pool.DruidDataSource;
import com.nott.fs.dao.PlayerDao;
import com.nott.fs.listener.EventListener;
import com.nott.fs.mapper.PlayerDataMapper;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.bukkit.plugin.java.JavaPlugin;

import org.yaml.snakeyaml.Yaml;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Map;

public class MyPlugin extends JavaPlugin{

    private DataSource dataSource;
    private PlayerDataMapper playerDataMapper;
    private SqlSessionFactory sqlSessionFactory;
    private PlayerDao playerDao;
    @Override
    public void onEnable() {
        // 获取数据源（这里假设你已经创建了连接池）
        XMLConfigBuilder xmlConfigBuilder;
        Configuration configuration;
        SqlSessionFactory factory;
        // 创建 MyBatis 的会话工厂
        //sessionFactoryBean = new MybatisSqlSessionFactoryBean();
        //sessionFactoryBean.setDataSource(dataSource);

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
//            System.out.println(sqlSessionFactory.getConfiguration());
            playerDao = new PlayerDao(sqlSessionFactory);
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
}