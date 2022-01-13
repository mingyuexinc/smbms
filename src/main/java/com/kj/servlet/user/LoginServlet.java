package com.kj.servlet.user;

import com.kj.pojo.User;
import com.kj.service.user.UserService;
import com.kj.service.user.UserServiceImpl;
import com.kj.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    // servlet：控制层调用业务层代码
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("LoginServlet--start...");
        // 获取用户名和密码(参数根据前端确定)
        String userCode = req.getParameter("userCode");
        String userPassword = req.getParameter("userPassword");
        // 跟数据库中的密码进行比对
        UserService userService = new UserServiceImpl();
        User user = userService.login(userCode, userPassword);
        if (user!=null&&userCode.equals(user.getUserCode())
                &&userPassword.equals(user.getUserPassword())){
            // 将用户的信息存放到session中
            req.getSession().setAttribute(Constants.USER_SESSION,user);
            // 跳转到主页
            resp.sendRedirect("jsp/frame.jsp");
        }else {
            // 转发回登录页面，顺带提示用户名或密码错误
            req.setAttribute("error","用户名或密码错误！");
            req.getRequestDispatcher("login.jsp").forward(req,resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
