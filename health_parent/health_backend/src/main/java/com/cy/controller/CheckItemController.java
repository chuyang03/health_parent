package com.cy.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cy.constant.MessageConstant;
import com.cy.entity.PageResult;
import com.cy.entity.QueryPageBean;
import com.cy.entity.Result;
import com.cy.pojo.CheckItem;
import com.cy.service.CheckItemService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/checkitem")
public class CheckItemController {

    //@Reference  这个注解的意思就是去zookeeper注册中心去找这个服务接口
    @Reference
    private CheckItemService checkItemService;

    //新增检查项    @RequestBody的作用就是将前端表单传进来的json数据，解析成对象，使用对象来接受数据
    @RequestMapping("/add")
    public Result add(@RequestBody CheckItem checkItem){

        try {
            checkItemService.add(checkItem);
        } catch (Exception e) {
            e.printStackTrace();

            //如果捕获到异常，说明调用远程服务不成功
            return new Result(false, MessageConstant.ADD_CHECKITEM_FAIL);
        }
        return new Result(true, MessageConstant.ADD_CHECKITEM_SUCCESS);
    }

    //检查项分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){

        PageResult pageResult = checkItemService.pageQuery(queryPageBean);

        return pageResult;
    }

    //检查项删除
    @RequestMapping("/delete")
    public Result delete(Integer id){

        try {
            checkItemService.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();

            //如果捕获到异常，说明调用远程服务不成功
            return new Result(false, MessageConstant.DELETE_CHECKITEM_FAIL);
        }
        return new Result(true, MessageConstant.DELETE_CHECKITEM_SUCCESS);
    }

    //检查项批量删除
    @RequestMapping("/deleteByIds")
    public Result deleteByIds(Integer[] ids){

        try {

            checkItemService.deleteByIds(ids);

        } catch (Exception e) {
            e.printStackTrace();

            //如果捕获到异常，说明调用远程服务不成功
            return new Result(false, MessageConstant.DELETE_CHECKITEM_FAIL);
        }
        return new Result(true, MessageConstant.DELETE_CHECKITEM_SUCCESS);
    }


    //编辑检查项
    @RequestMapping("/edit")
    public Result edit(@RequestBody CheckItem checkItem){

        try {
            checkItemService.edit(checkItem);
        } catch (Exception e) {
            e.printStackTrace();

            //如果捕获到异常，说明调用远程服务不成功
            return new Result(false, MessageConstant.EDIT_CHECKITEM_FAIL);
        }
        return new Result(true, MessageConstant.EDIT_CHECKITEM_SUCCESS);
    }

    //查询检查项数据用于编辑表单数据回显
    @RequestMapping("/findById")
    public Result findById(Integer id){

        try {
            CheckItem checkItem = checkItemService.findById(id);

            return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS, checkItem);
        } catch (Exception e) {
            e.printStackTrace();

            //如果捕获到异常，说明调用远程服务不成功
            return new Result(false, MessageConstant.QUERY_CHECKITEM_FAIL);
        }

    }
}
