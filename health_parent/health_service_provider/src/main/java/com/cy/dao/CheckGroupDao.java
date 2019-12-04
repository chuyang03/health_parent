package com.cy.dao;

import com.cy.pojo.CheckGroup;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface CheckGroupDao {

    public void add(CheckGroup checkGroup);

    public void setCheckGroupAndCheckItem(Map map);


    public Page<CheckGroup> findByCondition(String queryString);

    public CheckGroup findById(Integer id);

    public List<Integer> findCheckItemIdsByCheckGroupId(Integer checkGroupId);

    public void deleteAssociation(Integer checkGroupId);

    public void update(CheckGroup checkGroup);

    public void deleteById(Integer id);

    public List<CheckGroup> findAll();
}
