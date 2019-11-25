package com.cy.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.cy.dao.CheckItemDao;
import com.cy.pojo.CheckItem;
import com.cy.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * 如果使用@Transactional注解声明事物，那么就需要将这个服务明确出来实现了那个接口
 *
 * 也就是这个interfaceClass = CheckItemService.class
 */
@Service(interfaceClass = CheckItemService.class)
@Transactional
public class CheckItemServiceImpl implements CheckItemService {

    @Autowired
    private CheckItemDao checkItemDao;

    @Override
    public void add(CheckItem checkItem) {

        checkItemDao.add(checkItem);
    }
}
