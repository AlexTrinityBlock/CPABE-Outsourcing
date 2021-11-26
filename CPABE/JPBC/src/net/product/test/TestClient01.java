package net.product.test;

import  net.product.socket.*;

public class TestClient01 {
	public static void main(String[] args) {
		SocketClient client = new SocketClient("127.0.0.1", 7500);
		client.connect();
		client.closeConnect();
	}
}
