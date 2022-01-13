package com.kj.service.role;

import com.kj.dao.BaseDao;
import com.kj.dao.role.RoleDao;
import com.kj.dao.role.RoleDaoImpl;
import com.kj.pojo.Role;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class RoleServiceImpl implements RoleService{
    private RoleDao roleDao;

    public RoleServiceImpl() {
        roleDao = new RoleDaoImpl();
    }

    public List<Role> getRoleList() {
        Connection connection = null;
        List<Role> roleList = null;
        try {
            connection = BaseDao.getConnection();
            roleList = roleDao.getRoleList(connection);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            BaseDao.closeResources(connection,null,null);
        }
        return roleList;
    }
    @Test
    public void test(){
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
//        System.out.println(Arrays.toString(roleList.toArray()));
        for (Role role : roleList) {
            System.out.println(role.getRoleName());
        }
    }
}
