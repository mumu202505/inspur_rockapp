package com.interviewer.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.interviewer.entity.GaoDeDistrict;
import com.interviewer.mapper.GaoDeDistrictMapper;
import com.interviewer.service.GaoDeDistrictService;
import com.interviewer.utils.HttpRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class GaoDeDistrictServiceImpl implements GaoDeDistrictService {
    @Autowired
    GaoDeDistrictMapper gaoDeDistrictMapper;

    @Override
    public Object getGaoDeDistrict() throws Exception {
        String s = new HttpRequestUtil().doGetRequest("https://restapi.amap.com/v3/config/district?subdistrict=3&key=02154c1b8927a3dbd8c5ca0f44ccdd6b");

        JSONObject json = JSONObject.parseObject(s);
        int count = 0;
        System.out.println(json.get("status"));
        if (json.get("status").equals("1")) {
            //删除全部原数据
            gaoDeDistrictMapper.delete(new QueryWrapper<>());
            //国家 暂时只有中国
            JSONArray districts = json.getJSONArray("districts");
            for (int i = 0; i < districts.size(); i++) {
                JSONObject district = districts.getJSONObject(i);
                //省份
                JSONArray provinces = district.getJSONArray("districts");
                for (int j = 0; j < provinces.size(); j++) {
                    JSONObject province = provinces.getJSONObject(j);
                    String provinceCenter = province.getString("center");
                    String[] provinceCenterSplit = provinceCenter.split(",");
                    //在这里新增省份 到表中
                    GaoDeDistrict provinceGaoDeDistrict = new GaoDeDistrict();
                    provinceGaoDeDistrict.setAdCode(province.getString("adcode"));
                    provinceGaoDeDistrict.setName(province.getString("name"));
                    provinceGaoDeDistrict.setLevel(province.getString("level"));
                    provinceGaoDeDistrict.setParentCode("100000");
                    provinceGaoDeDistrict.setParentName("中华人民共和国");
                    provinceGaoDeDistrict.setFullName("中国" + "-" + province.getString("name"));
                    provinceGaoDeDistrict.setLongitude(provinceCenterSplit[0]);
                    provinceGaoDeDistrict.setLatitude(provinceCenterSplit[1]);
                    provinceGaoDeDistrict.setGmtCreate(new Date());
                    gaoDeDistrictMapper.insert(provinceGaoDeDistrict);
                    //市
                    JSONArray citys = province.getJSONArray("districts");

                    //Map<String, Object> cityMap = new HashMap<String, Object>();
                    for (int k = 0; k < citys.size(); k++) {
                        JSONObject city = citys.getJSONObject(k);
                        String citysCenter = city.getString("center");
                        String[] citysCenterSplit = citysCenter.split(",");
                        //在这里新增市 到表中
                        GaoDeDistrict cityGaoDeDistrict = new GaoDeDistrict();
                        cityGaoDeDistrict.setAdCode(city.getString("adcode"));
                        cityGaoDeDistrict.setName(city.getString("name"));
                        cityGaoDeDistrict.setLevel(city.getString("level"));
                        cityGaoDeDistrict.setCityCode(city.getString("citycode"));
                        cityGaoDeDistrict.setParentCode(province.getString("adcode"));
                        cityGaoDeDistrict.setParentName(province.getString("name"));
                        cityGaoDeDistrict.setFullName(province.getString("name") + "-" + city.getString("name"));
                        cityGaoDeDistrict.setLongitude(citysCenterSplit[0]);
                        cityGaoDeDistrict.setLatitude(citysCenterSplit[1]);
                        cityGaoDeDistrict.setGmtCreate(new Date());
                        gaoDeDistrictMapper.insert(cityGaoDeDistrict);
                        //区
                        JSONArray areas = city.getJSONArray("districts");
                        Map<String, Object> countyMap = new HashMap<String, Object>();
                        for (int l = 0; l < areas.size(); l++) {
                            JSONObject area = areas.getJSONObject(l);
                            countyMap.put(area.getString("name"), area.getString("name"));
                            String areaCenter = area.getString("center");
                            String[] areaCenterSplit = areaCenter.split(",");
                            count++;
                            //在这里新增区或县 到表中
                            GaoDeDistrict areaGaoDeDistrict = new GaoDeDistrict();
                            areaGaoDeDistrict.setAdCode(area.getString("adcode"));
                            areaGaoDeDistrict.setName(area.getString("name"));
                            areaGaoDeDistrict.setLevel(area.getString("level"));
                            areaGaoDeDistrict.setCityCode(area.getString("citycode"));
                            areaGaoDeDistrict.setParentCode(city.getString("adcode"));
                            areaGaoDeDistrict.setParentName(city.getString("name"));
                            areaGaoDeDistrict.setFullName(province.getString("name") + "-" + city.getString("name") + "-" + area.getString("name"));
                            areaGaoDeDistrict.setLongitude(areaCenterSplit[0]);
                            areaGaoDeDistrict.setLatitude(areaCenterSplit[1]);
                            areaGaoDeDistrict.setGmtCreate(new Date());
                            gaoDeDistrictMapper.insert(areaGaoDeDistrict);
                        }
                    }
                }
            }
            //查看所有省市县信息
            return "成功！！";
        } else {
            return (json.get("info"));
        }
    }
}
