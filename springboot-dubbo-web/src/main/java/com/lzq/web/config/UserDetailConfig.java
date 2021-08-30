package com.lzq.web.config;


import com.lzq.api.pojo.Account;
import com.lzq.api.service.AccountService;
import com.lzq.api.service.RoleService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        System.out.println(s);
        //生产用户
        Account account = null;
        //判断用户所用账号为邮箱还是用户名
        if (s.split("\\.").length > 1) {
            //根据邮箱查询用户
            account = accountService.queryByEmail(s);
            System.out.println(account);
        } else {
            //根据用户名查询用户
            account = accountService.queryByUsername(s);
            System.out.println(account);
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
