package com.lzq.web.config;


import com.lzq.api.pojo.Account;
import com.lzq.api.service.AccountService;
import com.lzq.api.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserDetailConfig implements UserDetailsService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Reference
    AccountService accountService;

    @Reference
    RoleService roleService;


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        log.info(s);
        //生产用户
        Account account = null;
        //判断用户所用账号为邮箱还是用户名
        if (s.split("\\.").length > 1) {
            //根据邮箱查询用户
            account = accountService.queryByEmail(s);
            log.info(account.toString());
        } else {
            //根据用户名查询用户
            account = accountService.queryByUsername(s);
            log.info(account.toString());
        }
        if (account == null) {
            throw new UsernameNotFoundException("账号或密码错误");
        } else {
            //设置权限用户的密码
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            //用户的权限角色
            account.setRole(roleService.queryById(account.getRoleId()));
            return account;
        }
    }
}
