package com.tanhua.server.controller;

import com.tanhua.model.vo.NearUserVo;
import com.tanhua.server.service.BaiduService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/baidu")
public class BaiduController {

    @Autowired
    private BaiduService baiduService;


    /**
     * 更新地理位置
     * @param param
     * @return
     */
    @PostMapping
    public ResponseEntity updateLocation(@RequestBody Map param) {
        Double longitude = Double.valueOf(param.get("longitude").toString());
        Double latitude = Double.valueOf(param.get("latitude").toString());
        String address = param.get("addr").toString();
        this.baiduService.updateLocation(longitude, latitude, address);
        return ResponseEntity.ok(null);
    }


}
