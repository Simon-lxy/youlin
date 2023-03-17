package com.tanhua.admin;

import com.tanhua.admin.mapper.AnalysisMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AnalysisMapperTest {

    @Resource
    private AnalysisMapper analysisMapper;


    @Test
    public void testSumAnalysisCount() {
        Long recored_date = analysisMapper.sumAnalysisData("num_active", "2022-05-01", "2022-09-20");
        System.out.println(recored_date);
    }



    @Test
    public void testSumUsers() {
        Integer recored_date = analysisMapper.sumUsers("num_active", "2022-05-01", "2022-09-20");
        System.out.println(recored_date);
    }

}
