package loc.zank;

import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static Set<DataOutputStream> writers = new HashSet<>();
    private int serverPort;
    private JTextArea archiveArea = new JTextArea(10, 40);
    private JTextArea newsArea = new JTextArea(10, 40);

    private Server(int serverPort) {
        this.serverPort = serverPort;
        initComponents();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Pass the port as the sole command line argument");
            return;
        }

        System.out.println("The server is running...");

        new Server(Integer.parseInt(args[0])).run();

    }

    private void initComponents() {

        JFrame frame = new JFrame("Newsletter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        JPanel panelTop = new JPanel(new BorderLayout());
        JPanel panelCenter = new JPanel(new BorderLayout());
        JPanel panelBottom = new JPanel(new GridBagLayout());
        frame.add(panelTop, BorderLayout.NORTH);
        frame.add(panelCenter, BorderLayout.CENTER);
        frame.add(panelBottom, BorderLayout.SOUTH);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(actionEvent -> {
            sendNews(newsArea.getText());
            newsArea.setText("");
        });

        archiveArea.setLineWrap(true);
        archiveArea.setEditable(false);
        newsArea.setLineWrap(true);

        panelTop.add(new JLabel("Archive"), BorderLayout.NORTH);
        panelTop.add(new JScrollPane(archiveArea), BorderLayout.CENTER);
        panelCenter.add(new JLabel("News"), BorderLayout.NORTH);
        panelCenter.add(new JScrollPane(newsArea), BorderLayout.CENTER);
        panelBottom.add(sendButton);

        frame.pack();

    }

    private void sendNews(String news) {
        if (news != null && !news.equals("")) {
            archiveArea.append("\n" + news);
            try {
                for (DataOutputStream out : Server.writers) {
                    out.writeUTF(news);
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void run() {

        ExecutorService pool = Executors.newFixedThreadPool(100);
        try (ServerSocket listener = new ServerSocket(serverPort)) {
            while (true) {
                pool.execute(new Handler(listener.accept()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static class Handler implements Runnable {
        private Socket socket;

        Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                Server.writers.add(out);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
