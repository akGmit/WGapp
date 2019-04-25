package wgapp.client.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import wgapp.client.ConnectionSocket;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConnectionSocketTests {

	private static ConnectionSocket s = new ConnectionSocket();
	private static Thread t = new Thread(s);
	@BeforeClass
	public static void setUpBeforeClass() throws InterruptedException {
		ConnectionSocket.getSocket().io().reconnection(false);
		t.start();
	}
	
	@Test
	public void testConnect() throws InterruptedException {
		ConnectionSocket.getSocket().connect();
		Thread.sleep(1000);
		assertTrue(ConnectionSocket.getSocket().connected());
	}

	@Test
	public void testDisconnect() throws InterruptedException {
		ConnectionSocket.getSocket().disconnect();
		Thread.sleep(1000);
		System.out.println(ConnectionSocket.getSocket().connected());
		assertFalse(ConnectionSocket.getSocket().connected());
	}

}
