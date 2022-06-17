package com.itheima.test;

import com.baidu.aip.face.AipFace;
import com.tanhua.autoconfig.template.AipFaceTemplate;
import com.tanhua.server.AppServerApplication;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class FaceTest {

    @Autowired
    private AipFaceTemplate aipFaceTemplate;

    //设置APPID/AK/SK
    public static final String APP_ID = "25895658";
    public static final String API_KEY = "HFYLieqUf33mmuYfi9lZBaef";
    public static final String SECRET_KEY = "Ka9sVB427ll7OaVlrHYhXQBMRDsux3rs";

    public static void main(String[] args) {
        // 初始化一个AipFace
        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 调用接口
        String image = "";
        String imageType = "URL";

        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age");
        options.put("max_face_num", "2");
        options.put("face_type", "LIVE");

        // 人脸检测
        JSONObject res = client.detect(image, imageType, options);
        System.out.println(res.toString(2));

    }

    @Test
    public void detectFace() {
        String image = "C:\\Users\\72840\\Desktop\\dc8e0641f0c0948d4321be4373f8015.jpg";
        boolean detect = aipFaceTemplate.detect(image);
        System.out.println(detect);
    }

}
