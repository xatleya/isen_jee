package yncrea.pw01.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@WebFilter(urlPatterns = "/*")
public class SessionFilter implements Filter {
    public SessionFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        if(Arrays.asList("/login","/index.jsp").contains(req.getServletPath())||
                req.getSession().getAttribute("loggedPharmacist")!=null){
            filterChain.doFilter(req,servletResponse);
        }else if(req.getSession().getAttribute("loggedPharmacist")==null){
            res.sendRedirect(req.getServletContext().getContextPath()+"/");
        }
    }

    @Override
    public void destroy() {

    }
}
