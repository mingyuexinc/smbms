package com.kj.service.user;

import com.kj.dao.BaseDao;
import com.kj.dao.user.UserDao;
import com.kj.dao.user.UserDaoImpl;
import com.kj.pojo.User;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService{
    // 业务层都会调用DAO层，所以要引入DAO层
    private UserDao userDao;

    // 无参构造
    public UserServiceImpl(){
        userDao = new UserDaoImpl();  // 当UserServiceImpl实例化时,就会实例化UserDaoImpl
    }

    public User login(String userCode, String password) {
        Connection conn = null;
        User user = null;
        try {
            conn = BaseDao.getConnection();
            // 调用DAO层的方法
            user = userDao.getLoginUser(conn, userCode);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            BaseDao.closeResources(conn,null,null);
        }
        return user;
    }
    public boolean updatePassword(int id, String password) {
        System.out.println("UserService:"+password);
        Connection connection = null;
        boolean flag = false;
        // 修改密码
        try {
            connection = BaseDao.getConnection();
            if (userDao.updatePassword(connection,id,password)>0){  // sql执行成功
                flag = true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            BaseDao.closeResources(connection,null,null);
        }
        return flag;
    }

    public int getUserNum(String username, int userRole) {
        Connection connection = null;
        int userNum = 0;
        try {
            connection = BaseDao.getConnection();
            userNum = userDao.getUserNum(connection, username, userRole);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            BaseDao.closeResources(connection,null,null);
        }
        return userNum;
    }

    // 参数来自前端
    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize) {
        Connection connection = null;
        List<User> userList = null;
        System.out.println("queryUserName ----> "+queryUserName);
        System.out.println("queryUserRole ----> "+queryUserRole);
        System.out.println("currentPageNo ----> "+currentPageNo);
        System.out.println("pageSize ----> "+pageSize);
        try {
            connection = BaseDao.getConnection();
            userList = userDao.getUserList(connection, queryUserName, queryUserRole, currentPageNo, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResources(connection,null,null);
        }
        return userList;
    }

    // 涉及增删改必须使用事务
    public boolean addUser(User user) {
        boolean flag = false;
        Connection connection = null;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);  //开启JDBC事务管理
            int updateRows = userDao.addUser(connection, user);
            connection.commit();  // 提交事务
            if (updateRows>0){
                flag = true;
                System.out.println("add success!");
            }else {
                System.out.println("add failed!");
            }
        } catch (Exception e) {
            System.out.println("rollback================");
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }finally {
                BaseDao.closeResources(connection,null,null);
            }
        }
        return flag;
    }

    public User selectUserCodeExist(String userCode) {
        Connection connection = null;
        User user = null;
        try {
            connection = BaseDao.getConnection();
            user = userDao.getLoginUser(connection, userCode);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            BaseDao.closeResources(connection, null, null);
        }
        return user;
    }

    @Test
    public void test(){
        UserServiceImpl userService = new UserServiceImpl();
        int count = userService.getUserNum(null,2);
        System.out.println(count);
    }
}
