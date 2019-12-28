package com.cy.service;

import com.cy.pojo.User;

public interface UserService {
    public User findByUsername(String username);
}
