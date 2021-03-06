package cn.edu.pku.ss.crypto.abe.apiV2;

import java.io.File;

public class Example {
	public static void main(String[] args) {
		Server server = new Server();
		Client PKUClient = new Client(new String[]{"PKU", "Student"});
		Client THUClient = new Client(new String[]{"THU", "Student"});
		Client TeacherClient = new Client(new String[]{"PKU", "Teacher"});
		long Start,End,WithO,WithoutO = 0;
		//client get the public string from server
		String PKJSONString = server.getPublicKeyInString();
		PKUClient.setPK(PKJSONString);
		THUClient.setPK(PKJSONString);
		TeacherClient.setPK(PKJSONString);

		//client sends its attributes message to server and get private string 
		String SKJSONString = server.generateSecretKey(PKUClient.getAttrs());
		PKUClient.setSK(SKJSONString);
		
		SKJSONString = server.generateSecretKey(THUClient.getAttrs());
		THUClient.setSK(SKJSONString);
		
		SKJSONString = server.generateSecretKey(TeacherClient.getAttrs());
		TeacherClient.setSK(SKJSONString);
		
		//encryption
		String outputFileName = "test.cpabe";
		File in = new File("README.md");
		String policy = "2 OF (Student,Teacher,PKU)";
		PKUClient.enc(in, policy, outputFileName);
		
		//decryption
		in = new File(outputFileName);
//		THUClient.dec(in);
		Start = System.currentTimeMillis();
		TeacherClient.dec(in);
		End = System.currentTimeMillis();
		WithoutO = End - Start;
		
		Start = System.currentTimeMillis();
		TeacherClient.dec_with_outsourcing(in);
		End = System.currentTimeMillis();
		WithO = End - Start;
		
		System.out.print("This decryption without outsourcing took ");
		System.out.print(WithoutO);
		System.out.println(" ms.");
		
		System.out.print("This decryption with outsourcing took ");
		System.out.print(WithO);
		System.out.println(" ms.");
		TeacherClient.PrintOutsourcingTime();
	}
}
