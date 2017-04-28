package com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps;

import static org.junit.Assert.*;

import com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps.OperationRunningException;

import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServerOpHandlerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	@Test(expected=com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps.OperationRunningException.class)
	public void publishTest1() throws OperationRunningException {
		System.out.println("publishTest1");
		//file scheme
		JSONObject jsonObject1=new JSONObject();
		JSONObject jsonObject2=new JSONObject();
		jsonObject2.put("channel","");
		jsonObject2.put("owner","");
		jsonObject2.put("uri","file://www.bilibili.com");
		jsonObject2.put("name", "ezshare_system");

		jsonObject1.put("resource",jsonObject2);
		try{
			ServerOperationHandler.publish(jsonObject1);
		} catch (OperationRunningException e){
			System.out.println(e.toString());
		}
	}

	@Test(expected=com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps.OperationRunningException.class)
	public void publishTest2() throws Exception{
		System.out.println("publishTest2");
		//null resource
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		jsonObject2.put("name", "ezshare_system");
		jsonObject1.put("resource", jsonObject2);
		try{
			ServerOperationHandler.publish(jsonObject1);
		} catch (OperationRunningException e){
			System.out.println(e.toString());
		}
	}
	
	@Test(expected=com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps.OperationRunningException.class)
	public void publishTest3() throws Exception{
		System.out.println("publishTest3");
		//absolute
		JSONObject jsonObject1=new JSONObject();
		JSONObject jsonObject2=new JSONObject();
		jsonObject2.put("channel","");
		jsonObject2.put("owner","");
		jsonObject2.put("uri","http://www.bilibili.com");
		//jsonObject2.put("name", "ezshare_system");
		jsonObject1.put("resource",jsonObject2);
		try{
			ServerOperationHandler.publish(jsonObject1);
		} catch (OperationRunningException e){
			System.out.println(e.toString());
		}
	}
	@Test(expected=com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps.OperationRunningException.class)
	public void publishTest4() throws Exception{
		System.out.println("publishTest4");
		//owner with *
		JSONObject jsonObject1=new JSONObject();
		JSONObject jsonObject2=new JSONObject();
		
		jsonObject2.put("owner","*");
		jsonObject2.put("url","E:\\Melbourne\\Study\\2017 Semester1");
		jsonObject2.put("name", "ezshare_system");
		jsonObject1.put("resource",jsonObject2);
		try{
			ServerOperationHandler.publish(jsonObject1);
		} catch (OperationRunningException e){
			System.out.println(e.toString());
		}
	}
	
	@Test(expected=com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps.OperationRunningException.class)
	public void publishTest5() throws Exception{
		System.out.println("publishTest5");
		//url==null
		JSONObject jsonObject1=new JSONObject();
		JSONObject jsonObject2=new JSONObject();
		
		jsonObject2.put("name", "ezshare_system");
		jsonObject1.put("resource",jsonObject2);
		
		try{
			ServerOperationHandler.publish(jsonObject1);
		} catch (OperationRunningException e){
			System.out.println(e.toString());
		}
	}
	
	@Test(expected=com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps.OperationRunningException.class)
	public void publishTest6() throws Exception{
		System.out.println("publishTest6");
		//url is ""
		JSONObject jsonObject1=new JSONObject();
		JSONObject jsonObject2=new JSONObject();
		jsonObject2.put("url","");
		jsonObject2.put("name", "ezshare_system");
		jsonObject1.put("resource",jsonObject2);
		try{
			ServerOperationHandler.publish(jsonObject1);
		} catch (OperationRunningException e){
			System.out.println(e.toString());
		}
	}
	
	/*@Test(expected=com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps.OperationRunningException.class)
	public void shareTest1() throws Exception{
		//share uri file scheme
		JSONObject jsonObject1=new JSONObject();
		JSONObject jsonObject2=new JSONObject();
		jsonObject2.put("secret","2Vy567");
		jsonObject2.put("url","file://www.bilibili.com");
		jsonObject1.put("resource",jsonObject2);
		ServerOperationHandler.share(jsonObject1);
	}*/
	
	@Test
	public void removeTest() throws Exception{
		System.out.println("remove test");
		JSONObject jsonObject1=new JSONObject();
		JSONObject jsonObject2=new JSONObject();
		
		jsonObject2.put("url","http://www.bilibili.com");
		jsonObject1.put("resource",jsonObject2);
		
		try{
			ServerOperationHandler.publish(jsonObject1);
		} catch (OperationRunningException e){
			System.out.println(e.toString());
		}
		
		JSONObject jsonObject3=new JSONObject();
		JSONObject jsonObject4=new JSONObject();
		jsonObject3.put("url","http://www.bilibili.com");
		jsonObject4.put("resource",jsonObject3);
		
		ServerOperationHandler.remove(jsonObject4);
	}
}
