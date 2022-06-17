package com.itheima.test;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
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
public class TestFastDFS {

    //测试将文件上传到FastDFS文件系统中

    //从调度服务器获取，一个目标存储服务器，用于文件上传或者下载
    @Autowired
    private FastFileStorageClient client;


    // 获取存储服务器的请求URL
    @Autowired
    private FdfsWebServer webServer;

    @Test
    public void testFileUpdate() throws FileNotFoundException {
 		//1、指定文件
        File file = new File("C:\\Users\\72840\\Pictures\\pictures\\a1.jpg");
		//2、文件上传
        StorePath path = client.uploadFile(new FileInputStream(file),
                file.length(), "jpg", null);
		//3、拼接访问路径
        String fullPath = path.getFullPath();
        String url = webServer.getWebServerUrl() + fullPath;
        System.out.println(url);
    }
}
