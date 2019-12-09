package com.cy.controller;

import com.aliyuncs.exceptions.ClientException;
import com.cy.constant.MessageConstant;
import com.cy.constant.RedisMessageConstant;
import com.cy.entity.Result;
import com.cy.utils.SMSUtils;
import com.cy.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

/**
 * 发送验证码
 */
@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {
    @Autowired
    private JedisPool jedisPool;

    //移动端提交预约信息界面，为指定手机号发送验证码
    @RequestMapping("/send4Order")
    public Result send4Order(String telephone){
        //生成随机验证码，4位验证码
        Integer code = ValidateCodeUtils.generateValidateCode(4);
        System.out.println("----------------------------------------------");
        System.out.println(code.toString());

        //调用短信服务发送验证码
        try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,code.toString());
            //将验证码保存到redis，时间为5分钟
            //使用的就是redis有一个过期时间
            jedisPool.getResource().setex(telephone+ RedisMessageConstant.SENDTYPE_ORDER,60*5,code.toString());

            return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (ClientException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }
    }

    //为指定手机号发送验证码
    @RequestMapping("/send4Login")
    public Result send4Login(String telephone){
        Integer code = ValidateCodeUtils.generateValidateCode(6);
        //调用短信服务发送验证码
        try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,code.toString());
            //将验证码保存到redis，时间为5分钟
            jedisPool.getResource().setex(telephone+ RedisMessageConstant.SENDTYPE_LOGIN,60*5,code.toString());
            return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (ClientException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }
    }
}
