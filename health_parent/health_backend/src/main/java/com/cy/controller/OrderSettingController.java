package com.cy.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cy.constant.MessageConstant;
import com.cy.entity.Result;
import com.cy.pojo.OrderSetting;
import com.cy.service.OrderSettingService;
import com.cy.utils.POIUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * 预约设置
 */
@RestController
@RequestMapping("/ordersetting")
public class OrderSettingController {

    @Reference
    private OrderSettingService orderSettingService;

    //upload(@RequestParam("excelFile")MultipartFile excelFile
    //如果方法参数变量设置的和前端name属性不一致，可以使用@RequestParam来指定，指定接受的名称要和前端一致\
    //上传文件
    @RequestMapping("/upload")
    public Result upload(@RequestParam("excelFile") MultipartFile excelFile){

        //list存放的是每一行数据，里面的数组元素是单元格数据
        //excel的每一行单元格数据都是一个对象的属性，所以可以使用对象来对单元格数据进行封装，然后将对象插入到数据库中
        try {
            List<String[]> list = POIUtils.readExcel(excelFile);
            List<OrderSetting> data = new ArrayList<>();

            for (String[] strings: list) {

                //预约日期
                String orderDate = strings[0];
                //预约数量
                String orderNumber = strings[1];

                //将预约日期改为指定格式

                OrderSetting orderSetting = new OrderSetting(new Date(orderDate), Integer.parseInt(orderNumber));

                data.add(orderSetting);


            }

            //将从excel表格中读取的数据封装成一个个对象，插入到对应数据库中
            orderSettingService.add(data);

            return new Result(true, MessageConstant.IMPORT_ORDERSETTING_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();

            return new Result(false, MessageConstant.IMPORT_ORDERSETTING_FAIL);
        }
    }

    ///ordersetting/getOrderSettingsByMonth.do?date=" + this.currentYear+'-'+this.currentMonth
    //根据月份查询当月所有天数的预约信息
    @RequestMapping("/getOrderSettingsByMonth")
    public Result getOrderSettingsByMonth(String date){

        try {

            //为什么要使用map封装，因为前端提取的json数据中有一个属性是，几号，而ordersetting中没有这个属性，只有完整的日期
            List<Map> list = orderSettingService.getOrderSettingsByMonth(date);

            return new Result(true, MessageConstant.GET_ORDERSETTING_SUCCESS, list);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_ORDERSETTING_FAIL);
        }

    }

    //orderDate:this.formatDate(year,month,dayNum),number:value,这两个参数是OrderSetting的参数
    //在页面上设置预约人数，如果已经有一个预约人数了，那只需要更新数据库中的预约人数就行了，如果没有就重新插入
    @RequestMapping("/editNumberByDate")
    public Result editNumberByDate(@RequestBody OrderSetting orderSetting){

        try {
            orderSettingService.editNumberByDate(orderSetting);
            return new Result(true, MessageConstant.ORDERSETTING_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ORDERSETTING_FAIL);
        }

    }
}
