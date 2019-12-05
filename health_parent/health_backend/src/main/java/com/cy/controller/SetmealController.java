package com.cy.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cy.constant.MessageConstant;
import com.cy.constant.RedisConstant;
import com.cy.entity.PageResult;
import com.cy.entity.QueryPageBean;
import com.cy.entity.Result;
import com.cy.pojo.Setmeal;
import com.cy.service.SetmealService;
import com.cy.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.UUID;

/**
 *
 *体检套餐管理
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    //使用JedisPool来操作redis服务
    @Autowired
    private JedisPool jedisPool;


    //@RequestParam("imgFile")   从前端标签<el-upload name="imgFile"  中的name属性拿到的名字
    //图片上传
    @RequestMapping("/upload")
    public Result upload(@RequestParam("imgFile") MultipartFile imgFile){

        //获取文件名称，比如xxxx.jpg
        String originalFilename = imgFile.getOriginalFilename();
        //获取字符串中"."所在的索引值
        int index = originalFilename.lastIndexOf(".");
        String extention = originalFilename.substring(index - 1); //.jpg
        String fileName = UUID.randomUUID().toString() + extention;

        try {
            QiniuUtils.upload2Qiniu(imgFile.getBytes(), fileName);

            //将上传到七牛云的图片名称保存到redis的集合中
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES, fileName);

        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }
        return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS, fileName);
    }

    //因为调用服务是远程调用，所以需要使用dubbo的注解 @Reference
    @Reference
    private SetmealService setmealService;

    @RequestMapping("/add")
    public Result add(@RequestBody Setmeal setmeal, Integer[] checkgroupIds){

        try {

            setmealService.add(setmeal, checkgroupIds);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ADD_SETMEAL_FAIL);
        }
        return new Result(true, MessageConstant.ADD_SETMEAL_SUCCESS);
    }

    //套餐页面的分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){

        return setmealService.pageQuery(queryPageBean);
    }
}
