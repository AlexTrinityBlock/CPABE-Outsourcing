package net.product.socket;

import java.io.*;
import java.net.*;
import net.product.data.Message;
import net.product.data.StringArray;

public class SocketClient extends Thread
{
    private Socket m_socket;
    private BufferedOutputStream clientOutPut;
    private BufferedReader serverInput;
    private String ip;
    private int port;
    
    public SocketClient(String ip, int port)
    {
    	this.ip = ip;
    	this.port =port;
    }
    
    //Connect
    public void connect() {
        try
        {
        	InetSocketAddress isa = new InetSocketAddress(this.ip,this.port);
            this.m_socket = new Socket();
            this.m_socket.connect(isa,100000);
            this.m_socket.setSendBufferSize(1024*1024*64);
            this.m_socket.setReceiveBufferSize(1024*1024*64);;
            this.clientOutPut= new BufferedOutputStream(this.m_socket.getOutputStream());
            this.serverInput = new BufferedReader(new InputStreamReader(this.m_socket.getInputStream()));
        }
        catch (IOException e)
        {
            System.out.println(e.toString());
        }
    }
    
    //closeConnect
    public void closeConnect() {
        try
        {
        	this.m_socket.close();
        	System.out.println("Connect Close");
        }
        catch (IOException e)
        {
            System.out.println(e.toString());
        }
    }
    
    //send  message to server
    public void sendMessage(Message message) {
    	try {
    		ObjectOutputStream ObjectOutput = new ObjectOutputStream(this.m_socket.getOutputStream());
    		ObjectOutput.writeObject(message);
    	}catch(Exception e)
    	{
    		System.out.println("Fail to Send message to server");
    		System.out.println(e.toString()); 
    	}
    }
    
    //receive  message 
    public Message receiveMessage() {
    	try {
    		ObjectInputStream ObjectInput = new ObjectInputStream(this.m_socket.getInputStream());
    		Message message = (Message) ObjectInput.readObject();
    		return message;
    	}catch(Exception e)
    	{
    		System.out.println("Fail to receive message");
    		System.out.println(e.toString()); 
    	}
    	return null;
    }
    
    //send string array to Client
    public void sendStringArray(String[] strArr) {
    	try {
    		ObjectOutputStream ObjectOutput = new ObjectOutputStream(this.m_socket.getOutputStream());
    		StringArray stringArray = new StringArray();
    		stringArray.stringArray = strArr;
    		ObjectOutput.writeObject(stringArray);
    	}catch(Exception e)
    	{
    		System.out.println("Fail to Send message to Client");
    		System.out.println(e.toString()); 
    	}
    }
   
    //receive  string array 
    public String[] receiveStringArray() {
    	try {
    		ObjectInputStream ObjectInput = new ObjectInputStream(this.m_socket.getInputStream());
    		StringArray stringArray = (StringArray) ObjectInput.readObject();
    		return stringArray.stringArray;
    	}catch(Exception e)
    	{
    		System.out.println("Fail to receive message");
    		System.out.println(e.toString()); 
    	}
    	return null;
    }
    
    //send string 
    public void sendString(String str) {
    	try {
    		String [] strArr= {str};
    		this.sendStringArray(strArr);
    	}catch(Exception e)
    	{
    		System.out.println("Fail to Send message to Client");
    		System.out.println(e.toString()); 
    	}
    }
    
    //receive  string  
    public String receiveString() {
    	try {
    		String[] strArr =this.receiveStringArray();
    		String str =strArr[0];
    		return str;
    	}catch(Exception e)
    	{
    		System.out.println("Fail to receive message");
    		System.out.println(e.toString()); 
    	}
    	return null;
    }
}
