package com.ti.crawlers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.ti.crawlers.lianjia.LianJiaApi;
import com.ti.crawlers.lianjia.LianJiaInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lianjia {

    public static SSLSocketFactory getSSLSocketFactory() throws Exception {
        //创建一个不验证证书链的证书信任管理器。
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[0];
            }
        }};

        // Install the all-trusting trust manager
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        // Create an ssl socket factory with our all-trusting manager
        return sslContext.getSocketFactory();
    }
    public static LianJiaApi lianJiaApi;
    static{
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.proxy(new Proxy(Proxy.Type.HTTP,new InetSocketAddress("localhost",8888)));
            builder.retryOnConnectionFailure(true)
                    .sslSocketFactory(getSSLSocketFactory())
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String s, SSLSession sslSession) {
                            return true;
                        }});
            builder.addInterceptor(new LianJiaInterceptor());
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://wechat.lianjia.com")
                    .client(builder.build())
                    .build();
            lianJiaApi = retrofit.create(LianJiaApi.class);
        }catch (Exception e){

        }
    }

    public static JSONObject page(int page_index) throws Exception{
        Call<ResponseBody> call = lianJiaApi.ershoufang("310000","undefined","",
                "",page_index * 10,10,"");
        Response<ResponseBody> responseBody = call.execute();
        String body = responseBody.body().string();
        JSONObject jsonObject = JSON.parseObject(body);
        return jsonObject;
    }

    public static JSONObject page(int page_index,String condition) throws Exception{
        Call<ResponseBody> call = lianJiaApi.ershoufang("310000",condition,"",
                "",page_index * 10,10,"");
        Response<ResponseBody> responseBody = call.execute();
        String body = responseBody.body().string();
        JSONObject jsonObject = JSON.parseObject(body);
        return jsonObject;
    }

    public List<JSONObject> loadAll(String name, String key) throws Exception{
        List<JSONObject> pages = new ArrayList<>();
        int page_index = 0;
        while(true){
            JSONObject jsonObject = page(page_index,key);
            JSONObject data = jsonObject.getJSONObject("data");
            int total_count = data.getInteger("total_count");
            if(total_count < page_index ++ * 10){
                break;
            }
            pages.add(jsonObject);
            Thread.sleep(200);
        }
        return pages;
    }

    public static void main(String[] args) {
        try {
            Lianjia lianjia = new Lianjia();
            List<JSONObject> pages = lianjia.loadAll("张江","b611900148");
            pages.stream().forEach(new Consumer<JSONObject>() {
                @Override
                public void accept(JSONObject jsonObject) {
                    Document document = new
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
