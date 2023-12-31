package com.zgamelogic.helpers;

import com.zgamelogic.data.serializable.monitors.MinecraftMonitor;
import com.zgamelogic.data.serializable.StatusMinecraft;
import com.zgamelogic.data.serializable.Status;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public abstract class MCInterfacer {

    public static final byte PACKET_HANDSHAKE = 0x00, PACKET_STATUSREQUEST = 0x00, PACKET_PING = 0x01;
    public static final int STATUS_HANDSHAKE = 1;

    public static Status pingServer(MinecraftMonitor minecraftMonitor){
        StatusMinecraft mh = new StatusMinecraft();
        mh.setup();

        int tries = 0;
        while(tries < 3) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(minecraftMonitor.getUrl(), minecraftMonitor.getPort()), 1000);
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                ByteArrayOutputStream handshake_bytes = new ByteArrayOutputStream();
                DataOutputStream handshake = new DataOutputStream(handshake_bytes);

                handshake.writeByte(PACKET_HANDSHAKE);
                writeVarInt(handshake, 4);
                writeVarInt(handshake, minecraftMonitor.getUrl().length());
                handshake.writeBytes(minecraftMonitor.getUrl());
                handshake.writeShort(minecraftMonitor.getPort());
                writeVarInt(handshake, STATUS_HANDSHAKE);

                writeVarInt(out, handshake_bytes.size());
                out.write(handshake_bytes.toByteArray());

                out.writeByte(0x01);
                out.writeByte(PACKET_STATUSREQUEST);

                readVarInt(in);
                int id = readVarInt(in);

                int length = readVarInt(in);

                byte[] data = new byte[length];
                in.readFully(data);
                JSONObject json = new JSONObject(new String(data));
                mh.update(json);

                out.writeByte(0x09);
                out.writeByte(PACKET_PING);
                out.writeLong(System.currentTimeMillis());

                readVarInt(in);
                id = readVarInt(in);

                socket.close();
                return mh;
            } catch (Exception e) {
                tries++;
                try {
                    Thread.sleep(150);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        mh.setStatus(false);
        return mh;
    }

    private static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.writeByte(paramInt);
                return;
            }

            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    private static int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();

            i |= (k & 0x7F) << j++ * 7;

            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }

            if ((k & 0x80) != 128) {
                break;
            }
        }

        return i;
    }
}
