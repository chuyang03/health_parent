package com.cy.service;

import com.cy.entity.PageResult;
import com.cy.entity.QueryPageBean;
import com.cy.pojo.CheckGroup;

import java.util.List;

public interface CheckGroupService {

    public void add(CheckGroup checkGroup, Integer[] checkitemIds);

    public PageResult pageQuery(QueryPageBean queryPageBean);

    public CheckGroup findById(Integer id);


    public List<Integer> findCheckItemIdsByCheckGroupId(Integer checkGroupId);

    public void update(CheckGroup checkGroup, Integer[] checkitemIds);

    public void deleteById(Integer id);

    public List<CheckGroup> findAll();
}
