package com.kj.dao.user;

import com.kj.dao.BaseDao;
import com.kj.pojo.User;
import com.mysql.jdbc.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {
    public User getLoginUser(Connection connection, String userCode) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        User user = null;

        String sql = "select * from smbms_user where userCode=?";
        Object[] params={userCode};
        // 确保连接
        if (connection!=null){
            rs = BaseDao.execute(connection, pstm, rs, sql, params);
            if (rs.next()){  // 如果查询结果不为空
                // 将查询的结果封装到用户里面
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setUserPassword(rs.getString("userPassword"));
                user.setId(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserRole(rs.getInt("userRole"));
                user.setCreatedBy(rs.getInt("createdBy"));
                user.setCreationDate(rs.getTimestamp("creationDate"));
                user.setModifyBy(rs.getInt("modifyBy"));
                user.setModifyDate(rs.getTimestamp("modifyDate"));
            }
            BaseDao.closeResources(null,pstm,rs);
        }
        return user;
    }
    public int updatePassword(Connection connection, int id, String password) throws SQLException {
        System.out.println("UserDaoImpl:"+password);
        PreparedStatement pstm = null;
        int execute = 0;
        if (connection!=null){
            Object params[] = {password,id};  // 顺序要一致！！
            String sql = "update smbms_user set userPassword= ? where id= ?";
            execute = BaseDao.execute(connection, pstm, sql, params);
            BaseDao.closeResources(connection,null,null);
        }
        return execute;
    }

    public int getUserNum(Connection connection, String username, int userRole) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int count = 0;
        if (connection!=null){
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as count from smbms_user u,smbms_role r where u.userRole=r.id");
            ArrayList<Object> list = new ArrayList<Object>();

            // 由于存在根据用户名、角色名进行查询的多种可能，因此要查询的不同情况拼接sql
            if (!StringUtils.isNullOrEmpty(username)){
                sql.append(" and u.username like ?"); //
                list.add("%" + username + "%");
            }
            if (userRole>0){
                sql.append(" and u.userRole = ?");
                list.add(userRole);
            }
            Object[] params = list.toArray();
            System.out.println("UserDao->getUseNum:"+sql.toString()); // 检查sql是否写对
            // 执行sql
            rs = BaseDao.execute(connection, pstm, rs, sql.toString(), params);
            if (rs.next()){
                count = rs.getInt("count");
            }
            BaseDao.closeResources(null,pstm,rs);
        }
        return count;
    }

    public List<User> getUserList(Connection connection, String username, int userRole, int currentPageNo, int pageSize) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<User> userList = new ArrayList<User>();
        if (connection!=null){
            StringBuffer sql = new StringBuffer();
            sql.append("select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.userRole = r.id");
            ArrayList<Object> list = new ArrayList<Object>();
            // 由于存在根据用户名、角色名进行查询的多种可能，因此要查询的不同情况拼接sql
            if (!StringUtils.isNullOrEmpty(username)){
                sql.append(" and u.username like ? "); //
                list.add("%"+username+"%");
            }
            if (userRole>0){
                sql.append(" and u.userRole = ? ");
                list.add(userRole);
            }
            // 在数据库中，分页使用limit startIndex,pageSize
            // 格式：0,5 01234  5,5 56789
            sql.append(" order by creationDate DESC limit ?,?");
            currentPageNo = (currentPageNo-1)*pageSize;  // 当前页第一个数据的下标
            list.add(currentPageNo);
            list.add(pageSize);
            Object[] params = list.toArray();
            rs = BaseDao.execute(connection, pstm, rs, sql.toString(), params);
            while(rs.next()){
                User _user = new User();
                _user.setId(rs.getInt("ID"));
                _user.setUserCode(rs.getString("userCode"));
                _user.setUserName(rs.getString("userName"));
                _user.setGender(rs.getInt("gender"));
                _user.setBirthday(rs.getDate("birthday"));
                _user.setPhone(rs.getString("phone"));
                _user.setUserRole(rs.getInt("userRole"));
                _user.setUserRoleName(rs.getString("userRoleName"));
                userList.add(_user);
            }
            BaseDao.closeResources(null,pstm,rs);
        }
        return userList;
    }

    public int addUser(Connection connection, User user) throws Exception {
        PreparedStatement pstm = null;
        int updateRows = 0;
        if (connection!=null){
            String sql = "insert into smbms_user (userCode,userName,userPassword,"+
                    "gender,birthday,phone,address,userRole,createdBy,creationDate)"+
                    "values (?,?,?,?,?,?,?,?,?,?)";
            Object[] params = {user.getUserCode(),user.getUserName(),user.getUserPassword(),
                    user.getGender(),user.getBirthday(),user.getPhone(),user.getAddress(),
                    user.getUserRole(),user.getCreatedBy(),user.getCreationDate()};
            updateRows = BaseDao.execute(connection,pstm,sql,params);
            BaseDao.closeResources(null,pstm,null);
        }
        return updateRows;
    }
}
