package com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps;

import static org.junit.Assert.assertEquals;

import org.junit.*;

import com.unimelb.comp90015.fourLiterGroup.ezshare.utils.utils;

public class ResourceWarehouseUnitTest {
	  //Any method annotated with "@Before" will be executed before each test,
	  //allowing the tester to set up some shared resources.
	  @Before public void setUp()
	  {

	  }

	  //Any method annotated with "@After" will be executed after each test,
	  //allowing the tester to release any shared resources used in the setup.
	  @After public void tearDown()
	  {
	  }
	
	  
	  
	  @Test  public void addResourceWithDiffOwner()
	  {
		  Resource resource1 = new Resource();
		  resource1.setChannel("channel");
		  resource1.setURI("http://www.bilibili.com");
		  resource1.setOwner("STB");
		  
		  Resource resource2 = new Resource();
		  resource2.setChannel("channel");
		  resource2.setURI("http://www.bilibili.com");
		  resource2.setOwner("STB2");
		  
		  
		  ResourceWarehouse warehouse = new ResourceWarehouse();
		  warehouse.AddResource(resource1);
		  assertEquals(false, warehouse.AddResource(resource2));
		  assertEquals(1, warehouse.getSizeOfWarehourse());
	  }
	  
	  @Test public void stringUtilTest(){
		  assertEquals("", utils.trimFirstAndLastChar("", "a"));
		  assertEquals("", utils.trimFirstAndLastChar("", " "));
		  assertEquals("", utils.trimFirstAndLastChar("   ", " "));
		  assertEquals("", utils.trimFirstAndLastChar("    ", " "));
		  assertEquals("1", utils.trimFirstAndLastChar("  1 ", " "));
	  }
	
	  @Test public void printResourceTest(){
		  Resource resource1 = new Resource();
		  resource1.setChannel("channel");
		  resource1.setURI("http://www.bilibili.com");
		  resource1.setOwner("STB");
		  resource1.setName("ezsare_system1");
		  
		  Resource resource2 = new Resource();
		  resource2.setChannel("channel");
		  resource2.setURI("http://www.bilibili.com");
		  resource2.setOwner("STB2");
		  resource2.setName("ezsare_system2");
		  
		  Resource resource3 = new Resource();
		  resource3.setChannel("privat_channel");
		  resource3.setURI("http://www.bilibili.com");
		  
		  Resource resource4 = new Resource();
		  resource4.setChannel("channel");
		  resource4.setURI("http://www.bilibili.com");
		  resource4.setOwner("STB");
		  resource4.setName("ezsare_system4");
		  
		  ResourceWarehouse warehouse = new ResourceWarehouse();
		  warehouse.AddResource(resource1);
		  assertEquals(false, warehouse.AddResource(resource2));
		  warehouse.AddResource(resource3);
		  warehouse.AddResource(resource4);

		  assertEquals(2, warehouse.getSizeOfWarehourse());
		  
		  warehouse.printResourceMap();
		  
		  Resource resource = new Resource();
		  resource.setChannel("channel");
		  resource.setURI("http://www.bilibili.com");
		  resource.setOwner("STB");
		  
		  warehouse.RemoveResource(resource);
		  System.out.println("Remove Function");
		  warehouse.printResourceMap();
		  
	  }
}
