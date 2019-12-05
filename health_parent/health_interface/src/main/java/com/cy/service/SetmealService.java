package com.cy.service;

import com.cy.entity.PageResult;
import com.cy.entity.QueryPageBean;
import com.cy.pojo.Setmeal;

public interface SetmealService {

    public void add(Setmeal setmeal, Integer[] checkgroupIds);

    public PageResult pageQuery(QueryPageBean queryPageBean);
}
