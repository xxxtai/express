package com.xxxtai.myToolKit;

public class ReaderWriter {
	public static byte[] hexString2Bytes(String src) {
		if (null == src || 0 == src.length()) {
			return null;
        }
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < (tmp.length / 2); i++) {
        	ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

	public static byte uniteBytes(byte src0, byte src1) { 
			byte _b0 = Byte.decode("0x" + new String(new byte[] {src0})).byteValue(); 
			_b0 = (byte) (_b0 << 4); 
			byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 })).byteValue(); 
			byte ret = (byte) (_b0 ^ _b1); 
			return ret; 
	}

	public static String bytes2HexString( byte[] b) { 
		StringBuilder buff = new StringBuilder();
		for (int i = 0; i < b.length; i++) { 
			String hex = Integer.toHexString(b[i] & 0xFF); 
			if (hex.length() == 1) { 
				hex = '0' + hex; 
			} 
			buff.append(hex.toUpperCase());
		} 
		return buff.toString();
	}
}
