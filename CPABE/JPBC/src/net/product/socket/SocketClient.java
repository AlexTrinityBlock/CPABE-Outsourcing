package net.product.socket;

import java.io.*;
import java.net.*;
import net.product.data.Message;

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
    
       
}
