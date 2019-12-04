package com.cy.controller;

import com.cy.constant.MessageConstant;
import com.cy.entity.Result;
import com.cy.utils.QiniuUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 *
 *体检套餐管理
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

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
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }
        return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS, fileName);
    }
}
