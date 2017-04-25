package com.unimelb.comp90015.fourLiterGroup.ezshare.utils;

import java.util.Random;


public class utils {

    public static String trimFirstAndLastChar(String source,String element){  
        boolean beginIndexFlag = true;  
        boolean endIndexFlag = true;  
        do{  
            int beginIndex = source.indexOf(element) == 0 ? 1 : 0;  
            int endIndex = source.lastIndexOf(element) + 1 == source.length() ? source.lastIndexOf(element) : source.length();  
            source = source.substring(beginIndex, endIndex);  
            beginIndexFlag = (source.indexOf(element) == 0);  
            endIndexFlag = (source.lastIndexOf(element) + 1 == source.length());  
        } while (beginIndexFlag || endIndexFlag);  
        return source;  
    } 
    
    public static String RandomString(int length) {  
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  
        Random random = new Random();  
        StringBuffer buf = new StringBuffer();  
        for (int i = 0; i < length; i++) {  
            int num = random.nextInt(62);  
            buf.append(str.charAt(num));  
        }  
        return buf.toString();  
    } 
}
