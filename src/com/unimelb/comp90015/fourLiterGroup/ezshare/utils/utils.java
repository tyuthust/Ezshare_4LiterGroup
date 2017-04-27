package com.unimelb.comp90015.fourLiterGroup.ezshare.utils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class utils {

	public static String trimFirstAndLastChar(String source, String element) {
		boolean beginIndexFlag = true;
		boolean endIndexFlag = true;
		if (null != source && null != element){
			do {
				if(-1 == source.indexOf(element)){
					return source;
				}
				boolean begin = (source.indexOf(element) == 0);
				if(begin){
					source = source.replaceFirst(element, "");
				}
				if(-1 == source.indexOf(element)){
					return source;
				}
				boolean end = (source.lastIndexOf(element) == source.length() -1);
				if(end){
					source = replaceLast(source, element, "");
				}
				endIndexFlag = (source.lastIndexOf(element) + 1 == source.length());
			} while (beginIndexFlag || endIndexFlag);
		}
		return source;
	}
	
    private static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)"+regex+"(?!.*?"+regex+")", replacement);
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
	
	public static boolean isIPandPort(String string){//TODO: judge the string of IP:Port is legal
		boolean Str = false;
		String[] IPandPort = string.split(":");
		if(isAddress(IPandPort[0]) && isPort(IPandPort[1])){
			Str = true;
		}

		return Str;
	}
	private static boolean isPort(String port){
		int Port=0;
		try{
			Port = Integer.parseInt(port);
		} catch (NumberFormatException e) {
		    e.printStackTrace();
		}
		if(Port>=0 && Port<=65535){
			return true;
		}else{
			return false;
		}
	}
	
	private static boolean isAddress(String addr){
		if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))  { 
			return false;
        }  
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";  
        Pattern pat = Pattern.compile(rexp);    
        Matcher mat = pat.matcher(addr);    
        boolean ipAddress = mat.find();  

        return ipAddress;  
    }  
}
