package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPHandler extends Thread {
	private Socket socket;
	
	public TCPHandler(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						
			String data = br.readLine();
			String header = data.substring(0, 2);
			
			if ("01".equals(header)) {
				loginHandler(data.substring(3));
			}
		} catch (IOException e) {
			System.out.println("Error when read or write data");
			e.printStackTrace();
		}
	}
	
	private void loginHandler(String data) throws IOException {
		String[] body = data.split("&");
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		
		if ("admin".equals(body[0]) && "admin".equals(body[1])) {
			dos.writeBytes("1\n");
		} else {
			dos.writeBytes("0\n");
		}
	}
}
