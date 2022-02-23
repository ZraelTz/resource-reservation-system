
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class Network extends Thread {

    private Socket clientSocket;
    private ServerSocket serverSocket;
    private int tcpPort;
    private Map<String, Integer> resources;
    
    private ArrayList<NetworkThread> threadList = new ArrayList<>();

    public Network(int port, Map<String, Integer> resources) {
        this.tcpPort = port;
        this.resources = resources;
    }

    public List<NetworkThread> getThreadList() {
        return threadList;
    }
    
    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(tcpPort);
            //server keeps listening for connections from client
            while (true) {
                System.out.println("Listening for connections...");
                clientSocket = serverSocket.accept();
                //prints accepted connection from client socket
                System.out.println("Connection accepted from " + clientSocket);
                
                NetworkThread networkThread = new NetworkThread(this, clientSocket);
                threadList.add(networkThread);
                networkThread.start();
            }

        } catch (IOException e) {
            if(e.getClass().isInstance(new BindException())){
                System.err.println("The tcp gateway port specified is already in use");
            } else
            e.printStackTrace();
        }
    }
    
    void removeNetworkThread(NetworkThread networkThread) throws IOException {
        threadList.remove(networkThread);
    }
    
    public Map<String, Integer> getResources(){
        return resources;
    }
    
    public void setResources(String key, Integer oldVal, Integer newVal){
        resources.replace(key, oldVal, newVal);
    }

    void close() {
        System.exit(1);
    }
}
