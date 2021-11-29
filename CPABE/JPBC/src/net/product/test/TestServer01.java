package net.product.test;

import net.product.data.Message;
import net.product.data.StringArray;
import  net.product.socket.*;

public class TestServer01 {
	public static void main(String[] args) {
		SocketServer server = new SocketServer(7500);
		server.connect();
		System.out.println(server.receiveStringArray()[0]);
		server.closeConnect();
	}
}
