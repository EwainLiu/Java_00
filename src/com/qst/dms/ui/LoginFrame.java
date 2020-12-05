package com.qst.dms.ui;

import com.qst.dms.entity.User;
import com.qst.dms.service.UserService;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;

public class LoginFrame extends JFrame {

	private JPanel contentPane;
	private JTextField txtUserName;
	private JTextField txtPassword;
	
	private User user;
	private UserService userService;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame frame = new LoginFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LoginFrame() {
		super("登录");
		//初始化很重要
		userService = new UserService();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 313, 212);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("\u7528\u6237\u540D\uFF1A");
		lblNewLabel.setFont(new Font("宋体", Font.BOLD, 14));
		lblNewLabel.setBounds(33, 22, 66, 29);
		contentPane.add(lblNewLabel);
		
		txtUserName = new JTextField();
		txtUserName.setBounds(101, 28, 154, 21);
		contentPane.add(txtUserName);
		txtUserName.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("\u5BC6\u7801\uFF1A");
		lblNewLabel_1.setFont(new Font("宋体", Font.BOLD, 14));
		lblNewLabel_1.setBounds(45, 79, 54, 15);
		contentPane.add(lblNewLabel_1);
		
		JButton btnNewButton = new JButton("\u767B\u5F55");
		btnNewButton.addActionListener(new LoginListener());
		btnNewButton.setBounds(10, 130, 73, 23);
		contentPane.add(btnNewButton);
		
		JButton btnReset = new JButton("\u91CD\u7F6E");
		btnReset.addActionListener(new ResetListener()); //重置
		btnReset.setBounds(117, 130, 73, 23);
		contentPane.add(btnReset);
		
		JButton btnNewButton_2 = new JButton("\u6CE8\u518C");
		btnNewButton_2.addActionListener(new RegistListener());
		btnNewButton_2.setBounds(216, 130, 73, 23);
		contentPane.add(btnNewButton_2);
		
		txtPassword = new JTextField();
		txtPassword.setColumns(10);
		txtPassword.setBounds(101, 76, 154, 21);
		contentPane.add(txtPassword);
	}
	
	
	//
	public class ReListener implements ActionListener {
		// 重置按钮的事件处理方法
		public void actionPerformed(ActionEvent e) {
			txtUserName.setText("");
			txtPassword.setText("");
		}
	}
	
	private class LoginListener implements ActionListener {		//登录
		public void actionPerformed(ActionEvent e) {
			//根据用户名查询用户
			user = userService.findUserByName(txtUserName.getText().trim());
			//判断输入的密码死否正确
			if(user!=null) {
				//判断输入的密码是否正确
				if(user.getPassword().equals(new String(txtPassword.getText().trim()))) {
					//登陆成功，隐藏登录窗口
					LoginFrame.this.setVisible(false);
					//显示主窗口
					new MainFrametest2();
				} else {
					//输出信息提示
					JOptionPane.showMessageDialog(null, "用户名或密码错误!请重新输入！","错误提示",JOptionPane.ERROR_MESSAGE);
					resetPwd();
				}
			}
		}
	}
	
	public class RegistListener implements ActionListener {
		// 重置按钮的事件处理方法
		public void actionPerformed(ActionEvent e) {
			LoginFrame.this.setVisible(false);
			new RegistFrame();
		}
	}
	
	public class ResetListener implements ActionListener {
		// 重置按钮的事件处理方法
		public void actionPerformed(ActionEvent e) {
			txtUserName.setText("");
			txtPassword.setText("");
		}
	}
	
	public void resetPwd() {
		txtPassword.setText("");
	}
}
