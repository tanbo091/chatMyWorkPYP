package com.work.fyc.MyWork.net;

import com.work.fyc.MyWork.entity.LoginResultEntity;
import com.work.fyc.MyWork.entity.MainDataEntity;
import com.work.fyc.MyWork.entity.ResultEntity;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * interface 是接口
 * retrofit2 基础知识。可以百度 retrofit2 进行学习
 */
public interface ApiService {

    /**
     * 这是登录API
     *
     * @param map 上传的参数 。 跟 volley 的 getParams 的 map 一样
     * @return 返回一个 ResultEntity 。 具体请看 ResultEntity 里面的说明。
     * @POST 就代表 使用 post 方式
     * @see ResultEntity  快捷键： Ctrl + 左键 可以点击 ResultEntity 过去
     */
    @POST("MyForum1/login")
    Observable<LoginResultEntity> loginApi(@Body Map<String, String> map);

    /**
     * 这是首页列表API
     *
     * @param map 上传的参数 。 跟 volley 的 getParams 的 map 一样
     * @return 返回一个 ResultEntity 。 具体请看 ResultEntity 里面的说明。
     * @POST 就代表 使用 post 方式
     * @see ResultEntity  快捷键： Ctrl + 左键 可以点击 ResultEntity 过去
     */
    @POST("MyForum1/main")
    Observable<MainDataEntity> getMainList(@Body Map<String, String> map);

    /**
     * 这是删帖的API
     *
     * @param map 上传的参数 。 跟 volley 的 getParams 的 map 一样
     * @return 返回一个 ResultEntity 。 具体请看 ResultEntity 里面的说明。
     * @POST 就代表 使用 post 方式
     * @see ResultEntity  快捷键： Ctrl + 左键 可以点击 ResultEntity 过去
     */
    @POST("MyForum1/deleteCard")
    Observable<ResultEntity> deletePost(@Body Map<String, String> map);

    /**
     * 这是置顶的API
     *
     * @param map 上传的参数 。 跟 volley 的 getParams 的 map 一样
     * @return 返回一个 ResultEntity 。 具体请看 ResultEntity 里面的说明。
     * @POST 就代表 使用 post 方式
     * @see ResultEntity  快捷键： Ctrl + 左键 可以点击 ResultEntity 过去
     */
    @POST("MyForum1/keepTop")
    Observable<ResultEntity> keepTopPost(@Body Map<String, String> map);

    /**
     * 这是置顶的API
     *
     * @param map 上传的参数 。 跟 volley 的 getParams 的 map 一样
     * @return 返回一个 ResultEntity 。 具体请看 ResultEntity 里面的说明。
     * @POST 就代表 使用 post 方式
     * @see ResultEntity  快捷键： Ctrl + 左键 可以点击 ResultEntity 过去
     */
    @POST("MyForum1/selfCard")
    Observable<MainDataEntity> getSelfCard(@Body Map<String, String> map);

}
