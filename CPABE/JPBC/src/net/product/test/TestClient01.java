package net.product.test;

import java.io.File;

import cn.edu.pku.ss.crypto.abe.apiV2.Client;
import net.product.data.Message;
import net.product.data.StringArray;
import  net.product.socket.*;

public class TestClient01 {
	public static void main(String[] args) {
		SocketClient socketClient = new SocketClient("127.0.0.1", 7500);
		socketClient.connect();
		
		Client Client01 = new Client(new String[]{"PKU", "Student"});
		String PKJSONString=socketClient.receiveString();
		
		//Get public key from server
		Client01.setPK(PKJSONString);
		socketClient.sendStringArray(Client01.getAttrs());

		//Get secret key from server
		Client01.setSK(socketClient.receiveString());
		
		//encryption
		String outputFileName = "test.cpabe";
		File in = new File("README.md");
		String policy = "2 OF (Student,Teacher,PKU)";
		Client01.enc(in, policy, outputFileName);
		
		//decryption
		in = new File(outputFileName);
		Client01.dec_with_outsourcing(in);
		
		socketClient.closeConnect();
	}
}
