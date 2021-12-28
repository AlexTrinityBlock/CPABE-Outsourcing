package net.product.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.ss.crypto.abe.apiV2.Client;
import net.product.data.Message;
import net.product.data.StringArray;
import  net.product.socket.*;

public class TestClient02 {
	public static void main(String[] args) {
		SocketClient socketClient = new SocketClient("127.0.0.1", 7500);
		socketClient.connect();
		
		//Attr Test
		int attrNumber=10;
		String encryptPolicy=Integer.toString(2+attrNumber)+" OF (Student,Teacher,PKU";
		List<String> userAttrArrayList = new ArrayList<String>();
		userAttrArrayList.add("PKU");
		userAttrArrayList.add("Student");
		for(int i=0;i<attrNumber;i++) {
			userAttrArrayList.add("Policy"+Integer.toString(i));
			encryptPolicy+=",Policy"+Integer.toString(i);
		}
		encryptPolicy+=")";
		System.out.println(userAttrArrayList);
		System.out.println(encryptPolicy);
		String[] userAttrArray = new String[ userAttrArrayList.size() ];
		userAttrArrayList.toArray( userAttrArray);
		
		Client Client01 = new Client(userAttrArray);
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
		socketClient.closeConnect();
		
		//encryption
		String outputFileName = "test.cpabe";
		File in = new File("README.md");
		Client01.enc(in, encryptPolicy, outputFileName);
		
		//decryption
		long Start,End,WithO,WithoutO = 0;
		in = new File(outputFileName);
		System.out.println("CLient is decrypting without outsourcing");
		Start = System.currentTimeMillis();
		Client01.dec(in);
		End = System.currentTimeMillis();
		System.out.println("CLient decryption with outsourcing: "+String.valueOf(End-Start)+"ms");
		System.out.println("CLient decryption process is finished ");
		//decryption without		
		System.out.println("CLient is decrypting with outsourcing");
		Start = System.currentTimeMillis();
		Client01.dec_with_outsourcing(in);
		End = System.currentTimeMillis();
		System.out.println("CLient decryption with outsourcing: "+String.valueOf(End-Start)+"ms");
		System.out.println("CLient decryption process is finished ");		
	}
}
