package com.cy.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cy.constant.MessageConstant;
import com.cy.entity.Result;
import com.cy.pojo.Setmeal;
import com.cy.service.SetmealService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 移动端套餐管理界面
 *
 */

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Reference
    private SetmealService setmealService;

    //查询出所有的套餐信息
    @RequestMapping("/getAllSetmeal")
    public Result getAllSetmeal(){

        try {
            List<Setmeal> setmealList = setmealService.findAll();

            return new Result(true, MessageConstant.GET_SETMEAL_LIST_SUCCESS, setmealList);
        } catch (Exception e) {
            e.printStackTrace();

            return new Result(false, MessageConstant.GET_SETMEAL_LIST_FAIL);
        }

    }

    //根据套餐id查询套餐详情（套餐基本信息、套餐关联的多个检查组信息、检查组关联的多个检查项信息）
    @RequestMapping("/findById")
    public Result findById(Integer id){

        try {
            Setmeal setmeal = setmealService.findById(id);

            return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS, setmeal);
        } catch (Exception e) {
            e.printStackTrace();

            return new Result(false, MessageConstant.QUERY_SETMEAL_FAIL);
        }

    }
}
