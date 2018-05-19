package com.ti.crawlers.lianjia;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface LianJiaApi {
    @GET("/ershoufang/search")
//    @Headers({
//            "Lianjia-Uuid:43d2a9d65b99ca2ebc0bb8a779874f87",
//            "Authorization:bGp3eGFwcDplMTZkMzgxMjhjMDVlYjczN2IzMjBlMWNjMjA1MGU5YQ==",
//            "Wxminiapp-SDK-Version:2.0.7",
//            "Lianjia-Wxminiapp-Version:0.1",
//            "Lianjia-Source:ljwxapp",
//            "Time-Stamp:1526614828798",
//            "Wx-Version:6.6.6"
//    })
    Call<ResponseBody> ershoufang(@Query("city_id")String city_id, @Query("condition")String condition,
                                  @Query("query")String query, @Query("order")String order,
                                  @Query("offset")int offset, @Query("limit")int limit,
                                  @Query("sign")String sign);
}
