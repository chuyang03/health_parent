package com.cy.dao;

import com.cy.pojo.CheckItem;
import com.github.pagehelper.Page;

public interface CheckItemDao {

    public void add(CheckItem checkItem);

    //因为分页查询出来的数据是这一页所有的数据，是一个list，所以需要封装到Page这个范型类中
    //public class Page<E> extends ArrayList<E>
    public Page<CheckItem> selectByCondition(String queryString);
}
