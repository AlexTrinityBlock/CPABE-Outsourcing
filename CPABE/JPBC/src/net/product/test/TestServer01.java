package net.product.test;

import  net.product.socket.*;

public class TestServer01 {
	public static void main(String[] args) {
		SocketServer server = new SocketServer(7500);
		server.connect();
		server.closeConnect();
	}
}
