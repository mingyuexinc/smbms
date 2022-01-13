package com.kj.dao.role;

import com.kj.dao.BaseDao;
import com.kj.pojo.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleDaoImpl implements RoleDao {

    public List<Role> getRoleList(Connection connection) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        ArrayList<Role> roleList = new ArrayList<Role>();  // 由于有多个角色，因此要放到数组中
        if (connection!=null){
            Object[] params = {};
            String sql = "select * from smbms_role";
            rs = BaseDao.execute(connection, pstm, rs, sql, params);
            while (rs.next()){
                Role _role = new Role();
                _role.setId(rs.getInt("id"));
                _role.setRoleCode(rs.getString("roleCode"));
                _role.setRoleName(rs.getString("roleName"));
                roleList.add(_role);
            }
            BaseDao.closeResources(null,pstm,rs);
        }

        return roleList;
    }
}
