package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.itheima.reggie.common.BaseContext;

/**
 *登录验证过滤器
 */
/**
 * 完善登录功能
 * 代码实现
 * 实现步骤:
 * 1、创建自定义过滤器LoginCheckFilter
 * 2、在启动类.上加入注解@ServletComponentScan
 * 3、完善过滤器的处理逻辑
 */
@WebFilter(filterName = "/loginCheckFilter")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配工具类，用于判断请求路径是否匹配
    public static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {


        /**
         * 控制流图
         * flowchart TD
         *     A[开始] --> B[强转请求和响应对象]
         *     B --> C[记录请求URI]
         *     C --> D[继续传递请求]
         */
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        /*
        强转的原因：
         ServletRequest 和 ServletResponse 是通用接口，
         它们提供了所有 Servlet 请求和响应的基本功能。
         但在实际应用中，通常需要使用更具体的子类 HttpServletRequest 和 HttpServletResponse，
         以便访问与 HTTP 协议相关的特定功能，例如获取请求的 URI、处理 HTTP 头等
         */



         /*
         * 过滤器具体的处理逻辑如下:
         * 1、获取本次请求的URI
         * 2、判断本次请求是否需要处理
         * 3、如果不需要处理,则直接放行
         * 4、判断登录状态，如果已登录，则直接放行
         * 5、如果未登录则返回未登录结果
         */


        //1、获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("本次请求{}",requestURI);
        //2、判断本次请求是否需要处理

        //不需要拦截的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",//文件上传
                "/user/sendMsg",//短信发送
                "/user/login",//用户登录
                "/user/logout" //用户退出
        };

        //遍历数组，判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3、如果不需要处理,则直接放行
        if (check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //4、判断登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("employee"));

            //调用BaseContext的setCurrentId方法将当前登录用户ID存入ThreadLocal。
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            /*Long empId = (Long) request.getSession().getAttribute("employee");
            //将当前登录用户的id存入ThreadLocal
            BaseContext.setCurrentId(empId);*/

            filterChain.doFilter(request,response);

            return;
        }
        //5、如果未登录则返回未登录结果，通过输出流返回数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }


    /**
     * 判断本次请求是否需要处理
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = ANT_PATH_MATCHER.match(url, requestURI);
            if (match){
                //匹配成功
                return true;
            }
        }
        //匹配失败
        return false;
    }
}
