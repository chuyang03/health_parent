package com.cy.dao;

import com.cy.pojo.OrderSetting;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderSettingDao {
    public long findCountByOrderDate(Date orderDate);

    public void updateNumberByOrderDate(OrderSetting orderSetting);

    public void add(OrderSetting orderSetting);

    public List<OrderSetting> getOrderSettingsByMonth(Map<String, String> map);

    public OrderSetting findByOrderDate(Date parseString2Date);

    public void updateReservationsByOrderDate(OrderSetting orderSetting);
}
