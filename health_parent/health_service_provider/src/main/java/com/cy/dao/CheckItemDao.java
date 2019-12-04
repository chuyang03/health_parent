package com.cy.dao;

import com.cy.pojo.CheckItem;
import com.github.pagehelper.Page;

import java.util.List;

public interface CheckItemDao {

    public void add(CheckItem checkItem);

    //因为分页查询出来的数据是这一页所有的数据，是一个list，所以需要封装到Page这个范型类中
    //public class Page<E> extends ArrayList<E>
    public Page<CheckItem> selectByCondition(String queryString);

    //根据检查项id查询出来是否关联检查组，如果关联检查组还不能直接删除该检查项
    public long findCountByCheckItemId(Integer id);

    public void deleteById(Integer id);

    public void edit(CheckItem checkItem);

    public CheckItem findById(Integer id);

    public void deleteByIds(Integer[] ids);

    public List<CheckItem> findAll();


}
