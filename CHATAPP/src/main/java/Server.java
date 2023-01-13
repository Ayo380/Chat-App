import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;


public class Server{
	
	/* Variables */
	private int clientCount = 1;
	public HashMap<Integer, ClientThread> clientsMap;
	private TheServer server; 
	private Consumer<Serializable> callback;
	private ChatInfo myChatInfo;

	// this lock is used for the synchronization portion of the program
	//private Object actionLock = new Object();

	
	/* Server Constructor */
	Server(Consumer<Serializable> myCallback){
	
		this.callback = myCallback;
		this.server = new TheServer();
		this.server.start();
		this.clientsMap  = new  HashMap<Integer, ClientThread> ();
		this.myChatInfo = new ChatInfo();
	}
	
	/* function: getListOfActiveClients */
	// based on the current clientsMap, it returns a string with the keys of 
	// each of the clients on the map. This string is used as the message in
	// a ChatInfo object to update constantly the Active Clients listview.
	public String getListOfActiveClients() {

		Set<Integer> keys = clientsMap.keySet(); 
		ArrayList <Integer> keysArray = new ArrayList <Integer>();			
		for (Integer k : keys) {
			keysArray.add(k); 
		}
		
		String list = "";
		list = Integer.toString(keysArray.get(0));
		
		for (int i = 1; i < keysArray.size(); i++) {
			list = list + ", "+ Integer.toString(keysArray.get(i));
		}
	
		return list;
	}
	
	/* TheServer Class */
	public class TheServer extends Thread{
		
		public void run() {
		
			try(ServerSocket myServerSocket = new ServerSocket(5555);){
				
				callback.accept("Server has started!!!");
				System.out.println("Server has started!!!");
		  
				while(true) {
					ClientThread myClientThread = new ClientThread(myServerSocket.accept(), clientCount);
					callback.accept("-> Client #" + clientCount + " has connected to the server");
					System.out.println("-> Client #" + clientCount + " has connected to the server");
					clientsMap.put(clientCount, myClientThread);
					myClientThread.start();
					clientCount++;
			    }
			} //end of try
			catch(Exception e) {
				callback.accept("Server socket did not launch");
				System.out.println("Server socket did not launch");
				e.printStackTrace();
			}
		} //end of while
	}
	

	class ClientThread extends Thread {

		Socket myConnection;
		int myCount;
		ObjectInputStream in;
		ObjectOutputStream out;

		ClientThread(Socket socket, int count) {
			this.myConnection = socket;
			this.myCount = count;
		}

		// make updateClients() a synchronized method
		public synchronized void updateClients(String message, String m, String recipients) {

			String mode = m;

			if (mode != null) {
				System.out.println("mode not null");
			}
			// we could also do synchronized(this) right here
			// so that each instance of the clientthread is synchronized before updating other clients
			//synchronized (actionLock) {
			switch (mode) {
					case "3":
						for (ClientThread thread : clientsMap.values()) {
							try {
								// send message to each of the threads
								ChatInfo myChatInfo = new ChatInfo();
								myChatInfo.setMessage(message);
								System.out.println("All - sending msg to client");
								thread.out.writeObject(myChatInfo);
							} catch (Exception e) {
								callback.accept("To All: Failed to send message to a client");
								System.out.println("To All: Failed to send message to a client");
								e.printStackTrace();
							}
						}
						break;

					case "2":
						// parsing the string, and getting each int, into an array.
						String[] tokens = recipients.split(",");
						ArrayList<Integer> myListOfInts = new ArrayList<Integer>();

						// get ints into an array
						for (String t : tokens) {
							System.out.println("Group - sending msg to client #" + t);
							myListOfInts.add(Integer.parseInt(t));
						}


						// use a for loop, to loop through the  array
						for (int e : myListOfInts) {

							// use the keys to access each thread
							ClientThread thread = clientsMap.get(e);

							try {
								// send message to each of the threads
								ChatInfo myChatInfo = new ChatInfo();
								myChatInfo.setMessage(message);
								thread.out.writeObject(myChatInfo);
							} catch (Exception s) {
								callback.accept("To Group: Failed to send message client #" + e);
								System.out.println("To Group: Failed to send message client #" + e);
								s.printStackTrace();
							}
						}
						break;

					case "1":
						int num = Integer.parseInt(recipients);
						ClientThread thread = clientsMap.get(num);

						try {
							// use the thread to send the message to that one client
							ChatInfo myChatInfo = new ChatInfo();
							myChatInfo.setMessage(message);
							System.out.println("One - sending msg to client #" + num);
							thread.out.writeObject(myChatInfo);
						} catch (Exception s) {
							callback.accept("To One: Failed to send message client #" + num);
							System.out.println("To One: Failed to send message client #" + num);
							s.printStackTrace();
						}
						break;
					default:
						System.out.println("default");
						break;
				}
			//}
		}

		// make this one synchronized to make it thread safe?
		public void run() {
			// every instance of the client thread is synchronized before running
			synchronized (this) {
				try {
					in = new ObjectInputStream(myConnection.getInputStream());
					out = new ObjectOutputStream(myConnection.getOutputStream());
					myConnection.setTcpNoDelay(true);
				} catch (Exception e) {
					callback.accept("Streams not open");
					System.out.println("Streams not open");
					e.printStackTrace();
				}

				updateClients("-> Client #" + myCount + " joined the server!", "3", "");
				updateClients("4" + getListOfActiveClients(), "3", "");

				while (true) {
					try {
						ChatInfo datas = (ChatInfo) in.readObject();
						String message = "Client #" + myCount + " sent: " + datas.getMessage();
						callback.accept(message);
						updateClients(message, datas.getMode(), datas.getRecepient());
					} catch (Exception e) {
							callback.accept("OOOOPPs...Something wrong with the socket from client: " + myCount + "....closing down!");
							clientsMap.remove(myCount);
							updateClients("-> Client #" + myCount + " has left the server!", "3", "");
							updateClients("4" + getListOfActiveClients(), "3", "");
							e.printStackTrace();
							break;
					}
				}
			} // end of run
		} // end of client thread
	}
}


	
	

	
