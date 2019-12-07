package com.cy.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.cy.dao.OrderSettingDao;
import com.cy.pojo.OrderSetting;
import com.cy.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 预约设置服务
 */

@Service(interfaceClass = OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    private OrderSettingDao orderSettingDao;


    //批量导入预约设置数据到数据库
    @Override
    public void add(List<OrderSetting> data) {

        //首先遍历数据，然后根据日期判断该数据是否已经存在，如果存在那么将数据库的数据进行更新，如果不存在则执行数据插入
        if (data != null && data.size() > 0){

            for (OrderSetting orderSetting: data) {

                long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());

                if (count > 0){

                    orderSettingDao.updateNumberByOrderDate(orderSetting);
                }else {
                    orderSettingDao.add(orderSetting);
                }
            }
        }

    }

    //根据月份查询当月所有天数的预约信息，因为date格式为2019-12，数据库格式2019-12-01
    @Override
    public List<Map> getOrderSettingsByMonth(String date) {

        //这个地方拼接字符串不能使用"-01"，不知道为什么
        String dateBegin = date + "-1";
        String dateEnd = date + "-31";

        Map<String, String> map = new HashMap<>();
        map.put("dateBegin", dateBegin);
        map.put("dateEnd", dateEnd);

        //从数据库中将当前月所有天的预约信息查询出来
        List<OrderSetting> orderSettingList = orderSettingDao.getOrderSettingsByMonth(map);
        List<Map> resultList = new ArrayList<>();

        if (orderSettingList != null && orderSettingList.size() > 0){

            for (OrderSetting orderSetting: orderSettingList) {

                Map<String, Object> orderSettingMap = new HashMap<>();
                orderSettingMap.put("date", orderSetting.getOrderDate().getDate()); //获得日期，是几号，12-06就是6号
                orderSettingMap.put("number", orderSetting.getNumber());
                orderSettingMap.put("reservations", orderSetting.getReservations());

                resultList.add(orderSettingMap);
            }
        }

        return resultList;
    }

    //单独点击设置，设置可预约人数
    @Override
    public void editNumberByDate(OrderSetting orderSetting) {

        //先查询点击设置这一天的可预约人数是否为空，不为空就更新，为空就插入,参数2019-12-01
        long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
        if (count > 0){

            orderSettingDao.updateNumberByOrderDate(orderSetting);
        }else {
            orderSettingDao.add(orderSetting);
        }
    }
}
