package com.cy.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.cy.dao.MemberDao;
import com.cy.dao.OrderDao;
import com.cy.service.ReportService;
import com.cy.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 运营报表数据查询
 */
@Service(interfaceClass = ReportService.class)
@Transactional
public class ReportServiceImpl implements ReportService {

    @Autowired
    private MemberDao memberDao;
    
    @Autowired
    private OrderDao orderDao;

    /**
     * {
     *   "data":{
     *     "todayVisitsNumber":0,
     *     "reportDate":"2019-04-25",
     *     "todayNewMember":0,
     *     "thisWeekVisitsNumber":0,
     *     "thisMonthNewMember":2,
     *     "thisWeekNewMember":0,
     *     "totalMember":10,
     *     "thisMonthOrderNumber":2,
     *     "thisMonthVisitsNumber":0,
     *     "todayOrderNumber":0,
     *     "thisWeekOrderNumber":0,
     *     "hotSetmeal":[
     *       {"proportion":0.4545,"name":"粉红珍爱(女)升级TM12项筛查体检套餐","setmeal_count":5},
     *       {"proportion":0.1818,"name":"阳光爸妈升级肿瘤12项筛查体检套餐","setmeal_count":2},
     *       {"proportion":0.1818,"name":"珍爱高端升级肿瘤12项筛查","setmeal_count":2},
     *       {"proportion":0.0909,"name":"孕前检查套餐","setmeal_count":1}
     *     ],
     *   },
     *   "flag":true,
     *   "message":"获取运营统计数据成功"
     * }
     *
     */
    //运营报表数据查询
    @Override
    public Map<String, Object> getBusinessReportData() throws Exception{

        //当日,报表日期
        String today = DateUtils.parseDate2String(DateUtils.getToday());
        //获得本周一日期
        String thisWeekMonday = DateUtils.parseDate2String(DateUtils.getThisWeekMonday());
        //获得本月第一天的日期
        String firstDay4ThisMonth = DateUtils.parseDate2String(DateUtils.getFirstDay4ThisMonth());

        //本日新增会员数
        Integer todayNewMember = memberDao.findMemberCountByDate(today);
        //总的会员数量
        Integer totalMember = memberDao.findMemberTotalCount();
        //本周新增加的会员数量，根据当前日期获得本周的周一所在的日期，然后大于这个日期注册的会员都是本周内注册的
        Integer thisWeekNewMember = memberDao.findMemberCountAfterDate(thisWeekMonday);
        //这个月新增加的会员数
        Integer thisMonthNewMember = memberDao.findMemberCountAfterDate(firstDay4ThisMonth);
        
        //今日预约数
        Integer todayOrderNumber = orderDao.findOrderCountByDate(today);
        //今日到访数
        Integer todayVisitsNumber = orderDao.findVisitsCountByDate(today);
        //本周预约数
        Integer thisWeekOrderNumber = orderDao.findOrderCountAfterDate(thisWeekMonday);

        //本月预约数
        Integer thisMonthOrderNumber = orderDao.findOrderCountAfterDate(firstDay4ThisMonth);

        //本周到诊数
        Integer thisWeekVisitsNumber = orderDao.findVisitsCountAfterDate(thisWeekMonday);

        //本月到诊数
        Integer thisMonthVisitsNumber = orderDao.findVisitsCountAfterDate(firstDay4ThisMonth);

        //热门套餐（取前4）
        List<Map> hotSetmeal = orderDao.findHotSetmeal();


        Map<String,Object> result = new HashMap<>();
        result.put("reportDate",today);
        result.put("todayNewMember",todayNewMember);
        result.put("totalMember",totalMember);
        result.put("thisWeekNewMember",thisWeekNewMember);
        result.put("thisMonthNewMember",thisMonthNewMember);
        result.put("todayOrderNumber",todayOrderNumber);
        result.put("thisWeekOrderNumber",thisWeekOrderNumber);
        result.put("thisMonthOrderNumber",thisMonthOrderNumber);
        result.put("todayVisitsNumber",todayVisitsNumber);
        result.put("thisWeekVisitsNumber",thisWeekVisitsNumber);
        result.put("thisMonthVisitsNumber",thisMonthVisitsNumber);
        result.put("hotSetmeal",hotSetmeal);

        return result;
    }
}
