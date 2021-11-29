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
		System.out.println("CLient receive public key");
		System.out.println(PKJSONString);
		
		//Get public key from server
		Client01.setPK(PKJSONString);
		socketClient.sendStringArray(Client01.getAttrs());
		System.out.println("CLient send attribute");
		
		//Get secret key from server
		String srcretKey=socketClient.receiveString();
		Client01.setSK(srcretKey);
		System.out.println("CLient receive srcretKey");
		
		//encryption
		String outputFileName = "test.cpabe";
		File in = new File("README.md");
		String policy = "2 OF (Student,Teacher,PKU)";
		Client01.enc(in, policy, outputFileName);
		
		//decryption
		in = new File(outputFileName);
		System.out.println("CLient is decrypting");
		Client01.dec_with_outsourcing(in);
		System.out.println("CLient decryption process is finished ");
		
		socketClient.closeConnect();
	}
}
