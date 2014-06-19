import java.io.*;
import java.net.*;
import java.util.*;


public class SimpleServer 
{
    public static void main (String [] args) 
    {
    	int port = 8888;
    	if (args.length > 0) port = Integer.parseInt(args[0]);
    	
    	SimpleServer cs = new SimpleServer();
    	cs.listen(port);
    }
	
    void listen(int port) 
    {
    	try
    	{	
    	    ServerSocket listener = new ServerSocket(port);
        
    	    System.out.println(">>>> The Chat Server <<<<");
    	    System.out.println("Telnet to " 
                               + InetAddress.getLocalHost().getHostName() 
    	    		       + " on port " + port);
    	    System.out.println("Use control-c to terminate Chat Server.");
    
    	    while (true) 
    	    {
    	    	Socket client = listener.accept();
    	    	new ChatHandler(client,this).start();
    	    	System.out.println("New client number " + num + " from " 
    	            		 + client.getInetAddress().getHostName()
    				 + " on client's port " + client.getPort());
    		add(client);
    	    }
    	}
    	catch (IOException e)
    	{   System.out.println(e);   }
    }
    
    synchronized void add(Socket s) 
    {
    	clientList.add(s);  num++;
    }  
    
    synchronized void remove(Socket s) 
    {
    	clientList.remove(s);  num--;
    }  
	
    synchronized void broadcast(String message, String source) throws IOException 
    {
    	// Sends the message to every client including source.

    	for (Socket s :  clientList) 
    	{
    	    PrintWriter p = new PrintWriter(s.getOutputStream(), true);
    	    p.println(source + ": " + message + "\n");	
    	}
    	System.out.println(source + ": " + message + "\n");
    }
    
    private List<Socket> clientList = new ArrayList<Socket>();
    private int num = 1;
}
class ChatHandler extends Thread 
{ 
    ChatHandler(Socket s, SimpleServer cs) 
    {
        clientSock = s;   chatServer = cs;
    }
    
    public void run() 
    {
        try 
        {   Scanner in = 
              new Scanner(clientSock.getInputStream());
            PrintWriter out = 
              new PrintWriter(new OutputStreamWriter(clientSock.getOutputStream()),
                                                     true);
    	    		
            out.println(">>>> Welcome to the Chat Server <<<<\n");
            out.println("Type \'bye\' to exit chat system.\n");

            out.println("What is your name?"); 
            String name = in.nextLine();
            chatServer.broadcast(name + " has joined discussion.", "Chat Server");
     
            while (true) 	
            {
            	String message = in.nextLine().trim();
            	if (message.equals("bye")) 
                {
            	    chatServer.broadcast(name + " has left discussion.", "Chat Server");
            	    break;
                }
            	chatServer.broadcast(message,name);
            }
            chatServer.remove(clientSock);
            clientSock.close();
        }
        catch (IOException e) { System.out.println("Chat error: " + e); }
    }
    
    private Socket clientSock;
    private SimpleServer chatServer;
}

/***************************************************************

>>>> The Chat Server <<<<
Telnet to october.cs.uiowa.edu on port 8888
New client num 1 from gust.cs.uiowa.edu on client's port 2101
Chat Server: Ken has joined the discussion.
Ken: This is a small group.
New client num 2 from gust.cs.uiowa.edu on client's port 2106
Chat Server: Claude has joined the discussion.
Claude: Hello all.
Claude: Is anyone there?
Ken: Yes, I am here.
Ken: This could go on forever.
Claude: That is the truth.
Chat Server: Claude has left the discussion.
Ken: Where did everyone go?
Chat Server: Ken has left the discussion.

***************************************************************/
