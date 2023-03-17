package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.FocusUser;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.PageResult;

import java.util.List;

public interface VideoApi {

    //保存视频
    String save(Video video);

    //根据vid查询数据列表
    List<Video> findMovementsByVids(List<Long> vids);

    //分页查询数据列表
    List<Video> queryVideoList(int page, Integer pagesize);

    //根据用户id查询
    PageResult findByUserId(Integer page, Integer pagesize, Long userId);

    //保存用户关注视频作者记录
    void saveFocusUser(FocusUser focusUser);

    //删除用户关注视频作者记录
    void deleteFollowUser(Long userId, Long followUserId);
}
