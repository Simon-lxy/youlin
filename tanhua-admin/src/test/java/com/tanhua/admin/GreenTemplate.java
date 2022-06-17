package com.tanhua.admin;


import com.tanhua.autoconfig.template.AliyunGreenTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GreenTemplate {

    @Autowired
    private AliyunGreenTemplate aliyunGreenTemplate;

    @Test
    public void test() throws Exception {
        Map<String, String> map = aliyunGreenTemplate.greenTextScan("今天是个好日子");
        map.forEach((k,v)->System.out.println(k+"--"+v));
    }
}
