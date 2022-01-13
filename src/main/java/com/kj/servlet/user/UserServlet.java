package com.kj.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.kj.pojo.Role;
import com.kj.pojo.User;
import com.kj.service.role.RoleServiceImpl;
import com.kj.service.user.UserService;
import com.kj.service.user.UserServiceImpl;
import com.kj.util.Constants;
import com.kj.util.PageSupport;
import com.mysql.jdbc.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 实现Servlet复用
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if (method!=null&&method.equals("savepwd")){  // 跟前端传递的方法进行比对
            this.updatepwd(req,resp);
        }else if (method!=null&&method.equals("pwdmodify")){
            this.checkpwd(req, resp);
        }else if (method!=null&&method.equals("query")){
            this.query(req,resp);
        }else if (method!=null&&method.equals("add")){
            this.add(req,resp);
        }else if (method!=null&&method.equals("getrolelist")){
            this.getRoleList(req,resp);
        }else if (method!=null&&method.equals("ucexist")){
            this.userCodeExist(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    // 重点、难点
    public void query(HttpServletRequest req, HttpServletResponse resp){
        // 查询用户列表
        // 从前端获取数据
        String queryUserName = req.getParameter("queryname");
        String qRole = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");
        int queryUserRole = 0;  // 默认为0

        // 获取用户总数
        UserServiceImpl userService = new UserServiceImpl();
        List<User> userList = null;


        // 第一次走这个请求时,一定是第1页，页面大小固定,当前页固定
        int pageSize = 5;
        int currentPageNo = 1;

        if (queryUserName == null){
            queryUserName = "";
        }
        if (qRole!=null&&!qRole.equals("")){
            queryUserRole = Integer.parseInt(qRole);  // 整型编号返回，0,1,2,3
        }
        if (pageIndex!=null){
            currentPageNo = Integer.parseInt(pageIndex);
        }

        // 获取用户的总数(分页的前提)
        int totalCount = userService.getUserNum(queryUserName, queryUserRole);

        // 设置分页格式
        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        pageSupport.setTotalCount(totalCount);

        int totalPageCount = pageSupport.getTotalPageCount(); //获取总页数

        // 页数合规性判断
        if (currentPageNo<1){
            currentPageNo = 1;
        }else if (currentPageNo>totalPageCount){
            currentPageNo = totalPageCount;
        }

        // 获取用户列表展示
        userList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);
        req.setAttribute("userList",userList);
        
        // 获取角色列表展示
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        req.setAttribute("roleList",roleList);
        req.setAttribute("totalCount",totalCount);
        req.setAttribute("totalPageCount",totalPageCount);
        req.setAttribute("currentPageNo",currentPageNo);
        req.setAttribute("queryUserName",queryUserName);
        req.setAttribute("queryUserRole",queryUserRole);


        // 返回前端
        try {
            req.getRequestDispatcher("userlist.jsp").forward(req,resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    // 修改密码
    public void updatepwd(HttpServletRequest req, HttpServletResponse resp){
        // 从Session中获取ID

        Object obj = req.getSession().getAttribute(Constants.USER_SESSION);
        String newpassword = req.getParameter("newpassword");
        System.out.println("UserServlet:"+newpassword);
        boolean flag = false;
        if (obj!=null && !StringUtils.isNullOrEmpty(newpassword)){
            UserService userService = new UserServiceImpl();
            flag = userService.updatePassword(((User)obj).getId(),newpassword);
            if (flag){
                req.setAttribute("message","密码修改成功，请退出并使用新密码登录！");
                // 移除当前Session
                req.getSession().removeAttribute(Constants.USER_SESSION);
            }else {
                req.setAttribute("message","密码修改失败！");  // 返回前端
            }
        }else{
            req.setAttribute("message","新密码设置有问题！");
        }
        try {
            req.getRequestDispatcher("pwdmodify.jsp").forward(req,resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 验证旧密码
    public void checkpwd(HttpServletRequest req, HttpServletResponse resp){
        //1.获取用户当前密码(session中)和用户输入的旧密码
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String oldpassword = req.getParameter("oldpassword");
        //2.验证用户当前状态(多种情况,参考前端js)
        HashMap<String, String> resultMap = new HashMap<String,String>();
        if (o==null){ //先判断session是否过期
            resultMap.put("result","sessionerror");
        }else if (StringUtils.isNullOrEmpty(oldpassword)){
            resultMap.put("result","error");
        }else{ // 不为空，进行密码验证
            String userPassword = ((User) o).getUserPassword(); // session中的密码
            if (oldpassword.equals(userPassword)){
                resultMap.put("result","true");
            }else {
                resultMap.put("result","false");
            }
        }

        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            // JSONArray 阿里的json工具类，转换格式(Map→Json)
            writer.write(JSONArray.toJSONString(resultMap));  // 实现前后端数据交互
            writer.flush();  // 刷新
            writer.close();  // 关闭
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 添加用户
    private void add(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        String userCode = req.getParameter("userCode");
        String userName = req.getParameter("userName");
        String userPassword = req.getParameter("userPassword");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");

        User user = new User();

        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setGender(Integer.valueOf(gender));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(Integer.valueOf(userRole));
        user.setCreatedBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());

        UserServiceImpl userService = new UserServiceImpl();
        if (userService.addUser(user)){
            resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
        }else {
            req.getRequestDispatcher("useradd.jsp").forward(req,resp);
        }
    }
    // 查询用户角色
    private void getRoleList(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        List<Role> roleList = null;
        RoleServiceImpl roleService = new RoleServiceImpl();
        roleList = roleService.getRoleList();
        //把roleList转换成json对象返回
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(roleList));
        writer.flush();
        writer.close();
    }
    private void userCodeExist(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        //首先我们判断数据库中是否存在该用户usercode，首先得获取到用户输入的userCode
        String userCode = req.getParameter("userCode");
        //万能的Map：结果集
        Map<String, String> map = new HashMap<String, String>();
        //判断输入是否为空
        if (StringUtils.isNullOrEmpty(userCode)) {
            map.put("userCode", "empty");
        } else {
            UserServiceImpl userService = new UserServiceImpl();
            User user = userService.selectUserCodeExist(userCode);
            if (user != null) {
                map.put("userCode", "exist");
            } else {
                map.put("userCode", "notexist");
            }
        }
        //将Map数据转换成json返回
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(map));
        writer.flush();
        writer.close();
    }

}
