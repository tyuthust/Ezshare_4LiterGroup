package com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps;

import static org.junit.Assert.*;

import org.junit.Test;
import com.unimelb.comp90015.fourLiterGroup.ezshare.utils.utils;;

public class UtilsTest {

	@Test
	public void test() {
		String port = "0";
		assertEquals(utils.isPort(port),true);
	}

	@Test
	public void test1() {
		String port = "881";
		assertEquals(utils.isPort(port),true);
	}
	
	@Test
	public void test2() {
		String port = "-1";
		assertEquals(utils.isPort(port),false);
	}
	
	@Test
	public void test3() {
		String port = "65535";
		assertEquals(utils.isPort(port),true);
	}
	
	@Test
	public void test4() {
		String port = "65536";
		assertEquals(utils.isPort(port),false);
	}
	
	@Test
	public void test5() {
		String IP = "127.0.0.1";
		assertEquals(utils.isAddress(IP),true);
	}

	@Test
	public void test6() {
		String IP = "256.0.0.1";
		assertEquals(utils.isAddress(IP),false);
	}
	
	@Test
	public void test7() {
		String IP = "-1.0.0.1";
		assertEquals(utils.isAddress(IP),false);
	}
	
	@Test
	public void test8() {
		String IP = "251.0.0";
		assertEquals(utils.isAddress(IP),false);
	}
	
	@Test
	public void test9() {
		String IP = "256.0.0.1.1";
		assertEquals(utils.isAddress(IP),false);
	}
	
	@Test
	public void test10() {
		String IP = "http:112312";
		assertEquals(utils.isAddress(IP),false);
	}
	
	@Test
	public void test11() {
		String IP = "";
		assertEquals(utils.isAddress(IP),false);
	}
	
	@Test
	public void test12() {
		String IP = "aaa.bbb.ccc.ddd";
		assertEquals(utils.isAddress(IP),false);
	}
	
	@Test
	public void test13() {
		String IP = "266.0.0.0";
		assertEquals(utils.isAddress(IP),false);
	}
}
