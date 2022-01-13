package com.kj.dao.user;

import com.kj.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    // 得到登录的用户
    public User getLoginUser(Connection connection,String userCode) throws SQLException;

    // 修改用户密码
    public int updatePassword(Connection connection,int id,String password) throws SQLException;

    // 根据用户名或角色查询用户总数
    public int getUserNum(Connection connection,String username,int userRole) throws SQLException;

    // 通过条件查询userList
    public List<User> getUserList(Connection connection,String username,int userRole,int currentPageNo,int pageSize) throws Exception;

    // 添加用户
    public int addUser(Connection connection,User user) throws Exception;
}
