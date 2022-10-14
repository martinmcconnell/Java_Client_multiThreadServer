// mySQL JDBC GUI
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;

public class Main
{
    Client client = new Client();

    public static String name="", email ="";
    public static void main(String[] args){
        JFrame f=new JFrame();
        JLabel labell = new JLabel("Name: ");
        //JLabel labe12 = new JLabel("Email: ");
        JTextField textl = new JTextField(20);
        //JTextField text2=new JTextField(20);
        JButton b1=new JButton("login");
        //JButton b2=new JButton("prev");
        Connection con;
        Statement st;
        try{
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test_create_DB", "root", "");
            st=con.createStatement();
            rs=st.executeQuery("select * from data");
            if(rs.next()){
                name=rs.getString("name");
                //email=rs.getString("email");
                textl.setText(name);
                //text2.setText(email);
            }
        }catch(Exception e){}
        JPanel p=new JPanel(new GridLayout(10,10));
        p.add(labell);
        p.add(textl);

        f.add(p);
        f.setVisible(true);
        f.pack();

        p.add(b1);

        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    System.out.println("login pressed..");
                    client.run();

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }
}