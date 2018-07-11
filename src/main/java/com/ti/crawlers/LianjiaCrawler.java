package com.ti.crawlers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ti.crawlers.lianjia.LianJiaApi;
import com.ti.crawlers.lianjia.LianJiaInterceptor;
import com.ti.crawlers.lianjia.LoadUtil;
import com.ti.dao.MongoLianJia;
import okhttp3.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.cert.CertificateException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Component
public class LianjiaCrawler {

    @Autowired
    MongoLianJia mongoLianJia;
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
            if(System.getProperty("Debug")!= null)
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

    public static JSONObject page(int page_index,String condition,int page_size) throws Exception{
        Call<ResponseBody> call = lianJiaApi.ershoufang("310000",condition,"",
                "",page_index * page_size,page_size,"");
        Response<ResponseBody> responseBody = call.execute();
        String body = responseBody.body().string();
        JSONObject jsonObject = JSON.parseObject(body);
        return jsonObject;
    }

    public List<JSONObject> loadArea(String name, String key,int page_size) throws Exception{
        List<JSONObject> pages = new ArrayList<>();
        int page_index = 0;
        while(true){
            JSONObject jsonObject = page(page_index,key,page_size);
            JSONObject data = jsonObject.getJSONObject("data");
            int total_count = data.getInteger("total_count");
            if(total_count == 0)continue;
            if(total_count < page_index ++ * page_size){
                break;
            }
            String json = jsonObject.toJSONString();
            Document document = Document.parse(json);
            mongoLianJia.save("lianjia",document);

            JSONObject dataObject = jsonObject.getJSONObject("data");
            JSONObject listObject = dataObject.getJSONObject("list");
            listObject.forEach(new BiConsumer<String, Object>() {
                @Override
                public void accept(String s, Object object) {
                    JSONObject house = (JSONObject)object;
                    house.put("id",s);
                    house.put("date", LocalDate.now().toString());
                    Document houseDocument = Document.parse(house.toJSONString());
                    mongoLianJia.save("lianjia_house",houseDocument);
                }
            });
            pages.add(jsonObject);
            Thread.sleep(200);
        }
        return pages;
    }


    public List<JSONObject> loadArea(JSONObject areaInfoObject) throws Exception{
        List<JSONObject> pages = new ArrayList<>();
        int page_index = 0;
        int page_size = 20;
        while(true){
            String area_name = areaInfoObject.getString("area_name");
            String sub_area_name = areaInfoObject.getString("sub_area_name");
            String sub_area_key = areaInfoObject.getString("sub_area_key");
            JSONObject jsonObject = page(page_index,sub_area_key,page_size);
            JSONObject data = jsonObject.getJSONObject("data");
            int total_count = data.getInteger("total_count");
            if(total_count <= ++page_index  * page_size){
                break;
            }
            String json = jsonObject.toJSONString();
            Document document = Document.parse(json);
            mongoLianJia.save("lianjia",document);

            JSONObject dataObject = jsonObject.getJSONObject("data");
            JSONObject listObject = dataObject.getJSONObject("list");
            listObject.forEach(new BiConsumer<String, Object>() {
                @Override
                public void accept(String s, Object object) {
                    JSONObject house = (JSONObject)object;
                    house.put("id",s);
                    house.put("date", LocalDate.now().toString());
                    house.putAll(areaInfoObject);
                    Document houseDocument = Document.parse(house.toJSONString());
                    mongoLianJia.save("lianjia_house",houseDocument);
                }
            });
            pages.add(jsonObject);
            Thread.sleep(1000);
        }
        return pages;
    }


    public List<JSONObject> loadCity() throws Exception{
        List<JSONObject> areaList = LoadUtil.loadAreas();
        List<JSONObject> houseList = Lists.newArrayList();
        areaList.forEach(area->{
            try {
                if("不限".equals(area.getString("sub_area_name"))){return;}
                houseList.addAll(loadArea(area));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return houseList;
    }

    public static void main(String[] args) {
        try {
            LianjiaCrawler lianjia = new LianjiaCrawler();
            List<JSONObject> pages = lianjia.loadArea("张江","b611900148",10);
            pages.stream().forEach(new Consumer<JSONObject>() {
                @Override
                public void accept(JSONObject jsonObject) {
                    Document document = new Document();

                    System.out.println(jsonObject);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
