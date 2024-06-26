package com.tanhua.admin.task;

import com.tanhua.admin.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class AnalysisTask {

    @Autowired
    private AnalysisService analysisService;

    @Scheduled(cron = "0/10 * * * * ?")  //todo 秒 分 时 日 月 周 年
    public void analysis () {
        //业务逻辑
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("当前时间=" + time);
        try {
            analysisService.analysis();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
