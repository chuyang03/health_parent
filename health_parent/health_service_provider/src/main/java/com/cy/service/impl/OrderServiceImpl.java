package com.cy.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.cy.constant.MessageConstant;
import com.cy.dao.MemberDao;
import com.cy.dao.OrderDao;
import com.cy.dao.OrderSettingDao;
import com.cy.entity.Result;
import com.cy.pojo.Member;
import com.cy.pojo.Order;
import com.cy.pojo.OrderSetting;
import com.cy.service.OrderService;
import com.cy.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 预约体检服务
 */
@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderSettingDao orderSettingDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private OrderDao orderDao;

    //微信端提交预约体检
    @Override
    public Result order(Map map) throws Exception {

        //1.检查用户提交预约信息的预约日期是否进行了预约设置，也就是预约的那一天公司允许不允许进行预约
        String orderDate = (String) map.get("orderDate");
        OrderSetting orderSetting = orderSettingDao.findByOrderDate(DateUtils.parseString2Date(orderDate));

        if (orderSetting == null && orderSetting.getNumber() == 0) {
            //所选日期不能进行预约体检
            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }

        //2.检查用户所选择的预约日期是否已经约满，如果已经约满则无法预约
        int orderNumber = orderSetting.getNumber();
        int reservations = orderSetting.getReservations();
        if (reservations >= orderNumber) {
            return new Result(false, MessageConstant.ORDER_FULL);
        }

        //3.检查用户是否重复预约，（同一个用户在同一天预约了同一个套餐），如果重复预约，则预约失败
        //三个条件同时满足，表示重复预约
        String telephone = (String) map.get("telephone");
        //根据电话从member表中查询会员用户
        Member member = memberDao.findByTelephone(telephone);

        if (member != null) {
            int memberId = member.getId();
            String setmealId = (String) map.get("setmealId");

            Order order = new Order(memberId, DateUtils.parseString2Date(orderDate), Integer.parseInt(setmealId));

            //根据条件进行查询
            List<Order> orderList = orderDao.findByCondition(order);
            if (orderList != null && orderList.size() > 0) {
                //重复预约,不能再次预约
                return new Result(false, MessageConstant.HAS_ORDERED);
            }
        } else {

            //4.检查当前用户是否为会员用户，如果是会员直接完成预约，如果不是会员则自动完成会员注册并进行预约
            //不是会员，自动完成注册
            member = new Member();
            member.setName((String) map.get("name"));
            member.setPhoneNumber(telephone);
            member.setIdCard((String) map.get("idCard"));
            member.setSex((String) map.get("sex"));
            member.setRegTime(new Date());
            memberDao.add(member);

        }

        //5.预约成功更新当前已预约人数
        //保存一下预约的信息，操作order表
        Order order = new Order();
        order.setMemberId(member.getId());
        order.setOrderDate(DateUtils.parseString2Date(orderDate));
        order.setOrderType((String) map.get("orderType"));
        order.setOrderStatus(Order.ORDERSTATUS_NO);
        order.setSetmealId((Integer) map.get("setmealId"));

        orderDao.add(order);

        //在预约设置中，将已预约人数加一
        orderSetting.setReservations(orderSetting.getReservations() + 1);
        orderSettingDao.updateReservationsByOrderDate(orderSetting);

        //返回前端的数据，给一个order的id
        return new Result(true, MessageConstant.ORDER_SUCCESS, order.getId());
    }

    //根据预约id，查询预约信息，包括预约体检人名字，和套餐详细信息
    @Override
    public Map findById(Integer id) throws Exception{

        Map map = orderDao.findById4Detail(id);
        if (map != null){

            //
            Date orderDate = (Date) map.get("orderDate");
            map.put("orderDate", DateUtils.parseDate2String(orderDate));
        }
        return map;
    }
}
