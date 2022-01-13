package com.kj.service.user;

import com.kj.pojo.User;

import java.sql.Connection;
import java.util.List;

public interface UserService {
    // 用户登录
    public User login(String userCode,String password);

    // 根据用户ID修改密码
    public boolean updatePassword(int id, String password);

    // 查询记录数
    public int getUserNum(String username,int userRole);

    // 根据条件查询用户列表
    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize);

    // 添加用户
    public boolean addUser(User user);

}
