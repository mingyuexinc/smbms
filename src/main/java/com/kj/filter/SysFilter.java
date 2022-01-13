package com.kj.filter;

import com.kj.pojo.User;
import com.kj.util.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SysFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
        // 转换成HTTP的servlet,这样才能拿到用户session
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        // 从session获取user
        User user = (User) request.getSession().getAttribute(Constants.USER_SESSION);
        if (user==null){ // 移除，注销或者未登录
            response.sendRedirect("/smbms/error.jsp"); // 注意，error要放在jsp外
        }else{
            filterChain.doFilter(request,response);
        }
    }

    public void destroy() {

    }
}
