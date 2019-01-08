import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

	public static void downloadFile(Socket sock, DataOutputStream dos, DataInputStream dis) {
		String fileName = "recTest.txt";
		String cmd = fileName + "\n";
		try {
			if(dis.read() == 0){
				OutputStreamWriter sWriter = new OutputStreamWriter(sock.getOutputStream());

				sWriter.write(cmd, 0, cmd.length());

				FileOutputStream fileStream = new FileOutputStream(fileName);

				while(dis.available() < 4) {}
				long fileSize = dis.readLong();

				byte[] buffer = new byte[(int)fileSize];

				while(dis.available()<fileSize) {}
				System.out.println("Pobieranie Pliku");
				dis.readFully(buffer);
				fileStream.write(buffer, 0, (int)fileSize);
				fileStream.flush();
				System.out.println("Pobrano plik");
				fileStream.close();
			}else{
				System.out.println("Error occured on server side");
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
    public static void main(String[] args) throws IOException {
		Scanner scn = new Scanner(System.in);
		
        try {
			Socket sock = new Socket("127.0.0.1", 11350);
			System.out.println("Ustalono polaczenie z: " + sock);
			DataInputStream dis = new DataInputStream(sock.getInputStream());
			DataOutputStream dos = new DataOutputStream(sock.getOutputStream());

			while(true) {
				System.out.println(dis.readUTF());
				String toSend = scn.nextLine();
				dos.writeUTF(toSend);

				if(toSend.equals("Exit")) {
					System.out.println("Zamykanie polaczenia " + sock);
					sock.close();
					System.out.println("Polaczenie zamkniete");
					break;
				}

				switch (toSend){
					case "PULL" :
						downloadFile(sock, dos, dis);
						break;
					default:
						break;
				}
			}

			scn.close();
			dis.close();
			dos.close();
        }
        catch (Exception ex) {
			ex.printStackTrace();
        }
    }
}