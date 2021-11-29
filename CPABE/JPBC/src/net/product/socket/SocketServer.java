package net.product.socket;

import java.io.*;
import java.net.*;
import net.product.data.Message;
import net.product.data.StringArray;

public class SocketServer extends Thread
{
    private ServerSocket m_serverSocket;
    private Socket m_socket;
    private BufferedReader clientInput;
    private OutputStream serverOutput;
    private int port;
    
    //Init Server Socket
    public SocketServer(int port)
    {
    	this.port =port;
    }
    
    //Connect
    public void connect() {
    	while(true) {
	        try
	        {
	        	if(this.m_serverSocket==null)this.m_serverSocket = new ServerSocket(this.port);
	        	this.m_serverSocket.setReceiveBufferSize(1024*1024*64);
	            System.out.println("Waiting for client...");
	            if(this.m_socket==null)this.m_socket = this.m_serverSocket.accept();
	            this.m_socket.setSendBufferSize(1024*1024*64);
	            this.m_socket.setReceiveBufferSize(1024*1024*64);
	            System.out.println("Find client!");
	            this.clientInput=new BufferedReader(new InputStreamReader(this.m_socket.getInputStream()));
	            this.serverOutput=this.m_socket.getOutputStream();
	            break;
	        }
	        catch(BindException e)
	        {
	        	System.out.println("Port is used");
	            System.out.println(e.toString()); 
	            System.exit(1);
	        }
	        catch (IOException e)
	        {
	        	System.out.println("Fail while connect to client");
	            System.out.println(e.toString());            
	        }
    	}
    }
    
    //Close connect
    public void closeConnect() {
    	try {
        	this.m_serverSocket.close();
        	this.m_socket.close();
        	System.out.println("Connect Close");
    		
    	}catch(IOException e)
    	{
    		System.out.println("Fail to Close");
    		 System.out.println(e.toString()); 
    	}
    }
    
    //send  message to Client
    public void sendMessage(Message message) {
    	try {
    		ObjectOutputStream ObjectOutput = new ObjectOutputStream(this.m_socket.getOutputStream());
    		ObjectOutput.writeObject(message);
    	}catch(Exception e)
    	{
    		System.out.println("Fail to Send message to Client");
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
    public void sendStringArray(String[] stringArray) {
    	try {
    		ObjectOutputStream ObjectOutput = new ObjectOutputStream(this.m_socket.getOutputStream());
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
  
}