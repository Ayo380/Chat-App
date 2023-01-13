import java.io.Serializable;
import java.util.ArrayList;
public class ChatInfo implements Serializable {

    private static final long serialVersionUID = 42L;

    private String chatMessage;
    private String recipient;
    private String mode;
    private ArrayList <Integer> activeUsers; 
    
    ChatInfo(){
        this.chatMessage = "";
        this.recipient = "";
        this.mode = "";
        this.activeUsers = new ArrayList<Integer>();
    }

    public String getMessage(){
        return this.chatMessage;
    }

    public String getRecepient(){
        return this.recipient;
    }
    
    public String getMode(){
        return this.mode;
    }
    
    public ArrayList <Integer> getActiveUsers(){
        return this.activeUsers;
    }
    
    public void setMode(String modeSet){
        this.mode = modeSet;
    }

    public void setMessage(String message){
        this.chatMessage = message;
    }

    public void setRecipient(String message){
        this.recipient = message;
    }

}
