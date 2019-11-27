package com.cy.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.cy.dao.CheckItemDao;
import com.cy.entity.PageResult;
import com.cy.entity.QueryPageBean;
import com.cy.pojo.CheckItem;
import com.cy.service.CheckItemService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * 如果使用@Transactional注解声明事物，那么就需要将这个服务明确出来实现了那个接口
 *
 * 也就是这个interfaceClass = CheckItemService.class
 */
@Service(interfaceClass = CheckItemService.class)
@Transactional
public class CheckItemServiceImpl implements CheckItemService {

    @Autowired
    private CheckItemDao checkItemDao;

    @Override
    public void add(CheckItem checkItem) {

        checkItemDao.add(checkItem);
    }

    //检查项分页查询
    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {

        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();

        //使用pagehelper分页插件，执行这个代码之后，在mybatis调用select语句时不需要写limit条件，这条语句会自动填充limit
        PageHelper.startPage(currentPage, pageSize);

        //上面这个语句必须要和下面分页语句一起使用，中间不能有别的查询语句，要不然下面这个语句就不会进行分页查询
        //select * from t_checkitem limit (currentPage-1)*pagesize, pagesize
        Page<CheckItem> checkItemPage = checkItemDao.selectByCondition(queryString);

        long total = checkItemPage.getTotal();
        //封装了CheckItem的list
        List<CheckItem> rows = checkItemPage.getResult();

        return new PageResult(total, rows);
    }

    @Override
    public void deleteById(Integer id) {
        //在删除该检查项之前需要判断，当前检查项是否关联到检查组中，如果关联了则不能直接删除
        long count = checkItemDao.findCountByCheckItemId(id);

        if (count > 0){
            new RuntimeException();
        }

        checkItemDao.deleteById(id);
    }

    //批量删除
    @Override
    public void deleteByIds(Integer[] ids) {

        checkItemDao.deleteByIds(ids);
    }

    @Override
    public void edit(CheckItem checkItem) {
        checkItemDao.edit(checkItem);
    }

    @Override
    public CheckItem findById(Integer id) {
        return checkItemDao.findById(id);
    }


}
