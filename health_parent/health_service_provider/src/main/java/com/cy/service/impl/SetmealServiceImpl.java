package com.cy.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.cy.constant.RedisConstant;
import com.cy.dao.SetmealDao;
import com.cy.entity.PageResult;
import com.cy.entity.QueryPageBean;
import com.cy.pojo.Setmeal;
import com.cy.service.SetmealService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 套餐管理服务
 */
@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealDao setmealDao;

    @Autowired
    private JedisPool jedisPool;

    //新增套餐
    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {

        //新增套餐信息，同时需要关联检查组
        setmealDao.add(setmeal);

        //操作关联表（套餐和检查组关联表）
        Integer setmealId = setmeal.getId();

        this.setSetmealAndCheckgroup(setmealId, checkgroupIds);

        //将添加到数据库的图片名称也添加到redis集合中
        String fileName = setmeal.getImg();
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES, fileName);
    }

    //套餐页面分页查询
    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();

        //使用pagehelper分页插件, public class Page<E> extends ArrayList<E> {
        PageHelper.startPage(currentPage, pageSize);
        Page<Setmeal> page = setmealDao.fincByCondition(queryString);

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Setmeal> findAll() {

        return setmealDao.findAll();
    }

    //根据套餐id查询套餐详情（套餐基本信息、套餐关联的多个检查组信息、检查组关联的多个检查项信息）
    //所有实体类的关联属性都在mybatis中，使用关联查询完成
    @Override
    public Setmeal findById(Integer id) {

        return setmealDao.findById(id);
    }

    //设置套餐和检查组关联表，插入数据
    public void setSetmealAndCheckgroup(Integer setmealId, Integer[]checkgroupIds){

        if (checkgroupIds != null && checkgroupIds.length > 0){
            for (Integer checkgroupId: checkgroupIds){
                Map<String, Integer> map = new HashMap<>();
                map.put("checkgroupId", checkgroupId);
                map.put("setmealId", setmealId);

                setmealDao.setSetmealAndCheckGroup(map);
            }
        }
    }
}
