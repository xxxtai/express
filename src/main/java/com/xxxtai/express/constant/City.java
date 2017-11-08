package com.xxxtai.express.constant;

import lombok.Getter;
import lombok.Setter;

public enum City {
    GUANGZHOU("广州", 510000L),
    SHENZHEN("深圳", 518000L),
    BEIJING("北京", 100000L),
    SHANGHAI("上海", 200000L),
    CHONGQING("重庆", 404100),
    TIANJIN("天津", 300000),
    HANGZHOU("杭州",310000),
    NANJING("南京", 210000),
    JINAN("济南",250000),
    QINGDAO("青岛", 266000),
    DALIAN("大连", 116000),
    NINGBO("宁波", 315000),
    XIAMEN("厦门", 361000),
    CHENGDU("成都", 610000),
    WUHAN("武汉", 430000),
    HAERBIN("哈尔滨", 150000),
    SHENYANG("沈阳", 110000),
    XIAN("西安", 710000),
    CHANGCHUN("长春", 130000),
    CHANGSHA("长沙", 410000),
    FUZHOU("福州", 350000),
    ZHENGZHOU("郑州", 450000),
    SUZHOU("苏州", 215000),
    FOSHAN("佛山", 528000),
    DONGGUAN("东莞", 523000),
    WUXI("无锡", 214000),
    YANTAI("烟台", 261400),
//    TAIYUAN("太原", 030000),
    NANCHANG("南昌", 330000),
    NULL("城市不存在", 0);

    private @Setter @Getter
    String name;
    private @Setter @Getter
    long code;
    City(String name, long code){
        this.name = name;
        this.code = code;
    }

    public static City valueOfName(String name){
        for (City city : City.values()) {
            if (city.getName().equals(name)) {
                return city;
            }
        }
        return NULL;
    }

    public static City valueOfCode(Long code){
        for (City city : City.values()) {
            if (city.getCode() == code) {
                return city;
            }
        }
        return NULL;
    }

}
