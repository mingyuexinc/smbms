package com.kj.servlet.user;

import com.kj.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 移除用户的Session
        req.getSession().removeAttribute(Constants.USER_SESSION);
        // 页面跳转
        resp.sendRedirect(req.getContextPath()+"/login.jsp");  //正确的首页地址：http://localhost:8080/smbms/login.jsp
        // 如果不加项目路径，则地址为http://localhost:8080/login.jsp,会报404
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
