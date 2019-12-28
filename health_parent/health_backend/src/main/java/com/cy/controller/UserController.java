package com.cy.controller;

import com.alibaba.fastjson.JSON;
import com.cy.constant.MessageConstant;
import com.cy.entity.Result;
import com.cy.pojo.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    //获取当前登录用户的用户名
    @RequestMapping("/getUsername")
    public Result getUsername(){
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            System.out.println("首页显示用户名的用户"+ JSON.toJSONString(user));

            return new Result(true, MessageConstant.GET_USERNAME_SUCCESS, user.getUsername());
        } catch (Exception e) {
            e.printStackTrace();

            return new Result(false, MessageConstant.GET_USERNAME_FAIL);
        }
    }
}
