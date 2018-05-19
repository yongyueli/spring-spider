package com.ti.crawlers.lianjia;

import okhttp3.*;
import org.springframework.util.DigestUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class LianJiaInterceptor  implements Interceptor{


    private final static char[] HEX = "0123456789abcdef".toCharArray();


    public static String bytes2Hex(byte[] bys) {
        char[] chs = new char[bys.length * 2];
        for(int i = 0, offset = 0; i < bys.length; i++) {
            chs[offset++] = HEX[bys[i] >> 4 & 0xf];
            chs[offset++] = HEX[bys[i] & 0xf];
        }
        return new String(chs);
    }


    public String getAuthorization(String path) throws UnsupportedEncodingException {
        String subfix = "6e8566e348447383e16fdd1b233dbb49";
        TreeSet<String> treeSet = new TreeSet<>();
        treeSet.addAll(Arrays.asList(path.split("&")));
        String paramLine =String.join("",treeSet);
        byte[] bytes = DigestUtils.md5Digest((paramLine + subfix).getBytes());
        String md5 = bytes2Hex(bytes);
//        String md5 = new String(),"UTF-8");
        String authoriztion = new String(Base64.getEncoder().encode(("ljwxapp:"+md5).getBytes()),"UTF-8");
        return authoriztion;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        String path = request.url().url().getQuery();
        request = request.newBuilder()
                .addHeader("Host","wechat.lianjia.com")
                .addHeader("Wxminiapp-SDK-Version","2.0.7")
                .addHeader("Lianjia-Wxminiapp-Version","0.1")
                .addHeader("Lianjia-Source","ljwxapp")
                .addHeader("Time-Stamp",String.valueOf(System.currentTimeMillis()))
                .addHeader("Wx-Version","6.6.6")
                .addHeader("Authorization",getAuthorization(path))
                .addHeader("ios-iOS","11.2.1")
                .addHeader("Referer","https://servicewechat.com/wxe7691063480f6aa0/15/page-frame.html")
                .addHeader("Content-Type","application/json")
                .addHeader("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 11_2_1 like Mac OS X) AppleWebKit/604.4.7 (KHTML, like Gecko) Mobile/15C153 MicroMessenger/6.6.1 NetType/WIFI Language/zh_CN")
                .addHeader("Referer","https://servicewechat.com/wx2c348cf579062e56/46/page-frame.html")
                .build();
        return chain.proceed(request);
    }


}
