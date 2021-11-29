package net.product.test;

import java.io.File;

import cn.edu.pku.ss.crypto.abe.apiV2.Client;
import cn.edu.pku.ss.crypto.abe.apiV2.Server;

public class TestLocal {
	public static void main(String[] args) {
		Server server = new Server();
		Client Client01 = new Client(new String[]{"PKU", "Student"});
		Client Client02 = new Client(new String[]{"THU", "Student"});
		Client Client03 = new Client(new String[]{"PKU", "Teacher"});
		long Start,End,WithO,WithoutO = 0;
		//client get the public string from server
		String PKJSONString = server.getPublicKeyInString();
		Client01.setPK(PKJSONString);
		Client02.setPK(PKJSONString);
		Client03.setPK(PKJSONString);

		//client sends its attributes message to server and get private string 
		String SKJSONString = server.generateSecretKey(Client01.getAttrs());
		Client01.setSK(SKJSONString);
		
		SKJSONString = server.generateSecretKey(Client02.getAttrs());
		Client02.setSK(SKJSONString);
		
		SKJSONString = server.generateSecretKey(Client03.getAttrs());
		Client03.setSK(SKJSONString);
		
		//encryption
		String outputFileName = "test.cpabe";
		File in = new File("README.md");
		String policy = "2 OF (Student,Teacher,PKU)";
		Client01.enc(in, policy, outputFileName);
		
		//decryption
		in = new File(outputFileName);
//		Client02.dec(in);
		Start = System.currentTimeMillis();
		Client03.dec(in);
		End = System.currentTimeMillis();
		WithoutO = End - Start;
		
		Start = System.currentTimeMillis();
		Client03.dec_with_outsourcing(in);
		End = System.currentTimeMillis();
		WithO = End - Start;
		
		System.out.print("This decryption without outsourcing took ");
		System.out.print(WithoutO);
		System.out.println(" ms.");
		
		System.out.print("This decryption with outsourcing took ");
		System.out.print(WithO);
		System.out.println(" ms.");
		Client03.PrintOutsourcingTime();
	}
}
