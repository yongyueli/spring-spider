package com.ti.spider;

import com.alibaba.fastjson.JSONObject;
import com.ti.crawlers.Lianjia;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableEurekaClient
@RestController
@Slf4j
public class SpiderApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpiderApplication.class, args);
	}

	@Value("${server.port}")
    String port;

	@RequestMapping("/hi")
    public String home(@RequestParam String name){
	    return "hi " + name + ",i am from port " + port;
    }

	@RequestMapping("/page")
	public String page(@RequestParam int page_index){
		try {
			JSONObject list = Lianjia.page(page_index);
			return list.toJSONString();
		} catch (Exception e) {
		    log.error("search lianjia page failed ",e);
			return null;
		}
	}
}
