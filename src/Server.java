import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
public class Server extends JFrame{

    ServerSocket server ;
    Socket socket;

    BufferedReader in;
    PrintWriter out;

    private JLabel heading = new JLabel("Server Area");
    private  JTextArea msgArea = new JTextArea();
    private  JTextField messageInput = new JTextField();

    private Font font = new Font("Roboto",Font.PLAIN,20);
    public Server(){
        try {
            server = new ServerSocket(7777);
            System.out.println("server is ready for connection");
            System.out.println("waiting...");


            createGUI();

            socket = server.accept();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            handleEvents();
            startReading();
//            startWriting();


        }catch(Exception e){
//            e.printStackTrace();
            System.out.println("connection is closed");

        }

    }

    private void createGUI(){
        this.setTitle("Server Messenger[END]");
        this.setSize(500,700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //coding for component
        heading.setFont(font);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);

        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        messageInput.setFont(font);
        msgArea.setFont(font);
        heading.setIcon(new ImageIcon("img.png"));
        msgArea.setEditable(false);
        //setting layout
        this.setLayout(new BorderLayout());

        //adding components to frame

        this.add(heading,BorderLayout.NORTH);
        JScrollPane jsp = new JScrollPane(msgArea);
        this.add(jsp,BorderLayout.CENTER);

        this.add(messageInput,BorderLayout.SOUTH);






        this.setVisible(true);
    }

    private  void handleEvents(){
        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
//                System.out.println("key released" + e.getKeyCode()); //keycode of enter = 10
                if(e.getKeyCode() ==  10) {
//                    System.out.println("enter button");
                    String contentToSend = messageInput.getText();
                    msgArea.append("Me : "+contentToSend+"\n");
                    out.println(contentToSend);
                    out.flush();

                   messageInput.setText("");

                    if(contentToSend.equals("exit")){
                        try {
                            socket.close();
                            messageInput.setEnabled(false);
                            System.out.println("server terminated the chat");
                        }catch (Exception x){
                            messageInput.setEnabled(false);
                            System.out.println("server terminated the chat");
                        }


                    }

                }

            }
        });
    }


    public void startReading() {
        Runnable r1= () -> {
            System.out.println("reader started...");
            try {


                while (true) {

                    String msg = in.readLine();
                    if (msg.equals("Client : exit")) {
                        System.out.println("client terminated the chat");
                        JOptionPane.showMessageDialog(this,"client terminated chat");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    msgArea.append(msg+"\n");

                }
            }catch(Exception e){
//                e.printStackTrace();
                System.out.println("connection closed");
            }
        };
        new Thread(r1).start();
    }
    public  void startWriting(){
        Runnable r2= () -> {
            System.out.println("writing started...");
            try {
            while(!socket.isClosed() ) {

                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String content = br.readLine();


                msgArea.append("Me : "+content+"\n");
                out.println("Client : " + content);
                out.println(content);
                out.flush();

                if(content.equals("exit")){

                    System.out.println("Client terminated the chat");
                    messageInput.setEnabled(false);
                    socket.close();
                    break;
                }
            }
            }catch(Exception e){
//                    e.printStackTrace();
                System.out.println("connection is closed");
                messageInput.setEnabled(false);
            }


        };

        new Thread(r2).start();
    }
    public static void main(String[] args) {
        System.out.println("started");
        new Server();

    }
}
