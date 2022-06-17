package com.tanhua.server.controller;

import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.UserInfoService;
import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UsersControler {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserService userService;


    /**
     * 查询用户资料
     */
    @GetMapping
    public ResponseEntity users(Long userID) {
        if(userID == null) {
            userID = UserHolder.getUserId();
        }
        UserInfoVo userInfo = userInfoService.findById(userID);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 更新用户资料
     */
    @PutMapping
    public ResponseEntity updateUserInfo(@RequestBody UserInfo userInfo) {
        userInfo.setId(UserHolder.getUserId());
        userInfoService.update(userInfo);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/header")
    public ResponseEntity updateUserPhoto(MultipartFile headPhoto, @RequestHeader("Authorization") String token) throws IOException {
        userInfoService.updateHead(headPhoto, UserHolder.getUserId());
        return ResponseEntity.ok(null);
    }

    /**
     * 修改手机号（向手机发送验证码）
     * @param token
     * @return
     */
    @PostMapping("/phone/sendVerificationCode")
    public ResponseEntity sendVerificationCode(@RequestHeader("Authorization") String token) {
        String phone = UserHolder.getMobile();
        userService.sendMsg(phone);
        return ResponseEntity.ok(null);
    }


    /**
     * 修改手机号（校验验证码）
     * @param token
     * @return
     */
    @PostMapping("/phone/checkVerificationCode")
    public ResponseEntity checkVerificationCode(@RequestBody Map<String,String> map, @RequestHeader("Authorization") String token) {
        String code = map.get("verificationCode");
        boolean verification = userService.updatePhoneVerification(UserHolder.getMobile(), code);
        return ResponseEntity.ok(verification);
    }


    /**
     * 修改手机号（输入新的手机号）
     * @return
     */
    @PostMapping("/phone")
    public ResponseEntity updatePhone(@RequestBody String phone) {
        userService.updatePhone(phone);
        return ResponseEntity.ok(null);
    }

}
