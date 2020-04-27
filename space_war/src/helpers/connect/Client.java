package helpers.connect;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import helpers.CheckAndAlert;

public class Client {
	private static String serverIP = "192.168.0.105";
	private static int serverPort = 8008;
	
	public Client() {}
	
	public static boolean sendLoginRequest(String data) {
		Socket socketClient = null;
		try {
			socketClient = new Socket(serverIP, serverPort);
			DataOutputStream dos = new DataOutputStream(socketClient.getOutputStream());
			dos.writeBytes(data + "\n");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
			String response = br.readLine();
			
			socketClient.close();
			return ("1".equals(response));
			
		} catch (IOException e) {
			CheckAndAlert.alertErrorMessage("Lỗi kết nối. Hãy thử lại!");
			if (socketClient != null)
				try {
					socketClient.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			e.printStackTrace();
		}
		
		return false;
	}
}