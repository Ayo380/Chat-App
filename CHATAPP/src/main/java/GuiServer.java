
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiServer extends Application{

	
	TextField inputText, userText;
	Button serverChoice,clientChoice,send, allClients, groupClients, oneClient, selectUsers;
	HashMap<String, Scene> sceneMap;
	GridPane grid;
	HBox buttonBox, messageBox, groupBox;
	VBox clientBox;
	Scene startScene;
	BorderPane startPane;
	Server serverConnection;
	Client clientConnection;
	
	String mode = "";
	String reciever = "";
	String message = "";
	
	
	ListView<String> listItems, listItems2;
	ListView<String> onlineList = new ListView();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Project 4: Chat App");
		
		//create and size server button
		this.serverChoice = new Button("Server");

		
		//server button event handler
		this.serverChoice.setOnAction(e->{ primaryStage.setScene(sceneMap.get("server"));
											primaryStage.setTitle("Server");
				serverConnection = new Server(data -> {
					Platform.runLater(()->{
						listItems.getItems().add(data.toString());
					});

				});
											
		});
		
		//create and size client button 
		this.clientChoice = new Button("Client");
		
		//client button event handler
		this.clientChoice.setOnAction(e-> {primaryStage.setScene(sceneMap.get("client"));
											primaryStage.setTitle("Client");
											clientConnection = new Client(data->{
							Platform.runLater(()->{	
								String msg = data.toString();
								if(msg.substring(0,1).equals("4")) {
									onlineList.getItems().clear();
									String trueMSG = msg.substring(1, msg.length());
									
									
									String[] tokens = trueMSG.split(", ");
									for (String t : tokens) {
										onlineList.getItems().add("Client #" + t + " online");
									}	
									
								}else {
								listItems2.getItems().add(data.toString());
								}
											});
							});
							
											clientConnection.start();
		});
		
		this.buttonBox = new HBox(100, serverChoice, clientChoice);
		startPane = new BorderPane();
		startPane.setPadding(new Insets(70));
		startPane.setCenter(buttonBox);
		
		startScene = new Scene(startPane, 400,400);
		
		listItems = new ListView<String>();
		listItems2 = new ListView<String>();
		
		sceneMap = new HashMap<String, Scene>();
		
		sceneMap.put("server",  createServerGui());
		sceneMap.put("client",  createClientGui());
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
		
		primaryStage.setScene(startScene);
		primaryStage.show();
		
	}
	
	public Scene createServerGui() {
		
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setStyle("-fx-background-color: blue");
		
		pane.setCenter(listItems);
	
		return new Scene(pane, 400, 400);
	}
	
	public Scene createClientGui() {
		
		inputText = new TextField();
		inputText.setDisable(true);
		
		userText = new TextField("client num seperated by comma");
		userText.setDisable(true);
		
		//selectUsers = new Button("Select");
		
		send = new Button("Send");
		send.setDisable(true);
		
		
		send.setOnAction(e->{
			clientConnection.send(inputText.getText(),mode,userText.getText()); 
			inputText.clear();
			allClients.setDisable(false);
			groupClients.setDisable(false);
			groupClients.setDisable(false);	
			userText.clear();});
		
		allClients = new Button("all clients");
		groupClients = new Button("group clients");		
		oneClient = new Button("one client");
		//
		buttonBox = new HBox(10,allClients,groupClients,oneClient); 
		buttonBox.setAlignment(Pos.CENTER);
		
		//groupBox = new HBox(10,userText,selectUsers); 
		groupBox = new HBox(10,userText); 
		groupBox.setAlignment(Pos.CENTER);
		
		messageBox = new HBox(10,inputText,send);
		messageBox.setAlignment(Pos.CENTER);
		
		allClients.setOnAction(e->{
			mode = "3";
			
			inputText.setDisable(false);
			send.setDisable(false);
			
			allClients.setDisable(true);
			groupClients.setDisable(false);	
			oneClient.setDisable(false);
			;});
		
		
		groupClients.setOnAction(e->{
			mode = "2";
			send.setDisable(false);
			userText.setDisable(false);
			
			inputText.setDisable(false);
			send.setDisable(false);
			
			allClients.setDisable(false);
			groupClients.setDisable(true);	
			oneClient.setDisable(false);
			});

		
		oneClient.setOnAction(e->{
			mode = "1";
			send.setDisable(false);
			userText.setDisable(false);
			
			inputText.setDisable(false);
			send.setDisable(false);
			
			allClients.setDisable(false);
			groupClients.setDisable(true);	
			oneClient.setDisable(false);
			});
		
		
		clientBox = new VBox(10,buttonBox,groupBox, messageBox, listItems2, onlineList);
		clientBox.setStyle("-fx-background-color: blue");
		return new Scene(clientBox, 400, 300);
		
	}

}

