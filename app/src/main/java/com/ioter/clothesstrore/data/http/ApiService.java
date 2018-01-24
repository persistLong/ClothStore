package com.ioter.clothesstrore.data.http;

import com.ioter.clothesstrore.been.requestBeen.BaseBean;
import com.ioter.clothesstrore.been.requestBeen.ClothBean;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Ivan on 2016/12/30.
 */

public interface ApiService
{


    //public static final String BASE_URL = "http://112.124.22.238:8081/course_api/cniaoplay/";
    public static final String BASE_URL = "http://172.16.33.84/api/center/";


    @GET("getproductbyepc")
    public Observable<BaseBean<ClothBean>> getProductByEpc(@Query("epc") String epc);

//
//    @GET("featured")
//    public Call<PageBean<AppInfo>> getApps(@Query("p") String jsonParam);



/*    @GET("featured2")
    public Observable<BaseBean<PageBean<AppInfo>>> getApps(@Query("p") String jsonParam);


    @GET("index")
    public  Observable<BaseBean<ClothBean>> index();



     @GET("toplist")
    public  Observable<BaseBean<PageBean<AppInfo>>> topList(@Query("page") int page);

     @GET("game")
    public  Observable<BaseBean<PageBean<AppInfo>>> games(@Query("page") int page);


    @POST("login")
    Observable<BaseBean<LoginBean>> login(@Body LoginRequestBean param);


    @GET("category")
    Observable<BaseBean<List<Category>>> getCategories();


    @GET("category/featured/{categoryid}")
    Observable<BaseBean<PageBean<AppInfo>>> getFeaturedAppsByCategory(@Path("categoryid") int categoryid, @Query("page") int page);

    @GET("category/toplist/{categoryid}")
    Observable<BaseBean<PageBean<AppInfo>>> getTopListAppsByCategory(@Path("categoryid") int categoryid, @Query("page") int page);

    @GET("category/newlist/{categoryid}")
    Observable<BaseBean<PageBean<AppInfo>>> getNewListAppsByCategory(@Path("categoryid") int categoryid, @Query("page") int page);*/
















}
