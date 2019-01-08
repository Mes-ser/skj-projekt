import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    
    public static void main(String[] args) throws IOException {
        Map<Integer, Thread> clientsList = new HashMap<Integer, Thread>();

        ServerSocket servSocket = new ServerSocket(11350);

        while(true){
            Socket sock = null;

            try{
                sock = servSocket.accept();
                System.out.println("Client " + sock + " Connected");

                DataInputStream dis = new DataInputStream(sock.getInputStream());
				DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
				
				ClientConnection client = new ClientConnection(sock, dis, dos);

				client.start();
				
				clientsList.put(client.clientSock.getPort(), client);
            }
            catch(Exception ex){
                sock.close();
                ex.printStackTrace();
            }
        }
    }
}

// Klasa umozliwiajaca operowanie na klientach w watkach
class ClientConnection extends Thread {

    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket clientSock;

    public ClientConnection(Socket sock, DataInputStream dis, DataOutputStream dos) {
        this.clientSock = sock;
        this.dis = dis;
        this.dos = dos;
    }

	public void sendFile(File fileToSend) throws IOException{
		try{
			FileInputStream fileStream = new FileInputStream(fileToSend);
			dos.write(0);
			byte[] buffer = new byte[(int)fileToSend.length()];

			int bytesRead = 0;

			System.out.println("Sending file " + fileToSend + " to Client " + clientSock.getPort());
			dos.writeLong(fileToSend.length());

			while ((bytesRead = fileStream.read(buffer)) > 0) {
				dos.write(buffer, 0, bytesRead);
			}
			fileStream.close();
			System.out.println("Data sended to: " + this.clientSock.getPort());
		}
		catch (Exception ex) {
			ex.printStackTrace();
			dos.write(1);
		}
	}
    // tu tworzymy obsluge zapytan od klienta
    @Override
    public void run() {

		String recevied;
		boolean exitFlag = false;
		while(!exitFlag){
			try {
				dos.writeUTF("----------\n" + "Available commands:\nPULL\nPUSH\nGetClients\n" + "-----------");

				recevied = dis.readUTF();

				if(recevied.equals("Exit")) {
					System.out.println("Close connection request from: " + this.clientSock.getPort());
					System.out.println("Closing connection for ");
					this.clientSock.close();
					System.out.println("Connection Closed");
					break;
				}

				switch (recevied) {
					case "PULL" :
						File file = new File("test.txt");
						sendFile(file);
						break;
					default:
						System.out.println("Invalid imput - " + recevied);
						break;
				}
			}
			catch (IOException ex) {
				ex.printStackTrace();
				exitFlag = true;
			}
		}
		try {
			this.dis.close();
			this.dos.close();
			this.clientSock.close();
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
    }

}