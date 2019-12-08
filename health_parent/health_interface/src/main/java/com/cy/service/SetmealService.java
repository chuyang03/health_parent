package com.cy.service;

import com.cy.entity.PageResult;
import com.cy.entity.QueryPageBean;
import com.cy.pojo.Setmeal;

import java.util.List;

public interface SetmealService {

    public void add(Setmeal setmeal, Integer[] checkgroupIds);

    public PageResult pageQuery(QueryPageBean queryPageBean);

    public List<Setmeal> findAll();

    public Setmeal findById(Integer id);
}
