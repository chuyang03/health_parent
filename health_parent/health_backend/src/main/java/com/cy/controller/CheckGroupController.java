package com.cy.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cy.constant.MessageConstant;
import com.cy.entity.PageResult;
import com.cy.entity.QueryPageBean;
import com.cy.entity.Result;
import com.cy.pojo.CheckGroup;
import com.cy.service.CheckGroupService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 *
 * 检查组管理页面
 */
@RestController
@RequestMapping("/checkgroup")
public class CheckGroupController {

    //引用dubbo服务
    @Reference
    private CheckGroupService checkGroupService;

    //新增检查组
    @RequestMapping("/add")
    public Result add(@RequestBody CheckGroup checkGroup, Integer[] checkitemIds){

        try {
            checkGroupService.add(checkGroup, checkitemIds);
        } catch (Exception e) {
            e.printStackTrace();

            return new Result(false, MessageConstant.ADD_CHECKGROUP_FAIL);
        }

        return new Result(true, MessageConstant.ADD_CHECKGROUP_SUCCESS);
    }

    //检查组分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){

        return checkGroupService.pageQuery(queryPageBean);
    }

    //查询检查组信息
    @RequestMapping("/findById")
    public Result findById(Integer id){

        try {
            CheckGroup checkGroup = checkGroupService.findById(id);

            return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS, checkGroup);
        } catch (Exception e) {
            e.printStackTrace();

            return new Result(false, MessageConstant.QUERY_CHECKGROUP_FAIL);
        }

    }

    //根据检查组id查询关联的所有检查项数据，进行表单回显
    @RequestMapping("/findCheckItemIdsByCheckGroupId")
    public Result findCheckItemIdsByCheckGroupId(Integer checkGroupId){

        try {
            List<Integer> checkItemIds = checkGroupService.findCheckItemIdsByCheckGroupId(checkGroupId);

            return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS, checkItemIds);
        } catch (Exception e) {
            e.printStackTrace();

            //如果捕获到异常，说明调用远程服务不成功
            return new Result(false, MessageConstant.QUERY_CHECKITEM_FAIL);
        }
    }

    //@RequestBody  用来将表单数据转化成对象
    //编辑检查组信息
    @RequestMapping("/edit")
    public Result edit(@RequestBody CheckGroup checkGroup, Integer[] checkitemIds){

        try {

            checkGroupService.update(checkGroup, checkitemIds);
        } catch (Exception e) {
            e.printStackTrace();

            return new Result(false, MessageConstant.EDIT_CHECKGROUP_FAIL);
        }

        return new Result(true, MessageConstant.EDIT_CHECKGROUP_SUCCESS);
    }

    //删除操作，如果要删除当前检查组，需要先去关联表，把相关数据都删除，然后在删除该检查组
    @RequestMapping("/deleteById")
    public Result deleteById(Integer id){

        try {
            checkGroupService.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();

            return new Result(false, MessageConstant.DELETE_CHECKGROUP_FAIL);
        }
        return new Result(true, MessageConstant.DELETE_CHECKGROUP_SUCCESS);
    }

    //这个套餐管理页面用到的方法
    //查询所有检查组信息
    @RequestMapping("/findAll")
    public Result findAll(){

        try {
            List<CheckGroup> checkGroupList = checkGroupService.findAll();

            return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS, checkGroupList);

        } catch (Exception e) {
            e.printStackTrace();

            return new Result(false, MessageConstant.QUERY_CHECKGROUP_FAIL);
        }

    }
}
