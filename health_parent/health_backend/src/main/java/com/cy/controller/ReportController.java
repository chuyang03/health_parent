package com.cy.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cy.constant.MessageConstant;
import com.cy.entity.Result;
import com.cy.service.MemberService;
import com.cy.service.ReportService;
import com.cy.service.SetmealService;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * 统计分析
 */
@RestController
@RequestMapping("report")
public class ReportController {

    //如果调用服务超时，可以在@Reference 注解中设置超时时间.默认时间是1秒
    @Reference(timeout = 2000)
    private MemberService memberService;

    @Reference
    private SetmealService setmealService;

    @Reference
    private ReportService reportService;

    /**
     * 返回到前端的数据格式：
     * "data":
     *  {"months":["2019.01","2019.02","2019.03","2019.04","2019.05","2019.06","2019.07","2019.08","2019.09","2019.10","2019.11","2019.12"],
     * 	"memberCount":[4,5,9,12,12,12,12,12,12,12,12,12]
     * },
     * @return
     */
    //返回会员折线图数据，包括月份和每月的会员数量
    @RequestMapping("/getMemberReport")
    public Result getMemberReport(){

        Map<String,Object> map = new HashMap<>();
        //存放的是月份，2019.01,2019.02等等
        List<String> months = new ArrayList<>();
        //获得一个日历时间，模拟时间就是当前时间
        Calendar calendar = Calendar.getInstance();
        //将当前时间向前推12个月，也就是如果现在是2019.12,那么向前推就是2018.12
        calendar.add(Calendar.MONTH, -12);

        for (int i = 0; i < 12; i++) {
            //从上面calendar获取的时间，也就是向前推了12个月的时间，每次遍历月份加一
            calendar.add(Calendar.MONTH, 1);
            Date date = calendar.getTime();

            months.add(new SimpleDateFormat("yyyy.MM").format(date));
        }
        map.put("months", months);

        //每个月统计的会员数量都是总会员数量，会员数量只会增加，也就是说每个月的毁约数量都比上一个月的数量大
        List<Integer> memberCount = memberService.findMemberCountByMonth(months);
        map.put("memberCount", memberCount);

        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS, map);
    }


    /**
     * 返回的json数据格式：
     * {"data":{"setmealNames":["入职无忧体检套餐（男女通用）","粉红珍爱(女)升级TM12项筛查体检套餐"],
     *
     * "setmealCount":[{"name":"入职无忧体检套餐（男女通用）","value":1},
     * {"name":"粉红珍爱(女)升级TM12项筛查体检套餐","value":1}]},
     *
     * "flag":true,"message":"获取套餐统计数据成功"}
     * @return
     */
    //套餐预约占比统计图数据
    @RequestMapping("/getSetmealReport")
    public Result getSetmealReport(){

        //返回前端的数据
        Map<String, Object> data = new HashMap<>();

        try {
            //"setmealCount":[{"name":"入职无忧体检套餐（男女通用）","value":1},
            //                  {"name":"粉红珍爱(女)升级TM12项筛查体检套餐","value":1}]

            //返回前端的json数据，map中的属性名称必须为，name，value，要不然echarts识别不了
            List<Map<String, Object>> setmealCount = setmealService.findSetmealCount();

            data.put("setmealCount", setmealCount);

            List<String> setmealNames = new ArrayList<>();
            for (Map<String, Object> map: setmealCount) {
                //获取套餐的名字加入到setmealNames
                String setmealName = (String) map.get("name");
                setmealNames.add(setmealName);

            }
            data.put("setmealNames", setmealNames);
            return new Result(true, MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS, data);

        } catch (Exception e) {
            e.printStackTrace();

            return new Result(false, MessageConstant.GET_SETMEAL_COUNT_REPORT_FAIL);
        }

    }

    //运营报表数据查询
    @RequestMapping("/getBusinessReportData")
    public Result getBusinessReportData(){

        try {
            Map<String, Object> data = reportService.getBusinessReportData();

            return new Result(true, MessageConstant.GET_BUSINESS_REPORT_SUCCESS, data);
        } catch (Exception e) {
            e.printStackTrace();

            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }

    }

    //将运营数据写入Excel文件并提供给客户端下载，导出运营数据
    @RequestMapping("/exportBusinessReport")
    public Result exportBusinessReport(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> result = null;
        try {
            //获取运营报表数据
            result = reportService.getBusinessReportData();

            //取出返回结果数据，准备将报表数据写入到Excel文件中
            String reportDate = (String) result.get("reportDate");
            Integer todayNewMember = (Integer) result.get("todayNewMember");
            Integer totalMember = (Integer) result.get("totalMember");
            Integer thisWeekNewMember = (Integer) result.get("thisWeekNewMember");
            Integer thisMonthNewMember = (Integer) result.get("thisMonthNewMember");
            Integer todayOrderNumber = (Integer) result.get("todayOrderNumber");
            Integer thisWeekOrderNumber = (Integer) result.get("thisWeekOrderNumber");
            Integer thisMonthOrderNumber = (Integer) result.get("thisMonthOrderNumber");
            Integer todayVisitsNumber = (Integer) result.get("todayVisitsNumber");
            Integer thisWeekVisitsNumber = (Integer) result.get("thisWeekVisitsNumber");
            Integer thisMonthVisitsNumber = (Integer) result.get("thisMonthVisitsNumber");
            List<Map> hotSetmeal = (List<Map>) result.get("hotSetmeal");

            //获得template目录的绝对路径
            String dir = request.getSession().getServletContext().getRealPath("template");
            //File.separator  可以适应操作系统的路径分割符
            String filePath = dir + File.separator + "report_template.xlsx";
            //根据提供的模板文件创建一个Excel表格对象
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(filePath)));
            XSSFSheet sheet = workbook.getSheetAt(0);

            XSSFRow row = sheet.getRow(2);
            row.getCell(5).setCellValue(reportDate);//日期

            row = sheet.getRow(4);
            row.getCell(5).setCellValue(todayNewMember);//新增会员数（本日）
            row.getCell(7).setCellValue(totalMember);//总会员数

            row = sheet.getRow(5);
            row.getCell(5).setCellValue(thisWeekNewMember);//本周新增会员数
            row.getCell(7).setCellValue(thisMonthNewMember);//本月新增会员数

            row = sheet.getRow(7);
            row.getCell(5).setCellValue(todayOrderNumber);//今日预约数
            row.getCell(7).setCellValue(todayVisitsNumber);//今日到诊数

            row = sheet.getRow(8);
            row.getCell(5).setCellValue(thisWeekOrderNumber);//本周预约数
            row.getCell(7).setCellValue(thisWeekVisitsNumber);//本周到诊数

            row = sheet.getRow(9);
            row.getCell(5).setCellValue(thisMonthOrderNumber);//本月预约数
            row.getCell(7).setCellValue(thisMonthVisitsNumber);//本月到诊数

            int rowNum = 12;
            for(Map map : hotSetmeal){//热门套餐
                String name = (String) map.get("name");
                Long setmeal_count = (Long) map.get("setmeal_count");
                BigDecimal proportion = (BigDecimal) map.get("proportion");
                row = sheet.getRow(rowNum ++);
                row.getCell(4).setCellValue(name);//套餐名称
                row.getCell(5).setCellValue(setmeal_count);//预约数量
                row.getCell(6).setCellValue(proportion.doubleValue());//占比
            }

            //使用输出流进行表格下载，基于浏览器作为客户端下载
            ServletOutputStream out = response.getOutputStream();
            //设置响应头信息
            response.setContentType("application/vnd.ms-excel");//代表的是excel文件类型
            //指定以附件形式下载
            response.setHeader("content-Disposition", "attachment;filename=report.xlsx");

            workbook.write(out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
