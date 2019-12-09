package com.cy.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.cy.dao.MemberDao;
import com.cy.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
@Service(interfaceClass = MemberService.class)
@Transactional
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberDao memberDao;

    //根据月份统计会员数量，
    @Override
    public List<Integer> findMemberCountByMonth(List<String> months) {

        List<Integer> memberCount = new ArrayList<>();
        //4.6.9.11
        List<String> littleMonth = new ArrayList<>();
        littleMonth.add("04");
        littleMonth.add("06");
        littleMonth.add("09");
        littleMonth.add("11");

        for (String month: months) {
            String date = null;
            //获取月份的后两个字符，比如2019.02获取02字符串
            String last2Char = month.substring(month.indexOf(".") + 1);

            //mysql8会自动对月份进行校验，每个月不能超过最大天数，比如4。6。9。11月份没有31天，此时按照4.31查询就会出错
            if (last2Char.equals("02")){

                date = month + ".28";
            }else if (littleMonth.contains(last2Char)){
                date = month + ".30";
            }else {
                date = month + ".31";
            }

            //使用mysql进行范围查询，查询每个月最后一天之前的所有会员数量
            Integer count = memberDao.findMemberCountBeforeDate(date);

            memberCount.add(count);

        }

        return memberCount;
    }

    //如果是查询这一个月的会员数量，可以将每个月的一号，每个月的31号，封装到map中，将这个map传递给sql，使用范围查询，between and
}
