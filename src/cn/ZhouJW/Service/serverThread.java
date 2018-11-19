package cn.ZhouJW.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import java.io.BufferedReader;
import java.io.BufferedWriter;

public class serverThread extends Thread {

	public String fileNames, password;
	private JTextArea textarea;
	private ServerSocket server;
	private final int Listen_Port = 9676;
	ArrayList<Socket> mySocketLists;

	// 构造函数
	public serverThread(String fileNames, JTextArea textarea, String password) {

		this.fileNames = fileNames;
		this.textarea = textarea;
		this.password = password;
		this.mySocketLists = new ArrayList<Socket>();

	}

	// 一定要执行的代码
	public void finalize() {
		try {
			server.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "error", "server close failed", JOptionPane.ERROR_MESSAGE);
			return;
		}

	}

	public void setFileNames(String fileNames) {
		this.fileNames = fileNames;
		System.out.println(this.fileNames);
	}

	// 服务线程代码
	@Override
	public void run() {
		textarea.append("文件信息服务启动..." + "\n");

		try {
			server = new ServerSocket(Listen_Port);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "提示", "端口被占用", JOptionPane.ERROR_MESSAGE);
			return;
		}
		while (true) {
			Socket client = null;
			try {
				client = server.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mySocketLists.add(client);
			try {
				new server(client).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				textarea.append("服务通信线程启动错误\n");
			}
		}
	}

	// socket通信
	class server extends Thread {

		private Socket client;
		private BufferedReader bReader;
		private BufferedWriter bWriter;
		private String msg = null, info = null, action = null, cip = null;

		public server(Socket s) throws IOException {
			client = s;
			cip = client.getInetAddress().getHostAddress();
		}

		public void logout() throws IOException {
			ClientCount.sub();
			textarea.append("客户端下线！ IP：" + cip + "  当前在线客户端" + ClientCount.getCount() + "个\n");
			try {
				bWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8"));
				bWriter.write("logout success");
				bWriter.newLine();
				bWriter.flush();
				bWriter.close();
				textarea.append("客户端已下线！！\n");
				bReader.close();
				client.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		public void login(String passCode) throws IOException {

			textarea.append("获得密码:" + passCode + "\n");
			try {
				bWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8"));
				if (!password.equals(passCode)) {
					bWriter.write("codeerror");
					bWriter.newLine();
					bWriter.flush();
					textarea.append("通知客户端密码错误！\n");
					bWriter.close();
					bReader.close();
					client.close();
					mySocketLists.remove(client);
				} else {
					ClientCount.add();
					textarea.append("客户端登陆！ IP：" + cip + "  当前在线客户端" + ClientCount.getCount() + "个\n");
					System.out.println(fileNames);
					bWriter.write(fileNames);
					bWriter.newLine();
					bWriter.flush();
					textarea.append("给客户端返回数据！\n");
					client.shutdownOutput();
					bReader.close();
					bWriter.close();
					client.close();
					mySocketLists.remove(client);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				textarea.append("服务端写数据错误！！\n");
				System.out.println("server thread IOException");
			}
		}

		private void refresh(String passCode) throws IOException {
			textarea.append("获得密码:" + passCode + "\n");
			textarea.append(cip + "刷新信息\n");
			try {
				bWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8"));
				if (!password.equals(passCode)) {
					bWriter.write("codeerror");
					bWriter.newLine();
					bWriter.flush();
					textarea.append("通知客户端密码错误！\n");
					bWriter.close();
					bReader.close();
					client.close();
					mySocketLists.remove(client);
				} else {
					System.out.println(fileNames);
					bWriter.write(fileNames);
					bWriter.newLine();
					bWriter.flush();
					textarea.append("给客户端返回数据！\n");
					client.shutdownOutput();
					bReader.close();
					bWriter.close();
					client.close();
					mySocketLists.remove(client);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				textarea.append("服务端写数据错误！！\n");
				System.out.println("server thread IOException");
			}
		}
private void shutdown(String info )throws IOException{
	textarea.append("获得密码:" +info + "\n");
	textarea.append(cip + "请求关机\n");
	try {
		bWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8"));
		if (!password.equals(info)) {
			bWriter.write("codeerror");
			bWriter.newLine();
			bWriter.flush();
			textarea.append("通知客户端密码错误！\n");
			bWriter.close();
			bReader.close();
			client.close();
			mySocketLists.remove(client);
		} else {
			bWriter.write("shutdown");
			bWriter.newLine();
			bWriter.flush();
			textarea.append("通知客户端开始关机！\n");
			client.shutdownOutput();
			bReader.close();
			bWriter.close();
			client.close();
			Runtime.getRuntime().exec("cmd /c shutdown");
			Runtime.getRuntime().exec("cmd /c shutdowncomputer");
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		textarea.append("服务端写数据错误！！\n");
		System.out.println("server thread IOException");
	}
	try {
		Runtime.getRuntime().exec("cmd /c shutdown");
	} catch (IOException e) {
		e.printStackTrace();
	}
	
}
		public void run() {
			try {
				bReader = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
				msg = bReader.readLine();
			} catch (Exception e) {
				// TODO: handle exception
				textarea.append("读客户端错误!!\n");
			}
			textarea.append("获得命令：" + msg + "\n");
			String[] cmd = msg.split(",");
			action = cmd[0];
			info = cmd[1];
			switch (action) {
			case "login":
				try {
					login(info);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "logout":
				try {
					logout();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case "refresh":
				try {
					refresh(info);
				} catch (IOException e) {
					// TODO: handle exception
				}
				break;
			case "shutdown":
				try {
					shutdown(info);
				} catch (IOException e) {
					// TODO: handle exception
				}
				break;
			default:
				break;
			}
		}
	}
}
