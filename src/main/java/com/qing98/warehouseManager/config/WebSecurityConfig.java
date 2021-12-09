package com.qing98.warehouseManager.config;

import com.qing98.warehouseManager.controller.WarehouseController;
import com.qing98.warehouseManager.service.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.PrintWriter;

/**
 * @author QIU
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final static Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class.getName());
    @Autowired
    UserServiceImpl userService;

    /**
     * 配置BCrypt密码加密器
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //除了网站图标，其他页面均需要身份认证
                .authorizeRequests()
                .antMatchers("/favicon.ico").permitAll()
                .anyRequest().authenticated()
                //登入配置，登入结果由JSON返回
                .and()
                .formLogin()
                .loginPage("/login.html")
                .successHandler((req, resp, authentication) -> {
                    logger.info("Login: ip[" + req.getRemoteAddr() + "] username[" + authentication.getName() + "]");
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write("{\"result\":true}");
                    out.flush();
                    out.close();
                })
                .failureHandler((req, resp, e) -> {
                    logger.warn("Login failed: ip[" + req.getRemoteAddr() + "] username[" + req.getParameter("username") + "] password[" + req.getParameter("password") + "]");
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write("{\"result\":false}");
                    out.flush();
                    out.close();
                })
                .permitAll()
                //开启记住我功能
                .and()
                .rememberMe()
                //登出配置，登出结果由JSON返回
                .and()
                .logout()
                .logoutSuccessHandler((req, resp, authentication) -> {
                    logger.info("Logout: ip[" + req.getRemoteAddr() + "] username[" + authentication.getName() + "]");
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write("{\"result\":true}");
                    out.flush();
                    out.close();
                })
                .permitAll()
                //配置跨域以及用户最大连接会话数量
                .and()
                .csrf().disable()
                .sessionManagement()
                .maximumSessions(1);
    }
}
