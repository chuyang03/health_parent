package com.cy.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.cy.dao.CheckGroupDao;
import com.cy.entity.PageResult;
import com.cy.entity.QueryPageBean;
import com.cy.pojo.CheckGroup;
import com.cy.service.CheckGroupService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
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
        //重建关联关系表
        this.setCheckGroupAndCheckItem(checkGroupId, checkitemIds);
    }

    //检查组分页查询
    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {

        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();

        //相当于给mybatis底层的sql语句添加limit，也就是编写sql的时候不需要写limit，但是这两个语句要在一起
        PageHelper.startPage(currentPage, pageSize);
        Page<CheckGroup> page = checkGroupDao.findByCondition(queryString);

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public CheckGroup findById(Integer id) {
        return checkGroupDao.findById(id);
    }

    //根据检查组id查询出所有关联的检查项id，进行表单回显
    @Override
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer checkGroupId) {
        return checkGroupDao.findCheckItemIdsByCheckGroupId(checkGroupId);
    }

    //编辑检查组
    //更新检查组信息
    @Override
    public void update(CheckGroup checkGroup, Integer[] checkitemIds) {

        //更新检查组信息
        checkGroupDao.update(checkGroup);

        //将关联表中与当前检查id关联的数据全部删除，方便根据勾选的检查项重新插入
        Integer checkGroupId = checkGroup.getId();
        //根据检查组id删除关联的所有检查项id，操作的数据库是检查组和检查项的关联表
        checkGroupDao.deleteAssociation(checkGroupId);

        //重建关联关系表
        this.setCheckGroupAndCheckItem(checkGroupId, checkitemIds);


    }

    //如果要删除当前检查组，需要先去关联表，把相关数据都删除，然后在删除该检查组
    @Override
    public void deleteById(Integer id) {

        //删除关联关系
        checkGroupDao.deleteAssociation(id);
        //删除当前检查组
        checkGroupDao.deleteById(id);
    }

    //查询所有检查组信息
    @Override
    public List<CheckGroup> findAll() {
        return checkGroupDao.findAll();
    }

    //建立检查组与检查项多对多关系
    public void setCheckGroupAndCheckItem(Integer checkGroupId, Integer[] checkitemIds) {

        //检查组关联检查项（操作中间表）
        if (checkitemIds != null && checkitemIds.length > 0) {

            Map<String, Integer> map = new HashMap<>();
            for (Integer checkItemId : checkitemIds) {
                map.put("checkGroupId", checkGroupId);
                map.put("checkItemId", checkItemId);

                checkGroupDao.setCheckGroupAndCheckItem(map);
            }
        }
    }

}
