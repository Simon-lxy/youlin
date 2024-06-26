package com.tanhua.server.service;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.PageUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.FocusUser;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.VideoVo;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SmallVideosService {

    @DubboReference
    private VideoApi videoApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private FastFileStorageClient client;

    @Autowired
    private FdfsWebServer webServer;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    /**
     * 发布视频
     * @param videoThumbnail  视频封面
     * @param videoFile  视频文件
     */
    @CacheEvict(value="videos",allEntries = true)
    public void saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        if(videoFile.isEmpty() || videoThumbnail.isEmpty()) {
            throw new BusinessException(ErrorResult.error());
        }

        //1、将视频上传到FastDFS,获取访问URL
        String filename = videoFile.getOriginalFilename();  // abc.mp4
        filename = filename.substring(filename.lastIndexOf(".")+1);
        StorePath storePath = client.uploadFile(videoFile.getInputStream(), videoFile.getSize(), filename, null);
        String videoUrl = webServer.getWebServerUrl() + storePath.getFullPath();
        //2、将封面图片上传到阿里云OSS，获取访问的URL
        String imageUrl = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());
        //3、构建Videos对象
        Video video = new Video();
        video.setUserId(UserHolder.getUserId());
        video.setPicUrl(imageUrl);
        video.setVideoUrl(videoUrl);
        video.setText("我就是我，不一样的烟火");
        //4、调用API保存数据
        String videoId = videoApi.save(video);
        if(StringUtils.isEmpty(videoId)) {
            throw new BusinessException(ErrorResult.error());
        }
    }

    //查询视频列表
    @Cacheable(
            value="videos",
            key = "T(com.tanhua.server.interceptor.UserHolder).getUserId()+'_'+#page+'_'+#pagesize")  //userid _ page_pagesize
    public PageResult queryVideoList(Integer page, Integer pagesize) {

        //1、查询redis数据
        String redisKey = Constants.VIDEOS_RECOMMEND +UserHolder.getUserId();
        String redisValue = redisTemplate.opsForValue().get(redisKey);
        //2、判断redis数据是否存在，判断redis中数据是否满足本次分页条数
        List<Video> list = new ArrayList<>();
        int redisPages = 0;
        if(!StringUtils.isEmpty(redisValue)) {
            //3、如果redis数据存在，根据VID查询数据
            String[] values = redisValue.split(",");
            //判断当前页的起始条数是否小于数组总数
            if( (page -1) * pagesize < values.length) {
                List<Long> vids = Arrays.stream(values).skip((page - 1) * pagesize).limit(pagesize)
                        .map(e->Long.valueOf(e))
                        .collect(Collectors.toList());
                //5、调用API根据PID数组查询动态数据
                list = videoApi.findMovementsByVids(vids);
            }
            redisPages = PageUtil.totalPage(values.length,pagesize);
        }
        //4、如果redis数据不存在，分页查询视频数据
        if(list.isEmpty()) {
            //page的计算规则，  传入的页码  -- redis查询的总页数
            list = videoApi.queryVideoList(page - redisPages, pagesize);  //page=1 ?
        }
        //5、提取视频列表中所有的用户id
        List<Long> userIds = CollUtil.getFieldValues(list, "userId", Long.class);
        //6、查询用户信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        //7、构建返回值
        List<VideoVo> vos = new ArrayList<>();
        for (Video video : list) {
            UserInfo info = map.get(video.getUserId());
            if(info != null) {
                VideoVo vo = VideoVo.init(info, video);
                vos.add(vo);
            }
        }

        return new PageResult(page,pagesize,0l,vos);
    }


    /**
     * 关注视频作者
     * @param followUserId   关注作者id
     */
    public void userFocus(Long followUserId) {
        //创建FollowUser对象，并且设置属性
        FocusUser focusUser = new FocusUser();
        focusUser.setFollowUserId(followUserId);
        focusUser.setUserId(UserHolder.getUserId());
        //调用APi保存
        videoApi.saveFocusUser(focusUser);
        //将关注记录存入redis
        String key = Constants.FOCUS_USER + UserHolder.getUserId();
        String hashKey = String.valueOf(followUserId);
        redisTemplate.opsForHash().put(key,hashKey,"1");
    }

    /**
     * 取消关注视频作者
     * @param followUserId
     */
    public void unUserFocus(Long followUserId) {
        //调用API删除关注数据
        videoApi.deleteFollowUser(UserHolder.getUserId(),followUserId);
        //删除redis中关注记录
        String key = Constants.FOCUS_USER + UserHolder.getUserId();
        String hashKey = String.valueOf(followUserId);
        redisTemplate.opsForHash().delete(key,hashKey);
    }
}
