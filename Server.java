import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    
    public static void main(String[] args) throws IOException {
        List<Thread> clients = new ArrayList<Thread>();

        ServerSocket servSocket = new ServerSocket(11350);

        while(true){
            Socket sock = null;

            try{
                sock = servSocket.accept();
                System.out.println("Client connected: " + sock);

                DataInputStream dis = new DataInputStream(sock.getInputStream());
                DataOutputStream dos = new DataOutputStream(sock.getOutputStream());

                Thread clientThread = new ClientConnection(sock, dis, dos);
                clientThread.start();
                clients.add(clientThread);
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
    final Socket sock;

    public ClientConnection(Socket sock, DataInputStream dis, DataOutputStream dos) {
        this.sock = sock;
        this.dis = dis;
        this.dos = dos;
    }

	public void sendFile() {
		try{
			File filename = new File("test.txt");
			FileInputStream fileStream = new FileInputStream(filename);

			byte[] buffer = new byte[(int)filename.length()];

			int bytesRead = 0;

			System.out.println("Wysylanie pliku: " + filename + " Do: " + sock.getPort());
			dos.writeLong(filename.length());

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
					System.out.println(this.sock + " Zarzadal zamkniecia polaczenia");
					System.out.println("Zamykanie polaczenia...");
					this.sock.close();
					System.out.println("Polaczenie zamkniete");
					break;
				}

				switch (recevied) {
					case "Pobierz" :
						sendFile();
						break;
					default:
						System.out.println("Zla wartosc: " + recevied);
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
			this.sock.close();
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
    }

}