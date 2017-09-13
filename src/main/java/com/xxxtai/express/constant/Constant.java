package com.xxxtai.express.constant;

public class Constant {
    public static final String QR_SUFFIX = "*";
    public static final String PREFIX = "&";
    public static final String CARD_SUFFIX = "$";
    public static final String COMMAND_SUFFIX = "#";
    public static final String ROUTE_SUFFIX = "@";
    public static final String STATE_SUFFIX = "%";
    public static final String HEART_SUFFIX = "!";
    public static final String SPLIT = "/";
    public static final String SUB_SPLIT = ",";
    private static final Integer FIX_LENGTH = 1;

    public static String getContent(String revMessage){
        return revMessage.substring(FIX_LENGTH, revMessage.length() - FIX_LENGTH);
    }
}
