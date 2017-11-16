package com.xxxtai.express.constant;

public class Constant {
    public static final String QR_PREFIX = "q";
    public static final String SUFFIX = "w";
    public static final String CARD_PREFIX = "r";
    public static final String COMMAND_PREFIX = "t";
    public static final String ROUTE_PREFIX = "y";
    public static final String STATE_PREFIX = "u";
    public static final String HEART_PREFIX = "i";
    public static final String SPLIT = "o";
    public static final String SUB_SPLIT = "p";
    public static final String BACKWARD = "BA";
    public static final String FORWARD = "FA";
    public static final Integer FIX_LENGTH = 1;
    public static final Integer EDGE_COST_CACHE_NUM = 100;
    public static final boolean USE_SERIAL = false;

    public static String getContent(String revMessage){
        return revMessage.substring(FIX_LENGTH, revMessage.length() - FIX_LENGTH);
    }
}
