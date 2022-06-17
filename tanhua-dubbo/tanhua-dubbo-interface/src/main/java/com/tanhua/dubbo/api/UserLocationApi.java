package com.tanhua.dubbo.api;

import java.util.List;

public interface UserLocationApi {

    //更新地理位置
    Boolean updateLocation(Long userId, Double longitude, Double latitude, String address);

    /**
     * 根据位置搜索附近人的所有ID
     */
    List<Long> queryNearUser(Long userId, Double meter);
}
