package com.lzq.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzq.api.pojo.Account;
import com.lzq.api.service.AccountService;
import com.lzq.api.service.FavoritesService;
import com.lzq.web.utils.JWTUtils;
import com.lzq.web.utils.ResultMapUtils;
import com.lzq.web.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailConfig userDetail;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PersistentTokenRepository tokenRepository;

    @Reference
    FavoritesService favoritesService;

    @Reference
    AccountService accountService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginProcessingUrl("/index/login")
                //登录成功
                .successHandler((httpServletRequest, httpServletResponse, authentication) -> {
                    //获取用户信息
                    Account account = (Account) authentication.getPrincipal();
                    Integer count = favoritesService.getCount(account.getUsername());
                    if (!count.equals(account.getFavorites())) {
                        account.setFavorites(count);
                        //更新（校正）我的喜爱数
                        Boolean aBoolean = accountService.updateFavorites(account);
                        log.info(aBoolean.toString());
                    }
                    //是否需要进行第三方绑定
                    String token = httpServletRequest.getHeader("token");
                    httpServletResponse.setContentType("application/json;charset=utf-8");
                    //把结果转换成json字符串
                    Map<String, Object> map = UserUtils.BindAccount(account, token, accountService);
                    //判断是否绑定成功
                    httpServletRequest.getSession().setAttribute("map", map);
                    httpServletResponse.getWriter().write(objectMapper.writeValueAsString(map));

                })
                //登录失败
                .failureHandler((httpServletRequest, httpServletResponse, e) -> {
                    Map<String, Object> map = ResultMapUtils.ResultMapWithToken(false, 0, null, null);
                    UserUtils.responseMessage(httpServletResponse, map, objectMapper);
                })
                //允许所有请求
                .permitAll()
                .and()
                .authorizeRequests()
                //放行index接口
                .antMatchers("/").permitAll()
                .antMatchers("/user/**", "/example/**", "/index/verify", "/feedback/**").

                authenticated()
                //所有请求都需要登录验证
                // .anyRequest().authenticated()
                .and()
                //记住我
                .rememberMe()
                //修改参数
                .rememberMeParameter("rememberMe")
                //有效时间30天
                .tokenValiditySeconds(60 * 60 * 24 * 30)
                .userDetailsService(userDetail)
                .tokenRepository(tokenRepository)
                //验证成功
                .authenticationSuccessHandler((httpServletRequest, httpServletResponse, authentication) -> {
                    Map<String, Object> map = new HashMap();
                    HashMap<String, String> jwtmap = new HashMap();
                    //获取用户
                    Account account = (Account) authentication.getPrincipal();
                    jwtmap.put("username", account.getUsername());
                    //生成token
                    String token = JWTUtils.getToken(jwtmap);
                    Map<String, Object> withToken = ResultMapUtils.ResultMapWithToken(true, 0, account, token);
                    httpServletRequest.getSession().setAttribute("map", withToken);
                    UserUtils.responseMessage(httpServletResponse, withToken, objectMapper);
                })
                .and()
                //退出登录
                .logout()
                .logoutUrl("/index/logout")
                //退出成功
                .logoutSuccessHandler((httpServletRequest, httpServletResponse, authentication) -> {
                    Map<String, Object> map = ResultMapUtils.ResultMapWithToken(true, 0, null, null);
                    UserUtils.responseMessage(httpServletResponse, map, objectMapper);
                })
                .and()
                .exceptionHandling().

                authenticationEntryPoint(macLoginUrlAuthenticationEntryPoint())
                .and()
                .csrf()
                .disable()
        ;

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint macLoginUrlAuthenticationEntryPoint() {
        return new MacLoginUrlAuthenticationEntryPoint();
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        //自动建表，第一次启动时需要，第二次启动时注释掉
        // tokenRepository.setCreateTableOnStartup(true);
        return tokenRepository;
    }

    /**
     * 认证管理器
     *
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


}
