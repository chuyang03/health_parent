package com.cy.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cy.pojo.Permission;
import com.cy.pojo.Role;
import com.cy.pojo.User;
import com.cy.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class SpringSecurityUserService implements UserDetailsService {

    @Reference
    private UserService userService;

    //登录用户名和密码  admin 1234
    //根据用户名查询数据库获取信息
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userService.findByUsername(username);
        if (user == null){
            return null;
        }

        List<GrantedAuthority> list = new ArrayList<>();

        //动态为用户授权
        Set<Role> roles = user.getRoles();
        for (Role role: roles) {

            list.add(new SimpleGrantedAuthority(role.getKeyword()));
            Set<Permission> permissions = role.getPermissions();
            for (Permission permission: permissions) {

                list.add(new SimpleGrantedAuthority(permission.getKeyword()));
            }
        }

        org.springframework.security.core.userdetails.User securityUser =
                new org.springframework.security.core.userdetails.User(username, user.getPassword(), list);


        return securityUser;
    }
}
