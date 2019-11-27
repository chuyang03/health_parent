package com.cy.service;

import com.cy.entity.PageResult;
import com.cy.entity.QueryPageBean;
import com.cy.entity.Result;
import com.cy.pojo.CheckItem;

//服务接口
public interface CheckItemService {

    public void add(CheckItem checkItem);

    public PageResult pageQuery(QueryPageBean queryPageBean);

    public void deleteById(Integer id);

    public void edit(CheckItem checkItem);

    public CheckItem findById(Integer id);

    public void deleteByIds(Integer[] ids);
}
