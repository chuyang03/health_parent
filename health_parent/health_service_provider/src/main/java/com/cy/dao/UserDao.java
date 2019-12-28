package com.cy.dao;

import com.cy.pojo.User;

public interface UserDao {

    public User findByUsername(String username);
}
