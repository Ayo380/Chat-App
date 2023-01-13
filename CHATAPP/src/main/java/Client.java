import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;



public class Client extends Thread{

	/* Variables */
	private Socket socketClient;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Consumer<Serializable> callback;
	
	/* Constructor */
	Client(Consumer<Serializable> myCallBack){
		this.callback = myCallBack;
	}
	
	
	public void run() {
		
		try {
			socketClient= new Socket("127.0.0.1",5555);
			
			callback.accept("Client has started!!!");
			System.out.println("Client has started!!!");
			
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
			socketClient.setTcpNoDelay(true);
			
		}
		catch(Exception e) {
			callback.accept("Client Socket did not launch");
			System.out.println("Client Socket did not launch");
			e.printStackTrace();
		}
		
		while(true) {
			 
			try {
				ChatInfo data = (ChatInfo)in.readObject();
		    	String message = data.getMessage();
				callback.accept(message);
			}
			catch(Exception e) {
				callback.accept("Client couldn't receieve message from server");
				System.out.println("Client couldn't receieve message from server");
				e.printStackTrace();
			}
		}
	
    }
	
	public void send(String msg, String mode, String recipients) {
		
		ChatInfo myChatInfo = new ChatInfo();
		myChatInfo.setMessage(msg);
		myChatInfo.setMode(mode);
		myChatInfo.setRecipient(recipients);

		
		try {
			out.writeObject(myChatInfo);
		} 
		catch (IOException e) {
            System.out.println(" Client failed to send to the server! Details:");
            callback.accept(" Client failed to send to the server! Details:");
            e.printStackTrace();
		}
	}


}
