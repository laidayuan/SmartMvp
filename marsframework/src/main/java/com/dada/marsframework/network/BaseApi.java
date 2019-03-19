package com.dada.marsframework.network;

import com.dada.marsframework.model.ResponseModel;
import com.dada.marsframework.model.SuperModel;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;


/**
 * Retrofit + okhttp + rxjava + mvp
 *
 * 文件上传
 * 数据请求
 * 文件下载
 *
 */
public interface BaseApi {

    @GET("test")
    Observable<ResponseModel<SuperModel>> test();

    @GET("top250")
    Observable<ResponseModel<SuperModel>> getTop250(@Query("start") int start, @Query("count")int count);

    @GET("api/XXX/InvitedImage")
    Observable<ResponseBody> getInvitedImage(@QueryMap Map<String, Object> map);


    /**
     *表明是一个表单格式的请求（Content-Type:application/x-www-form-urlencoded）
     * <code>Field("username")</code> 表示将后面的 <code>String name</code> 中name的取值作为 username 的值
     */
    @POST("/form")
    @FormUrlEncoded
    Call<ResponseBody> testFormUrlEncoded1(@Field("username") String name, @Field("age") int age);

    /**
     * {@link Part} 后面支持三种类型，{@link RequestBody}、{@link okhttp3.MultipartBody.Part} 、任意类型
     * 除 {@link okhttp3.MultipartBody.Part} 以外，其它类型都必须带上表单字段({@link okhttp3.MultipartBody.Part} 中已经包含了表单字段的信息)，
     */
    @POST("/form")
    @Multipart
    Call<ResponseBody> testFileUpload1(@Part("name") RequestBody name, @Part("age") RequestBody age, @Part MultipartBody.Part file);
/*
    // 具体使用
    GetRequest_Interface service = retrofit.create(GetRequest_Interface.class);
    // @FormUrlEncoded
    Call<ResponseBody> call1 = service.testFormUrlEncoded1("Carson", 24);

    //  @Multipart
    RequestBody name = RequestBody.create(textType, "Carson");
    RequestBody age = RequestBody.create(textType, "24");

    MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", "test.txt", file);
    Call<ResponseBody> call3 = service.testFileUpload1(name, age, filePart);
    */

    // @Header
    @GET("user")
    Call<SuperModel> getUser(@Header("Authorization") String authorization);

    // @Headers
    @Headers("Authorization: authorization")
    @GET("user")
    Call<SuperModel> getUser();

// 以上的效果是一致的。
// 区别在于使用场景和使用方式
// 1. 使用场景：@Header用于添加不固定的请求头，@Headers用于添加固定的请求头
// 2. 使用方式：@Header作用于方法的参数；@Headers作用于方法

    /**
     * Map的key作为表单的键
     */
    @POST("/form")
    @FormUrlEncoded
    Call<ResponseBody> testFormUrlEncoded2(@FieldMap Map<String, Object> map);
/*
    Map<String, Object> map = new HashMap<>();
        map.put("username", "Carson");
        map.put("age", 24);
    Call<ResponseBody> call2 = service.testFormUrlEncoded2(map);

    */

    /**
     * {@link Part} 后面支持三种类型，{@link RequestBody}、{@link okhttp3.MultipartBody.Part} 、任意类型
     * 除 {@link okhttp3.MultipartBody.Part} 以外，其它类型都必须带上表单字段({@link okhttp3.MultipartBody.Part} 中已经包含了表单字段的信息)，
     */
    @POST("/form")
    @Multipart
    Call<ResponseBody> testFileUpload10(@Part("name") RequestBody name, @Part("age") RequestBody age, @Part MultipartBody.Part file);

    /**
     * PartMap 注解支持一个Map作为参数，支持 {@link RequestBody } 类型，
     * 如果有其它的类型，会被{@link retrofit2.Converter}转换，如后面会介绍的 使用{@link com.google.gson.Gson} 的 {@link retrofit2.converter.gson.GsonRequestBodyConverter}
     * 所以{@link MultipartBody.Part} 就不适用了,所以文件只能用<b> @Part MultipartBody.Part </b>
     */
    @POST("/form")
    @Multipart
    Call<ResponseBody> testFileUpload2(@PartMap Map<String, RequestBody> args, @Part MultipartBody.Part file);

    @POST("/form")
    @Multipart
    Call<ResponseBody> testFileUpload3(@PartMap Map<String, RequestBody> args);
/*
    // 具体使用
    MediaType textType = MediaType.parse("text/plain");
    RequestBody name = RequestBody.create(textType, "Carson");
    RequestBody age = RequestBody.create(textType, "24");
    RequestBody file = RequestBody.create(MediaType.parse("application/octet-stream"), "这里是模拟文件的内容");

    // @Part
    MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", "test.txt", file);
    Call<ResponseBody> call3 = service.testFileUpload1(name, age, filePart);
        ResponseBodyPrinter.printResponseBody(call3);

    // @PartMap
    // 实现和上面同样的效果
    Map<String, RequestBody> fileUpload2Args = new HashMap<>();
        fileUpload2Args.put("name", name);
        fileUpload2Args.put("age", age);
    //这里并不会被当成文件，因为没有文件名(包含在Content-Disposition请求头中)，但上面的 filePart 有
    //fileUpload2Args.put("file", file);
    Call<ResponseBody> call4 = service.testFileUpload2(fileUpload2Args, filePart); //单独处理文件
        ResponseBodyPrinter.printResponseBody(call4);
        */

    @Multipart
    @POST
    Observable<ResponseBody> uploadFile(@Url String url, @Part MultipartBody.Part file);

    @Multipart
    @POST("mobile/upload")
    Call<ResponseBody> upload(@Part MultipartBody.Part file);

    //"application/otcet-stream" ：八进制数据流的形式传输的。 "multipart/form-data" ： 以Form表单的形式上传文件。
    //File file = new File(filePath);
    //RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
    //MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

    @Streaming  //下载大文件使用
    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlAsync(@Url String fileUrl);

}
