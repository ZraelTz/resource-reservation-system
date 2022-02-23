
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkThread extends Thread {

    private final Socket clientSocket;
    private final Network network;
    private OutputStream outputStream;

    //NetworkThreads constructor initializes all the needed fields
    
    public NetworkThread(Network network, Socket clientSocket) {
        this.network = network;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
            try {
                //handles NetworkClient actions after connection
                handleClientRequests();
            } catch (IOException ex) {
            } catch (InterruptedException ex) {
                Logger.getLogger(NetworkThread.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public void handleClientRequests() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        String command;
        Map<String, Integer> resources = network.getResources();

        while ((command = reader.readLine()) != null) {
            if (command.split(" ")[0].equalsIgnoreCase("Sending:")) {
                System.out.println("Resource request from Client: " + clientSocket.getInetAddress());
                String request[] = command.split(" ");
                String resourceRequests = "";
                
                for(int i =2; i<request.length; i++){
                resourceRequests += request[i] + " ";
                }
               
                System.out.println(resourceRequests);
                
                for(int i =0; i<resourceRequests.split(" ").length; i++){
                    
                    String requestedResourceKey = resourceRequests.split(" ")[i].split(":")[0];
                    int requestedResourceValue = Integer.parseInt(resourceRequests.split(" ")[i].split(":")[1]);
                    
                    if(resources.containsKey(requestedResourceKey)){
                        int value = resources.get(requestedResourceKey);
                        if(value > requestedResourceValue){
                            network.setResources(requestedResourceKey, value, value-requestedResourceValue);
                            System.out.println("Allocation-successful: " 
                                    + requestedResourceKey + ":" + requestedResourceValue + " to " + clientSocket);
                            outputStream.write(("ALLOCATED: " 
                                    + requestedResourceKey + ":" + requestedResourceValue + "\n").getBytes());
                        } else {
                            System.err.println("Allocation-failed: insufficient resource " + requestedResourceKey);
                            outputStream.write(("FAILED: insufficient resource - " + requestedResourceKey + "\n").getBytes());
                        }
                    } else {
                        System.err.println("Resource-unavailable: the requested resource is unavailable - " + requestedResourceKey);
                        outputStream.write(("Resource-unavailable: the requested resource is unavailable - " + requestedResourceKey + "\n").getBytes());
                    }
                }
                
                System.out.println("Resources available at this network node");
                System.out.println(network.getResources().toString());
                
                outputStream.write(("Resources available at the network node:" + "\n").getBytes());
                outputStream.write((network.getResources().toString() + "\n").getBytes());
                
            }
        }
    }
}
