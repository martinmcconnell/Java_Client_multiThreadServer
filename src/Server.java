import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.awt.*;
import javax.swing.*;

public class Server extends JFrame {
    JTextArea jta;
    public static ResultSet rs;


    private class ThreadClass extends Thread {
        Socket socket;
        private InetAddress address;
        private DataInputStream inputFromClient;
        private DataOutputStream outputToClient;           
        
        public ThreadClass(Socket socket){
            this.socket = socket;
            address = socket.getInetAddress();
            try {
                DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
                DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());
            }catch (IOException e) {
                System.err.println("Exception in class");
                e.printStackTrace();
            }
        }
        
        public void run() {
            try {
            while (true) {
                
                System.out.println("HELLO");
            // Receive radius from the client
            double radius = inputFromClient.readDouble();

            // Compute area
            double area = radius * radius * Math.PI;

            // Send area back to the client
            outputToClient.writeDouble(area);

            jta.append("Radius received from client: " + radius + '\n');
            jta.append("Area found: " + area + '\n');
        }
            } catch(Exception e) {
                System.err.println(e + "on " + socket);
                e.printStackTrace();
            }
        }
    }
}
