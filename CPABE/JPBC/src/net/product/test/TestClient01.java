package net.product.test;

import net.product.data.Message;
import net.product.data.StringArray;
import  net.product.socket.*;

public class TestClient01 {
	public static void main(String[] args) {
		String [] strArray = {"Hey","Hello"};
		SocketClient client = new SocketClient("127.0.0.1", 7500);
		client.connect();
		client.sendStringArray(strArray);;
		client.closeConnect();
	}
}
