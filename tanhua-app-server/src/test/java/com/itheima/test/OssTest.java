package com.itheima.test;


import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.server.AppServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class OssTest {

    @Autowired
    private OssTemplate ossTemplate;

    @Test
    public void uploadTest() throws FileNotFoundException {

        String filePath = "C:\\Users\\72840\\Pictures\\5bcae75d874fd (2).jpg";
        FileInputStream fileInputStream = new FileInputStream(new File(filePath));
        String imageUrl = ossTemplate.upload(filePath, fileInputStream);
        System.out.println(imageUrl);
    }
}
