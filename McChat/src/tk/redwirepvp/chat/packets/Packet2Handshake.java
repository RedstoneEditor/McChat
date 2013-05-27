package tk.redwirepvp.chat.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import tk.redwirepvp.chat.Main;
import tk.redwirepvp.chat.Packet;

public class Packet2Handshake extends Packet{
	private DataOutputStream out;
	private DataInputStream in;
	private Main m;
	public Packet2Handshake(Main instance) {
		m = instance;
		try {
			out = new DataOutputStream(m.connection.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void writePacketData(DataOutputStream var1){
		try {
			out.writeByte(0x02);
			out.writeByte(61);
			writeString(m.account.sessionName, var1);
			writeString(m.server, var1);
			out.writeInt(m.port);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int getPacketSize() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void readPacketData(DataInputStream var1) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
