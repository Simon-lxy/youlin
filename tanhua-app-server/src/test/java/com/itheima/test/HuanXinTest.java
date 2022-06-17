//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.itheima.test;

import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.model.domain.User;
import com.tanhua.server.AppServerApplication;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {AppServerApplication.class}
)
public class HuanXinTest {
    @Autowired
    private HuanXinTemplate template;
    @DubboReference
    private UserApi userApi;

    public HuanXinTest() {
    }

    @Test
    public void testCreateUser() {
        this.template.createUser("106", "123456");
    }

    @Test
    public void register() {
        for(int i = 1; i <= 106; ++i) {
            User user = this.userApi.findById((long)i);
            if (user != null) {
                Boolean create = this.template.createUser("hx" + user.getId(), "123456");
                if (create) {
                    user.setHxUser("hx" + user.getId());
                    user.setHxPassword("123456");
                    this.userApi.update(user);
                }
            }
        }

    }
}
