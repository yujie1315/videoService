package cn.ZhouJW.Service;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.ArrayList;

public class UdpReceiveAndTcpSend extends Thread {

	private MulticastSocket ms;
	private DatagramPacket dp;
	private final int Port = 9676;
	private BufferedWriter bufferedWriter;
	private ArrayList<String> ips;

	@Override
	public void run() {
		byte[] data = new byte[1024];
System.out.println("开始监听！！！");
		try {
			InetAddress groupAddress = InetAddress.getByName("224.1.2.3");
			ms = new MulticastSocket(Port);
			ms.joinGroup(groupAddress);
		} catch (Exception e) {
			e.printStackTrace();
		}

		while (true) {

			try {
				dp = new DatagramPacket(data, data.length);
				if (ms != null)
					ms.receive(dp);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (dp.getAddress() != null) {

				final String codeString = new String(data, 0, dp.getLength());
				 System.out.println(codeString);
				if (! "getServer".equals(codeString))
				 continue;
				System.out.println(codeString + dp.getAddress().toString().substring(1));
				System.out.println(dp.getAddress().toString());
				final String target_ip = dp.getAddress().toString().substring(1);
				try {
					Socket socket = new Socket(target_ip, Port);
					bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
					bufferedWriter.write("I am server");
					bufferedWriter.newLine();
					bufferedWriter.flush();
					socket.shutdownOutput();
					System.out.println("socket写入完成");
					bufferedWriter.close();
					socket.close();
				} catch (Exception e) {
					e.printStackTrace();
					// TODO: handle exception
				}
			}
		}
	}
}
