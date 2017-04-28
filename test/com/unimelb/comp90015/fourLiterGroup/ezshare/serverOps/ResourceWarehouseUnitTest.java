package com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps;

import static org.junit.Assert.assertEquals;

import org.junit.*;

import com.unimelb.comp90015.fourLiterGroup.ezshare.utils.utils;

public class ResourceWarehouseUnitTest {
	// Any method annotated with "@Before" will be executed before each test,
	// allowing the tester to set up some shared resources.
	@Before
	public void setUp() {

	}

	// Any method annotated with "@After" will be executed after each test,
	// allowing the tester to release any shared resources used in the setup.
	@After
	public void tearDown() {
	}

	@Test
	public void addResourceWithDiffOwner() {
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

	@Test
	public void stringUtilTest() {
		assertEquals("", utils.trimFirstAndLastChar("", "a"));
		assertEquals("", utils.trimFirstAndLastChar("", " "));
		assertEquals("", utils.trimFirstAndLastChar("   ", " "));
		assertEquals("", utils.trimFirstAndLastChar("    ", " "));
		assertEquals("1", utils.trimFirstAndLastChar("  1 ", " "));
	}

	@Test
	public void addResourceWithSameOwner() {
		Resource resource1 = new Resource();
		resource1.setChannel("channel");
		resource1.setURI("http://www.bilibili.com");
		resource1.setOwner("STB");
		resource1.setDescription("This is STB");

		Resource resource2 = new Resource();
		resource2.setChannel("channel");
		resource2.setURI("http://www.bilibili.com");
		resource2.setOwner("STB");
		resource2.setDescription("This is not STB");

		ResourceWarehouse warehouse = new ResourceWarehouse();
		warehouse.AddResource(resource1);
		warehouse.AddResource(resource2);

		assertEquals("This is not STB", resource2.getDescription());
		assertEquals(1, warehouse.getSizeOfWarehourse());
	}

	@Test
	public void testRemoveResource() throws OperationRunningException {
		Resource resource1 = new Resource();
		resource1.setChannel("channel");
		resource1.setURI("http://www.bilibili.com");
		resource1.setOwner("STB");

		ResourceWarehouse warehouse = new ResourceWarehouse();
		warehouse.AddResource(resource1);
		warehouse.RemoveResource(resource1);
		assertEquals(0, warehouse.getSizeOfWarehourse());
	}

	@Test
	public void testFindResource() {
		// test find
		Resource resource1 = new Resource();
		resource1.setChannel("channel");
		resource1.setURI("http://www.bilibili.com");
		resource1.setOwner("STB");

		ResourceWarehouse warehouse = new ResourceWarehouse();
		warehouse.AddResource(resource1);
		assertEquals(resource1, warehouse.FindResource("channel", "http://www.bilibili.com", "STB"));

	}

	@Test
	public void testFindResource1() throws OperationRunningException {
		// test exist
		Resource resource1 = new Resource();
		resource1.setChannel("channel");
		resource1.setURI("http://www.bilibili.com");
		resource1.setOwner("STB");

		ResourceWarehouse warehouse = new ResourceWarehouse();
		warehouse.AddResource(resource1);
		assertEquals(true, warehouse.FindResource("channel", "http://www.bilibili.com"));

	}

	@Test
	public void printResourceTest() {
		Resource resource1 = new Resource();
		resource1.setURI("http://www.bilibili.com");
		resource1.setName("ezsare_system1");

		Resource resource2 = new Resource();
		resource2.setURI("http://www.bilibili.com");
		resource2.setOwner("STB2");
		resource2.setName("ezsare_system2");

		Resource resource3 = new Resource();
		resource3.setChannel("privat_channel");
		resource3.setURI("http://www.bilibili.com");

		Resource resource4 = new Resource();
		resource4.setURI("http://www.bilibili.com");
		resource4.setName("ezsare_system4");

		ResourceWarehouse warehouse = new ResourceWarehouse();
		warehouse.AddResource(resource1);
		assertEquals(false, warehouse.AddResource(resource2));
		warehouse.AddResource(resource3);
		warehouse.AddResource(resource4);

		assertEquals(2, warehouse.getSizeOfWarehourse());

		warehouse.printResourceMap();

		Resource resource = new Resource();
		resource.setURI("http://www.bilibili.com");

		warehouse.RemoveResource(resource);
		System.out.println("Remove Function");
		warehouse.printResourceMap();
	}
}
