package com.nott.FastSync.common;

import com.alibaba.druid.pool.DruidDataSource;
import com.nott.FastSync.Application;
import javafx.scene.chart.PieChart;

import javax.sql.DataSource;

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
