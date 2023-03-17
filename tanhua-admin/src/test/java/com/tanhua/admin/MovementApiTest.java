package com.tanhua.admin;


import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.PageResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MovementApiTest {


    @Resource
    private MovementApi movementApiImpl;


    @Test
    public void findByUserId() {

        PageResult byUserId = movementApiImpl.findByUserId(106L, 1, 1, 10);

        List<Movement> items = (List<Movement>) byUserId.getItems();

        for (Movement item : items) {
            System.out.println(item);
        }
    }
}
