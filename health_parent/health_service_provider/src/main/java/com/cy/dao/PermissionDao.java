package com.cy.dao;

import com.cy.pojo.Permission;

import java.util.Set;

public interface PermissionDao {

    public Set<Permission> findByRoleId(Integer roleId);
}
