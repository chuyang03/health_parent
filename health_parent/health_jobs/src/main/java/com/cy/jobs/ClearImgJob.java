package com.cy.jobs;

import com.cy.constant.RedisConstant;
import com.cy.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/**
 *
 * 自定义Job，实现定时清理垃圾图片
 */
public class ClearImgJob {

    @Autowired
    private JedisPool jedisPool;

    public void clearImg(){

        //根据redis中保存的两个set图片集合进行差值运算，计算出垃圾图片集合
        Set<String> set = jedisPool.getResource()
                .sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);

        if (set != null){

            for (String picName: set) {

                //将七牛云上的垃圾图片删除
                QiniuUtils.deleteFileFromQiniu(picName);

                //从保存所有上传图片的set集合中删除垃圾图片
                jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES, picName);

                System.out.println("垃圾图片清理:" + picName);
            }
        }
    }
}
