package com.tanhua.admin.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.admin.mapper.AnalysisMapper;
import com.tanhua.admin.mapper.LogMapper;
import com.tanhua.model.domain.Analysis;
import com.tanhua.model.dto.YearDto;
import com.tanhua.model.vo.AnalysisSummaryVo;
import com.tanhua.model.vo.DashboardYearVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class AnalysisService {

    @Autowired
    private LogMapper logMapper;

    @Autowired
    private AnalysisMapper analysisMapper;


    /**
     * 定时统计头tb_log表中的数据,保存或者更新tb_analysis表
     *  1.查询tb_log表（注册用户数，登陆用户数，活跃用户数，次日留存）
     *  2.构造Analysis对象
     *  3.保存或者更新
     */
    public void analysis() throws ParseException {
        //1.定义查询的日期
        String todayStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String yesDayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
        //2.统计数据-注册数量
        Integer regCount = logMapper.queryByTypeAndLogTime("0102", todayStr);
        //3.统计数据-登陆数量
        Integer loginCount = logMapper.queryByTypeAndLogTime("0101", todayStr);
        //4.统计数据-活跃数量
        Integer activeCount = logMapper.queryByLogTime(todayStr);
        //5.统计数据次日留存
        Integer numRetention1d = logMapper.queryNumRetention1d(todayStr, yesDayStr);
        //6.构造Analysis对象
        //7.根据日期查询数据
        QueryWrapper<Analysis> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("record_date",new SimpleDateFormat("yyyy-MM-dd").parse(todayStr));
        Analysis analysis = analysisMapper.selectOne(queryWrapper);
        //8.如果存在，更新，如果不存在保存
        if(analysis != null) {
            //更新
            analysis.setNumRegistered(regCount);
            analysis.setNumLogin(loginCount);
            analysis.setNumActive(activeCount);
            analysis.setNumRetention1d(numRetention1d);
            analysisMapper.updateById(analysis);
        } else {
            //保存
            analysis = new Analysis();
            analysis.setNumRegistered(regCount);
            analysis.setNumLogin(loginCount);
            analysis.setNumActive(activeCount);
            analysis.setNumRetention1d(numRetention1d);
            analysis.setRecordDate(new SimpleDateFormat("yyyy-MM-dd").parse(todayStr));
            analysis.setCreated(new Date());
            analysisMapper.insert(analysis);
        }
    }


    /**
     * 概要信息统计
     * @return
     */
    public AnalysisSummaryVo getSummary() {

        AnalysisSummaryVo analysisSummaryVo = new AnalysisSummaryVo();

        DateTime dateTime = DateUtil.parseDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        //累积用户数
//        analysisSummaryVo.setCumulativeUsers(Long.valueOf(1000));
        analysisSummaryVo.setCumulativeUsers(Long.valueOf(1000));
        //过去30天活跃用户
        analysisSummaryVo.setActivePassMonth(this.queryActiveUserCount(dateTime,-30));

        //过去7天活跃用户
        analysisSummaryVo.setActivePassWeek(this.queryActiveUserCount(dateTime,-7));
        //今日活跃用户
        analysisSummaryVo.setActiveUsersToday(this.queryActiveUserCount(dateTime,0));
        //今日新增用户
        analysisSummaryVo.setNewUsersToday(this.queryRegisterUserCount(dateTime,0));
        //今日新增用户涨跌率，单位百分数，正数为涨，负数为跌
        analysisSummaryVo.setNewUsersTodayRate(computeRate(
                analysisSummaryVo.getNewUsersToday(),
                this.queryRegisterUserCount(dateTime, -1)
        ));

        //今日登录次数
        analysisSummaryVo.setLoginTimesToday(this.queryLoginUserCount(dateTime, 0));

        //今日登录次数涨跌率，单位百分数，正数为涨，负数为跌
        analysisSummaryVo.setLoginTimesTodayRate(computeRate(
                analysisSummaryVo.getLoginTimesToday(),
                this.queryLoginUserCount(dateTime, -1)
        ));

        return analysisSummaryVo;
    }


    /**
     * 查询活跃用户的数量
     */
    public Long queryActiveUserCount(DateTime today, int offset) {
        return this.queryUserCount(today, offset, "num_active");
    }

    /**
     * 查询注册用户的数量
     */
    public Long queryRegisterUserCount(DateTime today, int offset) {
        return this.queryUserCount(today, offset, "num_registered");
    }

    /**
     * 查询登录用户的数量
     */
    public Long queryLoginUserCount(DateTime today, int offset) {
        return this.queryUserCount(today, offset, "num_login");
    }



    /**
     * 构造日期字符串
     * @param today   当天日期
     * @param offset  日期偏移量  过去多少天到现在
     * @param typeCount     统计数据的类型的个数     注册   登陆   活跃
     * @return
     */
    private  Long queryUserCount(DateTime today,int offset,String typeCount) {

        String gtToday = today.toDateStr();
        String leToday = offsetDay(today,offset);

        return this.queryAnalysisCount(typeCount,leToday,gtToday);
    }

    /**
     * 调用AnalysisMapper的方法查询一定时间内统计数据类型的总数
     * @param column
     * @param leToday
     * @param gtToday
     * @return
     */
    private Long queryAnalysisCount(String column,String leToday,String gtToday){
        return analysisMapper.sumAnalysisData(column,leToday,gtToday);
    }


    /**
     * 操作类型（注册   登陆   活跃）一定时间内   用户的涨跌率，单位百分数，正数为涨，负数为跌
     * @param current
     * @param last
     * @return
     */
    private static BigDecimal computeRate(Long current, Long last) {
        BigDecimal result;
        if (last == 0) {
            // 当上一期计数为零时，此时环比增长为倍数增长
            result = new BigDecimal((current - last) * 100);
        } else {
            result = BigDecimal.valueOf((current - last) * 100).divide(BigDecimal.valueOf(last), 2, BigDecimal.ROUND_HALF_DOWN);
        }
        return result;
    }


    /**
     * 求当前日期之前offset天前的日期  并且转换成字符串
     * @param date
     * @param offSet
     * @return
     */
    private static String offsetDay(Date date,int offSet) {
        return DateUtil.offsetDay(date,offSet).toDateStr();
    }





    /**
     * 新增、活跃用户、次日留存率
     * @return
     */
    public DashboardYearVo getUsers(Integer type,String sd,String ed) throws ParseException {

        DashboardYearVo vos = new DashboardYearVo();
        YearDto thisYear = new YearDto();
        YearDto lastYear = new YearDto();

        //日期字符串格式化并转换成Date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date sdDate = simpleDateFormat.parse(sd);  //Unparseable date: "1663165137677"
        Date edDate = simpleDateFormat.parse(ed);


        //将处理过的Date重新转成字符串
        /*String sdTime = DateUtil.format(sdDate,);
        String edTime = DateUtil.format(edDate,"yyyy-MM-dd");*/
        String sdTime = simpleDateFormat.format(sdDate);
        String edTime = simpleDateFormat.format(edDate);


        //今年一段时间的新增、活跃用户、次日留存率
        if (type == 101) {
            thisYear.setAmount(analysisMapper.sumUsers("num_registered",sdTime,edTime));
            thisYear.setTitle(new Date().toString());
        }

        if (type == 102) {
            thisYear.setAmount(analysisMapper.sumUsers("num_active",sdTime,edTime));
            thisYear.setTitle(new Date().toString());
        }

        if (type == 103) {
            thisYear.setAmount(analysisMapper.sumUsers("num_retention1d",sdTime,edTime));
            thisYear.setTitle(new Date().toString());
        }

        //去年一段时间的新增、活跃用户、次日留存率
        if (type == 101) {
            lastYear.setAmount(analysisMapper.sumUsers("num_registered",sdTime,edTime));
            lastYear.setTitle(new Date().toString());
        }

        if (type == 102) {
            lastYear.setAmount(analysisMapper.sumUsers("num_active",sdTime,edTime));
            lastYear.setTitle(new Date().toString());
        }

        if (type == 103) {
            lastYear.setAmount(analysisMapper.sumUsers("num_retention1d",sdTime,edTime));
            lastYear.setTitle(new Date().toString());
        }

        vos.setThisYear(thisYear);
        vos.setLastYear(lastYear);

        return vos;
    }
}


