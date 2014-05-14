import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Dustin on 5/14/14.
 */
public class Main extends JFrame {
    ArrayList<SocketWorker> workers;
    JLabel status;
    int connections;
    private SwingWorker mainWorker;
    private ServerSocket serverSocket;
    private SwingWorker updater;
    private Robot robot;
    public Main(){
        super("Pad Receiver");
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        workers = new ArrayList<SocketWorker>();
        connections = 0;
        try {
            serverSocket = new ServerSocket(5050);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                mainLoop();
                return null;
            }
        };
        updater = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                updateSocketWorkers();
                return null;
            }
        };
    }

    private void updateSocketWorkers() {
        while (true){
            for (int i = 0; i < getWorkerSize(); i = (i + 1)%getWorkerSize()){
                while(!getWorker(i).empty()){
                    robot.keyPress(getWorker(i).getPop());
                    System.out.println("typed");
                }
            }
        }
    }
    private synchronized SocketWorker getWorker(int i){
        return workers.get(i);
    }
    private synchronized void addWorker(SocketWorker skt){
        workers.add(skt);
    }
    private synchronized int getWorkerSize(){
        return workers.size();
    }

    private void mainLoop() {
        while(true){
            try {
                Socket temp = serverSocket.accept();
                System.out.print("ex");
                addWorker(new SocketWorker(temp));
                connections++;
                System.out.println("connections");
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        status.setText(connections + " Connected Users");
                    }
                });
                getWorker(workers.size()-1).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createGUI(){
        setSize(new Dimension(500,500));
        status = new JLabel(connections + " Connected Users");
        add(status);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public static void main(String[] args){
        final Main main = new Main();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                main.createGUI();
                main.setVisible(true);
            }
        });
        main.startUpdater();
        main.executeWorker();

    }

    private void startUpdater() {
        updater.execute();
    }

    private void executeWorker() {
        mainWorker.execute();
    }
}
