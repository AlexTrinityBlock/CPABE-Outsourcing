package net.product.test;

import cn.edu.pku.ss.crypto.abe.apiV2.Server;
import net.product.data.Message;
import net.product.data.StringArray;
import  net.product.socket.*;

public class TestServer01 {
	public static void main(String[] args) {
		SocketServer socketServer = new SocketServer(7500);
		socketServer.connect();
		
		Server server = new Server();
		
		//Send Public key
		String PKJSONString = server.getPublicKeyInString();
		socketServer.sendString(PKJSONString);
		System.out.println("Server send public key");
		System.out.println(PKJSONString);
		
		//Get attribute  and generate secret key
		String SKJSONString =server.generateSecretKey(socketServer.receiveStringArray());
		socketServer.sendString(SKJSONString);
		System.out.println("Server send secret key");
		System.out.println(SKJSONString);
		
		//
		socketServer.closeConnect();
	}
}
