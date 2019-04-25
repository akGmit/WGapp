package wgapp.client.tests;



import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import wgapp.client.ConnectionOutput;
import wgapp.client.ConnectionSocket;
import wgapp.client.User;
import wgapp.inter.Observer;

public class ConnectionOutputTests implements Observer{
	
	private static ConnectionSocket s = new ConnectionSocket();
	private static Thread t;
	private User user = User.getUser();
	private ConnectionOutput c;
	private String actual;
	private Object o;
	
	@BeforeClass
	public static void initialize() {
		ConnectionSocket.connect();
		
		t = new Thread(s);
		t.start();
	}
	
	@Before
	public void initOutput() {
		c = new ConnectionOutput();
		user.setUser("1", "Group 1", false, "1", "");
		s.addObserver(this);
	}
	
	@Test
	public void testLogIn() throws InterruptedException {
		
		c.logIn(user);
		Thread.sleep(1000);
		
		assertTrue((boolean)o);
		assertEquals("log_in", actual);
	}

	@Test
	public void testCreateNewUser() throws InterruptedException {
		c.createNewUser(user);
		Thread.sleep(1000);
		
		assertEquals("error_msg", actual);
	}

	@Test
	public void testCreateGroup() throws InterruptedException {
		c.createGroup(user);
		Thread.sleep(1000);
		String expected = "\"Group name exists!\"";
		String res = (String)o;
		assertEquals("Create group failed", expected , res);
	}

	@Test
	public void testJoinGroup() throws InterruptedException {
		user.setPassword("wrong");
		c.joinGroup(user);
		Thread.sleep(1000);
		String expected = "\"Wrong work group password!\"";
		String res = (String)o;
		
		assertEquals("Join group failed", expected , res);
	}

	@Test
	public void testSendMessage() throws InterruptedException {
		user.setPassword("1");
		c.joinGroup(user);
		c.sendMessage("testing");
		
		Thread.sleep(1000);
		
		assertEquals("message", actual);
	}

	@Test
	public void testGetWorkGroupList() throws InterruptedException {
		c.getWorkGroupList();
		
		Thread.sleep(1000);
		
		assertEquals("workgroup_list", actual);
	}

	@Test
	public void testLeaveGroup() throws InterruptedException {
		c.joinGroup(user);
		c.leaveGroup();
		
		Thread.sleep(1000);
		
		assertEquals("message", actual);
	}

	@Override
	public void update(String event, Object obj) {
		actual = event;
		o = obj;
	}

}
