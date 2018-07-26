package com.threetree.thttp;

import com.threetree.tthttp.Result;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2018/7/17.
 */

public interface ApiService {
    /**
     * 文案
     * @param memberId
     * @param type
     * @return
     */
    @FormUrlEncoded
    @POST("sys/document")
    Observable<Result<String>> getDoc(@Field("memberId") long memberId,
                                      @Field("type") int type);
}
