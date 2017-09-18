package com.xxxtai.express.constant;

public class Constant {
    public static final String QR_PREFIX = "2A";//*
    public static final String SUFFIX = "26";//&
    public static final String CARD_PREFIX = "24";//$
    public static final String COMMAND_PREFIX = "23";//#
    public static final String ROUTE_PREFIX = "40";//@
    public static final String STATE_PREFIX = "25";//%
    public static final String HEART_PREFIX = "7E";//!
    public static final String SPLIT = "2F";///
    public static final String SUB_SPLIT = "2C";//,
    private static final Integer FIX_LENGTH = 2;

    public static String getContent(String revMessage){
        return revMessage.substring(FIX_LENGTH, revMessage.length() - FIX_LENGTH);
    }
}
