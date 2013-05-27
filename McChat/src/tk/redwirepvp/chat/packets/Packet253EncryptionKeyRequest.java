package tk.redwirepvp.chat.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.PublicKey;

import tk.redwirepvp.chat.CryptManager;
import tk.redwirepvp.chat.Packet;

public class Packet253EncryptionKeyRequest extends Packet
{
    private String serverId;
    private PublicKey publicKey;
    private byte[] verifyToken = new byte[0];

    public Packet253EncryptionKeyRequest() {}

    public Packet253EncryptionKeyRequest(String par1Str, PublicKey par2PublicKey, byte[] par3ArrayOfByte)
    {
        this.serverId = par1Str;
        this.publicKey = par2PublicKey;
        this.verifyToken = par3ArrayOfByte;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.serverId = readString(par1DataInputStream, 20);
        this.publicKey = CryptManager.func_75896_a(readBytesFromStream(par1DataInputStream));
        this.verifyToken = readBytesFromStream(par1DataInputStream);
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        writeString(this.serverId, par1DataOutputStream);
        writeByteArray(par1DataOutputStream, this.publicKey.getEncoded());
        writeByteArray(par1DataOutputStream, this.verifyToken);
    }
    
    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 2 + this.serverId.length() * 2 + 2 + this.publicKey.getEncoded().length + 2 + this.verifyToken.length;
    }

    public String getServerId()
    {
        return this.serverId;
    }

    public PublicKey getPublicKey()
    {
        return this.publicKey;
    }

    public byte[] getVerifyToken()
    {
        return this.verifyToken;
    }
}