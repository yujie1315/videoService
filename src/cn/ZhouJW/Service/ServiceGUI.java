package cn.ZhouJW.Service;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServiceGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private JLabel label, label1;
	private JButton selectbutton, startbutton;
	private JTextField textfield, textfield1;
	private JTextArea textarea;
	private JScrollPane scroll;
	private BorderLayout mainLayout;
	private Container mainpanel;
	private JPanel panel;
	String dir = " ";
	private serverThread myServerThread;
	private boolean isStart = false;

	public ServiceGUI() {
		super();
		setTitle("服务器");
		this.setBounds(0, 0, 530, 390);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

				try {
					Runtime.getRuntime().exec("cmd /c shutdown");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					// return;
				}
				// shutdown
				System.exit(0);
			}
		});

		label = new JLabel();
		label.setText("视频目录");

		label1 = new JLabel();
		label1.setText("登陆密码:");

		textfield = new JTextField();
		textfield1 = new JTextField();

		textfield.setPreferredSize(new Dimension(200, 30));
		textfield1.setPreferredSize(new Dimension(200, 30));

		selectbutton = new JButton("浏览");
		startbutton = new JButton("启动");

		textarea = new JTextArea();
		textarea.setLineWrap(true);
		scroll = new JScrollPane(textarea);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		mainLayout = new BorderLayout();
		mainpanel = getContentPane();
		mainpanel.setLayout(mainLayout);

		panel = new JPanel();
		panel.add(label);
		panel.add(textfield);
		panel.add(selectbutton);

		JPanel panel1 = new JPanel();
		panel1.add(label1);
		panel1.add(textfield1);
		panel1.add(startbutton);

		JPanel panel2 = new JPanel();
		BorderLayout Layout1 = new BorderLayout();
		panel2.setLayout(Layout1);
		panel2.add(panel, Layout1.NORTH);
		panel2.add(panel1, Layout1.CENTER);

		mainpanel.add(panel2, mainLayout.NORTH);
		mainpanel.add(scroll, mainLayout.CENTER);

		selectbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					dir = fileChooser.getSelectedFile().getAbsolutePath();
					textfield.setText(dir);
				}
			}
		});

		startbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (textfield.getText().length() < 1) {
					JOptionPane.showMessageDialog(null, "文件夹不能为空", "错误", JOptionPane.ERROR_MESSAGE);
					return;
				} else if (textfield1.getText().length() < 1) {
					JOptionPane.showMessageDialog(null, "密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
					return;
				} else if (textfield1.getText().length() > 5) {
					JOptionPane.showMessageDialog(null, "密码长度最大为5", "错误", JOptionPane.ERROR_MESSAGE);
					return;
				}

				ArrayList<String> fileNames = new ArrayList<String>();
				StringBuffer stringbuffer = new StringBuffer();
				stringbuffer.append(textfield.getText() + ",");

				textarea.append("启动中...\n");
				if (getFileName(textfield.getText(), fileNames) == 0) {
					JOptionPane.showMessageDialog(null, "错误", "文件夹有误", JOptionPane.ERROR_MESSAGE);
					return;
				}

				textarea.append("视频文件扫描...\n");
				for (int i = 0; i < fileNames.size(); i++) {

					textarea.append("\t" + fileNames.get(i) + "\n");
					stringbuffer.append(fileNames.get(i) + ",");
				}
				textarea.append("视频文件数量：" + fileNames.size() + "\n");

				if (!isStart) {

					myServerThread = new serverThread(stringbuffer.toString(), textarea, textfield1.getText());
					myServerThread.start();
					new UdpReceiveAndTcpSend().start();

					try {
						textarea.append("注册Tomcat服务...\n");
						Runtime.getRuntime().exec("cmd /c startup");
					} catch (IOException e) {
						e.printStackTrace();
					}
						isStart = true;
				} else {
					myServerThread.setFileNames(stringbuffer.toString());
				}
			}
		});
	}

	private int getFileName(String DirPath, ArrayList<String> fileNames) {

		File file = new File(DirPath);
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {

			if (!files[i].isDirectory()) {

				fileNames.add(files[i].getName());
			}

		}

		return fileNames.size();
	}

	public static void main(String[] args) {

		ServiceGUI sGUI = new ServiceGUI();

		sGUI.setVisible(true);

	}

}
