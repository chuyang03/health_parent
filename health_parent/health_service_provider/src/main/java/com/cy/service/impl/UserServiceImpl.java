package com.cy.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.cy.dao.PermissionDao;
import com.cy.dao.RoleDao;
import com.cy.dao.UserDao;
import com.cy.pojo.Permission;
import com.cy.pojo.Role;
import com.cy.pojo.User;
import com.cy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 *
 * 用户服务
 */

@Service(interfaceClass = UserService.class)
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private PermissionDao permissionDao;

    //根据用户名查询数据库获取用户信息和关联的角色信息，同时需要查询角色关联的权限信息
    public User findByUsername(String username) {

        //查询用户基本信息，不包含用户关联的角色
        User user = userDao.findByUsername(username);
        if (user == null){
            return null;
        }

        Integer userId = user.getId();
        //根据用户id查询对应的角色
        Set<Role> roles = roleDao.findByUserId(userId);
        for (Role role: roles) {

            Integer roleId = role.getId();
            Set<Permission> permissions = permissionDao.findByRoleId(roleId);
            for (Permission permission: permissions) {

                //让角色关联权限
                role.setPermissions(permissions);
            }
        }
        user.setRoles(roles);   //让用户关联角色
        return user;
    }
}
