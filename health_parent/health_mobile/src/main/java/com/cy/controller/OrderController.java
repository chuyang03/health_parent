package com.cy.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.aliyuncs.exceptions.ClientException;
import com.cy.constant.MessageConstant;
import com.cy.constant.RedisConstant;
import com.cy.constant.RedisMessageConstant;
import com.cy.entity.Result;
import com.cy.pojo.Order;
import com.cy.service.OrderService;
import com.cy.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Map;

/**
 *
 * 体检预约处理
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private JedisPool jedisPool;

    @Reference
    private OrderService orderService;

    //@RequestBody  因为前端ajax提交的是json数据，如果不加这个注解，后端无法解析
    //微信端提交预约信息
    @RequestMapping("/submit")
    public Result submit(@RequestBody Map map){

        //首先判断之前给手机发送的验证码，这个验证码同时插入了redis，并设置了过期时间，
        // 需要从redis中取出来和表单中填写的验证码是否一致
        String telephone = (String) map.get("telephone");
        //从redis中读取验证码
        String validateCodeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);
        //表单填写的验证码
        String validateCode = (String) map.get("validateCode");

        Result result = null;
        //将用户输入的验证码和redis中保存的验证码进行比较
        if (validateCodeInRedis != null && validateCode != null && validateCode.equals(validateCodeInRedis)){

            //设置预约类型，是微信端预约还是电话端预约
            map.put("orderType", Order.ORDERTYPE_WEIXIN);
            //提交预约
            try {
                result = orderService.order(map);
            } catch (Exception e) {
                e.printStackTrace();

                return result;
            }

            //如果预约成功，需要给用户发送一个提示成功的短消息
            if (result.isFlag()){

                try {
                    SMSUtils.sendShortMessage(SMSUtils.ORDER_NOTICE, telephone, (String) map.get("orderDate"));
                } catch (ClientException e) {
                    e.printStackTrace();
                }
            }

            return result;

        }else {

            //验证码比对不一致
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }

    }
}
