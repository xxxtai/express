package com.xxxtai.express.constant;

public class Constant {
    public static final String QR_PREFIX = "q";
    public static final String SUFFIX = "e";
    public static final String CARD_PREFIX = "c";
    public static final String COMMAND_PREFIX = "m";
    public static final String ROUTE_PREFIX = "r";
    public static final String STATE_PREFIX = "s";
    public static final String HEART_PREFIX = "h";
    public static final String SPLIT = "l";
    public static final String SUB_SPLIT = "i";
    public static final String BACKWARD = "BA";
    public static final String FORWARD = "FA";
    public static final Integer FIX_LENGTH = 1;
    public static final Integer EDGE_COST_CACHE_NUM = 100;
    public static final boolean USE_SERIAL = true;

    public static String getContent(String revMessage){
        return revMessage.substring(FIX_LENGTH, revMessage.length() - FIX_LENGTH);
    }
}
