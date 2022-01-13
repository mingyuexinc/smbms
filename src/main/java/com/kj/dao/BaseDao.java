package com.kj.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

// 操作数据库的公共类
public class BaseDao {
    private static String driver;
    private static String url;
    private static String userName;
    private static String passWord;


    static {
        // 通过类加载器读取资源
        Properties properties = new Properties();
        InputStream inStream = BaseDao.class.getClassLoader().getResourceAsStream("db.properties");

        try {
            properties.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        driver = properties.getProperty("driver");
        url = properties.getProperty("url");
        userName = properties.getProperty("user");
        passWord = properties.getProperty("password");

    }
    // 获取数据库的连接
    public static Connection getConnection(){
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, userName, passWord);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
    // 编写查询公共类
    public static ResultSet execute(Connection connection,PreparedStatement preparedStatement,ResultSet resultSet,String sql,Object[] params) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i+1,params[i]);
        }
        resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    // 编写增删改公共方法
    public static int execute(Connection connection,PreparedStatement preparedStatement,String sql,Object[] params) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i+1,params[i]);
        }
        int updateRows = preparedStatement.executeUpdate();
        return updateRows;
    }

    // 释放资源
    public static boolean closeResources(Connection connection,PreparedStatement preparedStatement,ResultSet resultSet){
        boolean flag = true;
        if (resultSet!=null){
            try {
                resultSet.close();
                // GC回收
                resultSet = null;  // 置为空
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                flag = false;   // 关闭失败
            }
        }

        if (connection!=null){
            try {
                connection.close();
                // GC回收
                connection = null;  // 置为空
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                flag = false;   // 关闭失败
            }
        }

        if (preparedStatement!=null){
            try {
                preparedStatement.close();
                // GC回收
                preparedStatement = null;  // 置为空
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                flag = false;   // 关闭失败
            }
        }
        return flag;
    }
}
