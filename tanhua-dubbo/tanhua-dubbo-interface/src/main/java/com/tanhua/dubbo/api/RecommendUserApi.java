package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.PageResult;

import java.util.List;

public interface RecommendUserApi {

    RecommendUser queryWithMaxScore(Long toUserId);

    //分页查询
    PageResult queryRecommendUserList(Integer page, Integer pagesize, Long toUserId);

    //根据操作人id和查看的用户id，查询两者的推荐数据
    RecommendUser queryByUserId(Long userId, Long userId1);

    /**
     * 查询探花列表，查询时需要排除喜欢和不喜欢的用户
     */
    List<RecommendUser> queryCardsList(Long userId, int counts);
}
