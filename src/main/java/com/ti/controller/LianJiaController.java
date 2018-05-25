package com.ti.controller;

import com.alibaba.fastjson.JSONObject;
import com.ti.crawlers.LianjiaCrawler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("lianjia")
@Slf4j
public class LianJiaController {
    @Autowired
    LianjiaCrawler lianjiaCrawler;

    @RequestMapping("area")
    public String loadArea(@RequestParam(value="area",defaultValue="undefined") String area,
                           @RequestParam(value="pageSize",defaultValue="10") int page_size){
        try {
            lianjiaCrawler.loadArea("undifined",area,page_size);
            return "finished";
        } catch (Exception e) {
            log.error("something seems wrong",e);
        }
        return "finished";
    }


    @RequestMapping(value = "areaPage",produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody JSONObject page(@RequestParam(value="area",defaultValue="undefined") String area,
                                         @RequestParam(value="page",defaultValue="0") int page,
                                         @RequestParam(value="pageSize",defaultValue="10") int page_size){
        try {
            return LianjiaCrawler.page(page,area,page_size);
        } catch (Exception e) {
            log.error("something seems wrong",e);
        }
        return null;
    }

    @RequestMapping("allArea")
    public String loadAllCity(){
        try {
            lianjiaCrawler.loadCity();
            return "finished";
        } catch (Exception e) {
            log.error("something seems wrong",e);
        }
        return "finished";
    }

}
