import javax.swing.*;
import javax.swing.text.StringContent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Dustin on 5/14/14.
 */
public class SocketWorker {
    private SwingWorker worker;
    private Socket skt;
    Queue<Integer> backlog;
    public SocketWorker(Socket s){
        backlog =  (Queue<Integer>) new LinkedList<Integer>();
        skt = s;
        worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                loop();
                return null;
            }
        };
    }

    private void loop() {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(skt.getInputStream());
            while(true){
                addBacklog((Integer) inputStream.readObject());
                System.out.println("shouldbeworking");
                System.out.println(backlog);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        worker.execute();
    }
    public synchronized void addBacklog(int s){
        backlog.add(s);
    }
    public synchronized Integer getPop(){
        return backlog.poll();
    }
    public synchronized boolean empty(){
        return backlog.isEmpty();
    }
}
