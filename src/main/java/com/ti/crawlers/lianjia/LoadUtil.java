package com.ti.crawlers.lianjia;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class LoadUtil {


    public static List<JSONObject> loadAreas(){
        List<JSONObject> mergedAreaList = Lists.newArrayList();
        try {
            String text = new String(Files.readAllBytes(Paths.get("area.json")), StandardCharsets.UTF_8);
            JSONObject jsonObject = JSON.parseObject(text);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONObject check_filters = data.getJSONObject("check_filters");
            JSONObject region = check_filters.getJSONObject("region");
            JSONArray options = region.getJSONArray("options");
            JSONObject areasObject = options.getJSONObject(0);
            JSONArray areasOptions = areasObject.getJSONArray("options");
            int count = 0;
            for(int i = 0; i < areasOptions.size() ; i++){
                JSONObject areaObject = areasOptions.getJSONObject(i);
                JSONArray subAreaOptions = areaObject.getJSONArray("options");
                for(int sub_index = 0; sub_index < subAreaOptions.size() ; sub_index ++){
                    JSONObject subAreaObject = subAreaOptions.getJSONObject(sub_index);
                    JSONObject areaMergeObject = new JSONObject();
                    areaMergeObject.put("area_name",areaObject.getString("name"));
                    areaMergeObject.put("sub_area_name",subAreaObject.getString("name"));
                    areaMergeObject.put("sub_area_key",subAreaObject.getString("key"));
                    log.info("{},area is {},sub area is {},key is {}",
                            count ++,
                            areaObject.getString("name"),
                            subAreaObject.getString("name"),
                            subAreaObject.getString("key"));
                    mergedAreaList.add(areaMergeObject);
//                    log.info(areaObject.toJSONString());
                }
            }
            log.info(jsonObject.toJSONString());
        }catch (Exception e){

        }
        return mergedAreaList;
    }

    public static void main(String[] args) {
        loadAreas();
    }
}
