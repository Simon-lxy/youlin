package com.tanhua.admin.controller;


import com.tanhua.admin.service.AnalysisService;
import com.tanhua.model.vo.AnalysisSummaryVo;
import com.tanhua.model.vo.DashboardYearVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/dashboard")
public class DashBoradController {


    @Autowired
    private AnalysisService analysisService;

    @GetMapping("/summary")
    public AnalysisSummaryVo getSummary() {
        return analysisService.getSummary();
    }

    @GetMapping("/users")
    public DashboardYearVo getUsers(Integer type, String sd, String ed) throws ParseException {
        return analysisService.getUsers(type,sd,ed);
    }
}
