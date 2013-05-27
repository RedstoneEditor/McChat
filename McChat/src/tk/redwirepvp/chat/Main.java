package tk.redwirepvp.chat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import tk.redwirepvp.chat.packets.Packet252SharedKey;
import tk.redwirepvp.chat.packets.Packet253EncryptionKeyRequest;
import tk.redwirepvp.chat.packets.Packet2Handshake;

public class Main extends Thread {
	public String server;
	public int port;
	public Socket connection;
	private DataOutputStream out;
	private DataInputStream in;
	public Account account;

	public Main(String username, String password, String server, int port) {
		this.server = server;
		this.port = port;
		try {
			connection = new Socket(this.server, this.port);
			this.out = new DataOutputStream(connection.getOutputStream());
			this.in = new DataInputStream(connection.getInputStream());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		account = new Account(username, password);
		account.login();
		this.start();
		this.beginTransmission();
	}

	private void beginTransmission() {
		Packet2Handshake packet = new Packet2Handshake(this);
		packet.writePacketData(out);
	}

	public static void main(String[] args) {
		Main m = new Main("RedstoneEditor@gmail.com", "renaud",
				"localhost", 25565);
	}

	public void run() {
		try {
			while (connection.isConnected()) {
				short id = (short) (in.readByte() & 0xFF);
				Packet packet = Packet.getNewPacket(id);
				if (packet instanceof Packet253EncryptionKeyRequest){
					System.out.println(packet.getClass().getSimpleName());
					((Packet253EncryptionKeyRequest) packet).readPacketData(in);
					handleServerAuthData((Packet253EncryptionKeyRequest) packet);
				}
				else if (id == 255){
					System.out.println("Disconnected from server: "
							+ Packet.readString(in, 256)); connection.close();}
				else
					throw new IOException("Bad packet id: " + packet);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void handleServerAuthData(Packet253EncryptionKeyRequest keyRequest) {
		String serverId = keyRequest.getServerId().trim();
		PublicKey publicKey = keyRequest.getPublicKey();
		SecretKey secretKey = CryptManager.func_75890_a();

		if (!serverId.equals("-")) {
			String hash = new BigInteger(CryptManager.func_75895_a(serverId,
					publicKey, secretKey)).toString(16);
			if (account.getSessionId() != null) {
				String response = authenticate(account.sessionName,
						account.getSessionId(), hash);

				if (response == null)
					return;

				if (!response.equalsIgnoreCase("ok")) {
					try {
						connection.close();
						System.out.println("Failed login: " + response);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return;
				}
			}
		}

		Packet252SharedKey sharedKey = new Packet252SharedKey(secretKey,
				publicKey, keyRequest.getVerifyToken());
		try {
			sharedKey.writePacketData(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private String authenticate(String username, String sessionId,
			String serverId) {
		try {
			URL url = new URL(
					(new StringBuilder())
							.append("http://session.minecraft.net/game/joinserver.jsp?user=")
							.append(encodeUtf8(username)).append("&sessionId=")
							.append(encodeUtf8(sessionId)).append("&serverId=")
							.append(encodeUtf8(serverId)).toString());
			BufferedReader bufferedreader;
				URLConnection connection = url.openConnection();
				connection.setConnectTimeout(30000);
				connection.setReadTimeout(30000);
				bufferedreader = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
			String response = bufferedreader.readLine();
			bufferedreader.close();

			return response;
		} catch(Exception exception) {
			exception.printStackTrace();
			try {
				connection.close();
				System.out.println("Internal error handling handshake: " + exception);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}

	private static String encodeUtf8(String par0Str) throws IOException {
		return URLEncoder.encode(par0Str, "UTF-8");
	}

}