package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *检查用户是否已经完成了登陆
 **/

@Slf4j
@WebFilter(urlPatterns = "/*",filterName = "LoginCheckFilter")
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        /*
         * 1、获取本次请求的URI
         * 2、判断本次请求是否需要处理
         * 3、如果不需要处理，则直接放行
         * 4、判断登录状态，如果已登录，则直接放行
         * 5、如果未登录则返回未登录结果
         */


        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1、获取本次请求的URI
        String requestURI = request.getRequestURI();

        log.info("本次访问的URI为：{}",requestURI);


        String[] urls = new  String[]{
                "/backend/**",
                "/employee/login",
                "/employee/logout",
                "/front/**",
                "/user/login",
                "/user/sendMsg",
        };

        boolean checkout = check(urls,requestURI);
        if(checkout){

            filterChain.doFilter(request,response);
            return;
        }

        //判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee") != null){
            Long threadId = Thread.currentThread().getId();
            log.info("当前在Filter中，ThreadId为{}",threadId);


            Long id = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(id);
            filterChain.doFilter(request,response);
            return;
        }

        //判断登录状态，如果已登录，则直接放行


        if(request.getSession().getAttribute("user") != null){
            Long threadId = Thread.currentThread().getId();
            log.info("当前在Filter中，ThreadId为{}",threadId);
            Long phone = Long.parseLong(request.getSession().getAttribute("user").toString());


            BaseContext.setCurrentId(phone);

            filterChain.doFilter(request,response);
            return;
        }



        log.info("URI请求{}还没有登录",requestURI);
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));



    }
    public  boolean check(String[] urls,String requestURI){
        for (String url : urls) {
           boolean match = PATH_MATCHER.match(url,requestURI);

           if (match){
               log.info("本次URI请求{}能够 直接 接放行",requestURI);
               return true;
           }
        }
        log.info("本次URI请求{} 不能 直接放行",requestURI);
        return false;
    }
}
