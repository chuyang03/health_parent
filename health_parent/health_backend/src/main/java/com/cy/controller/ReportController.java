package com.cy.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cy.constant.MessageConstant;
import com.cy.entity.Result;
import com.cy.service.MemberService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * 报表操作
 */
@RestController
@RequestMapping("report")
public class ReportController {

    @Reference
    private MemberService memberService;

    //返回会员折线图数据，包括月份和每月的会员数量
    @RequestMapping("/getMemberReport")
    public Result getMemberReport(){

        Map<String,Object> map = new HashMap<>();
        //存放的是月份，2019.01,2019.02等等
        List<String> months = new ArrayList<>();
        //获得一个日历时间，模拟时间就是当前时间
        Calendar calendar = Calendar.getInstance();
        //将当前时间向前推12个月，也就是如果现在是2019.12,那么向前推就是2018.12
        calendar.add(Calendar.MONTH, -12);

        for (int i = 0; i < 12; i++) {
            //从上面calendar获取的时间，也就是向前推了12个月的时间，每次遍历月份加一
            calendar.add(Calendar.MONTH, 1);
            Date date = calendar.getTime();

            months.add(new SimpleDateFormat("yyyy.MM").format(date));
        }
        map.put("months", months);

        //每个月统计的会员数量都是总会员数量，会员数量只会增加，也就是说每个月的毁约数量都比上一个月的数量大
        List<Integer> memberCount = memberService.findMemberCountByMonth(months);
        map.put("memberCount", memberCount);

        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS, map);
    }
}
