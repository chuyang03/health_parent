package com.cy.dao;

import com.cy.pojo.Setmeal;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface SetmealDao {
    public void add(Setmeal setmeal);

    public void setSetmealAndCheckGroup(Map<String, Integer> map);

    public Page<Setmeal> fincByCondition(String queryString);

    public List<Setmeal> findAll();

    public Setmeal findById(Integer id);

    public List<Map<String, Object>> findSetmealCount();
}
