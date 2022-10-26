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
    public boolean login = false;

    public Server() {
        jta = new JTextArea();
        setLayout(new BorderLayout());
        add(new JScrollPane(jta), BorderLayout.CENTER);
        setTitle("Server");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true); // It is necessary to show the frame here!
        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(8000);
            jta.append("Server started at " + new Date() + '\n');
            // Number a session
            int sessionId = 1;
            // Ready to create a session for every two players
            while (true) {
                jta.append("Waiting for players to join session " + '\n');
                // Connect to player 1
                Socket client1 = serverSocket.accept();
                jta.append("Client 1 joined session " + '\n');
                jta.append("Client 1's IP address" + client1.getInetAddress().getHostAddress() + '\n');
                // Notify that the player is Player 1
                new DataOutputStream(client1.getOutputStream()).writeInt(1);
                // Connect to player 2
                Socket client2 = serverSocket.accept();
                jta.append("Client 2 joined session " + '\n');
                jta.append("Client 2's IP address" + client2.getInetAddress().getHostAddress() + '\n');
                // Notify that the player is Player 2
                new DataOutputStream(client2.getOutputStream()).writeInt(2);
                // Display this session and increment session number
                jta.append("Start a thread for session " + sessionId++ + '\n');
                // Find the client's host name, and IP address
                InetAddress inetAddress = InetAddress.getLocalHost();
                jta.append("Host name is " + inetAddress.getHostName() + '\n');
                jta.append("IP Address is " + inetAddress.getHostAddress() + '\n');
                // Create and start a new thread for this session of two players
                ThreadClass t = new ThreadClass(s);
                t.start();
            }
        }
        catch (IOException ex) {
            System.err.println(ex);
        }
    }
    private class ThreadClass extends Thread {
        Socket socket;
        private InetAddress address;
        private DataInputStream inputFromClient;
        private DataOutputStream outputToClient;           
        public Statement st = null;
        private int id = -1;
        public ThreadClass(Socket socket) throws IOException{
            try {
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test_create_DB", "root", "root");
                st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
            this.socket = socket;
            address = socket.getInetAddress();
            inputFromClient = new DataInputStream(socket.getInputStream());
            outputToClient = new DataOutputStream(socket.getOutputStream());
        }
        
        public void run() {
            try {
                while (login) {
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
                while (!login) {
                    String username = inputFromClient.readUTF();
                    String sql = "SELECT * FROM user WHERE username = '" + username + "'";
                    String updateTOT = "\"update students set TOT_REQ=TOT_REQ+1 where STUD_ID='\" + id + \"'\"";
                    rs = st.executeQuery(sql);
                    if (rs.next()) {
                        id = rs.getInt("STUD_ID");
                        login = true;
                        outputToClient.writeBoolean(true);
                        outputToClient.writeUTF("Login successful: " + rs.getString("FNAME"));
                        st.execute(updateTOT);
                        System.out.println("Login successful");
                    } else {
                        outputToClient.writeBoolean(false);
                        outputToClient.writeUTF("Login failed");
                        System.out.println("Login failed");
                    }
                }
            } catch(Exception e) {
                System.err.println(e + "on " + socket);
                e.printStackTrace();
            }
        }
    }
}
