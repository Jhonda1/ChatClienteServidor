import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;
    private JFrame frame = new JFrame("Chat Client");
    private JTextArea textArea = new JTextArea(10, 40);
    private JTextField textField = new JTextField(40);
    private JScrollPane scrollPane = new JScrollPane(textArea);
    private String username;
    private PrintWriter out;

    public Client() {
        frame = new JFrame("Chat usuarios");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea(10, 40);
        textField = new JTextField(40);
        JScrollPane scrollPane = new JScrollPane(textArea);

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(textField, BorderLayout.SOUTH);
        frame.pack();

        // Calcular la posición de la ventana
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int windowX = (int) (screenSize.getWidth() - frame.getWidth()) / 2;
        int windowY = (int) (screenSize.getHeight() - frame.getHeight()) / 2;

        // Establecer la posición de la ventana
        frame.setLocation(windowX, windowY);
        frame.setVisible(true);


        // Dialog to ask for username
        username = JOptionPane.showInputDialog(frame, "Ingrese su nombre de usuario :");

        try {
            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Thread for receiving messages from the server
            Thread receiveThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String serverMessage;
                        while ((serverMessage = in.readLine()) != null) {
                            appendMessage(serverMessage);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            receiveThread.start();

            // ActionListener for sending messages
            textField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String message = textField.getText();
                    if (!message.isEmpty()) {
                        sendMessage(message);
                        textField.setText("");
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendMessage(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textArea.append(message + "\n");
            }
        });
    }

    private void sendMessage(String message) {
        if (username != null && !username.isEmpty()) {
            message = username + ": " + message;
        }
        out.println(message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Client();
            }
        });
    }
}
