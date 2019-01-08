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
				System.out.println(client);
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

	public void sendFile(File fileToSend) {
		try{
			FileInputStream fileStream = new FileInputStream(fileToSend);

			byte[] buffer = new byte[(int)fileToSend.length()];

			int bytesRead = 0;

			System.out.println("Wysylanie pliku: " + fileToSend + " Do: " + clientSock.getPort());
			dos.writeLong(fileToSend.length());

			while ((bytesRead = fileStream.read(buffer)) > 0) {
				dos.write(buffer, 0, bytesRead);
			}
			fileStream.close();
			System.out.println("Wyslano");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
    // tu tworzymy obsluge zapytan od klienta
    @Override
    public void run() {

		String recevied;

		while(true){
			try {
				dos.writeUTF("----------\n" + "By pobrac plik wpisz Pobierz\n" + "Wpisz Exit by zamknac polaczenie\n" + "-----------");

				recevied = dis.readUTF();

				if(recevied.equals("Exit")) {
					System.out.println("Close connection request from: " + this.clientSock.getPort());
					System.out.println("Closing connection for");
					this.clientSock.close();
					System.out.println("Connection Closed");
					break;
				}

				switch (recevied) {
					case "Pobierz" :
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