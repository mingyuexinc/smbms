package com.kj.filter;

import javax.servlet.*;
import java.io.IOException;

public class characterEncodingFilter implements Filter {
    public characterEncodingFilter() {
        super();
    }

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletRequest.setCharacterEncoding("utf-8");
        servletResponse.setCharacterEncoding("utf-8");

        filterChain.doFilter(servletRequest,servletResponse);
    }

    public void destroy() {

    }
}
