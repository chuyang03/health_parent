package com.cy.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.cy.dao.CheckGroupDao;
import com.cy.pojo.CheckGroup;
import com.cy.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;


/**
 * 检查组服务
 */
@Service(interfaceClass = CheckGroupService.class)
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService {
    @Autowired
    private CheckGroupDao checkGroupDao;

    //新增检查组，同时需要关联检查项
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        //新增检查组
        checkGroupDao.add(checkGroup);
        Integer checkGroupId = checkGroup.getId();
        //检查组关联检查项（操作中间表）
        if(checkitemIds != null && checkitemIds.length > 0){

            Map<String,Integer> map = new HashMap<>();
            for(Integer checkItemId:checkitemIds){
                map.put("checkGroupId",checkGroupId);
                map.put("checkItemId",checkItemId);

                checkGroupDao.setCheckGroupAndCheckItem(map);
            }
        }
    }


}
