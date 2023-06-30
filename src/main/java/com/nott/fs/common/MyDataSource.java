package com.nott.fs.common;

import com.alibaba.druid.pool.DruidDataSource;
import org.yaml.snakeyaml.Yaml;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Map;

/**
 * @author Nott
 * @Date 2023/6/27
 */



public class MyDataSource {

    private DataSource dataSource;

    public DataSource getDataSource() {
        if(dataSource == null){
            return createDataSource();
        }
        return dataSource;
    }

    public static DataSource createDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://your_database_url");
        dataSource.setUsername("your_username");
        dataSource.setPassword("your_password");

        // 进行其他连接池配置，如最大连接数、最小空闲连接数等

        return dataSource;
    }
}
