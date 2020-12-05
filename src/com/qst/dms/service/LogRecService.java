package com.qst.dms.service;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javax.swing.JOptionPane;

import com.qst.dms.db.DBUtil;
import com.qst.dms.entity.*;

//日志业务类
public class LogRecService {
	// 日志数据采集
	public LogRec inputLog() {
		LogRec log = null;
		// 建立一个从键盘接收数据的扫描器
		Scanner scanner = new Scanner(System.in);
		try {
			// 提示用户输入ID标识
			System.out.println("请输入ID标识：");
			// 接收键盘输入的整数
			int id = scanner.nextInt();
			// 获取当前系统时间
			Date nowDate = new Date();
			// 提示用户输入地址
			System.out.println("请输入地址：");
			// 接收键盘输入的字符串信息
			String address = scanner.next();
			// 数据状态是“采集”
			int type = DataBase.GATHER;
			// 提示用户输入登录用户名
			System.out.println("请输入用户名：");
			// 接收键盘输入的字符串信息
			String user = scanner.next();
			// 提示用户输入主机IP
			System.out.println("请输入主机IP：");
			// 接收键盘输入的字符串信息
			String ip = scanner.next();
			while(!ipaddress(ip)){
				System.out.println("请输入正确的IP：");
				ip = scanner.next();
			}
			// 提示用户输入登录状态、登出状态
			System.out.println("请输入登录状态:1是登录，0是登出");
			int logType = scanner.nextInt();
			// 创建日志对象
			log = new LogRec(id, nowDate, address, type, user, ip, logType);
		} catch (Exception e) {
			System.out.println("采集的日志信息不合法");
		}
		// 返回日志对象
		return log;
	}

	public static boolean ipaddress(String str){
		String[] s = str.split("\\.");
		if(s.length == 4){
			for(int i = 0; i < s.length; i++){
				char[] ch = s[i].toCharArray();
				boolean z = s[i].matches("[0-9]+");//判断字符串是否是纯数字
				if(z == true){
					int n = Integer.parseInt(s[i]);
					if(n<0 || n>255){
						return false;
					}
				}else if(z == false){
					return  false;
				}
			}
		}else{
			return false;
		}
		return true;
	}

	// 日志信息输出
	public void showLog(LogRec... logRecs) {
		for (LogRec e : logRecs) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// 匹配日志信息输出，可变参数
	public void showMatchLog(MatchedLogRec... matchLogs) {
		for (MatchedLogRec e : matchLogs) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// 匹配日志信息输出,参数是集合
	public void showMatchLog(ArrayList<MatchedLogRec> matchLogs) {
		for (MatchedLogRec e : matchLogs) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	public void saveMatchLog(ArrayList<MatchedLogRec> matchLogs) {
		try (ObjectOutputStream obs = new ObjectOutputStream(
				new FileOutputStream("MatchedLogs.txt", true))) {
			// 循环保存对象数据
			for (MatchedLogRec e : matchLogs) {
				if (e != null) {
					// 把对象写入到文件中
					obs.writeObject(e);
					obs.flush();
				}
			}
			// 文件末尾保存一个null对象，代表文件结束
			obs.writeObject(null);
			obs.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public ArrayList<MatchedLogRec> readMatchLog() {
		ArrayList<MatchedLogRec> matchLogs = new ArrayList<>();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				"MatchedLogs.txt"))) {
			MatchedLogRec matchLog;
		/*		// 循环读文件中的对象
			while ((matchLogRec = (MatchedLogRec) ois.readObject()) != null) {
				// 将对象添加到泛型集合中
				matchLogRecs.add(matchLogRec);
			} */
			while (true) {
				try {
					//将对象添加到泛型集合中
					matchLog = (MatchedLogRec) ois.readObject();
					matchLogs.add(matchLog);
				} catch (EOFException ex) {
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return matchLogs;
	}

	public void saveAndAppendMatchLog(ArrayList<MatchedLogRec> matchLogRecs) {
		AppendObjectOutputStream aoos = null;
		File file = new File("MatchedLogs.txt");
		try {
			AppendObjectOutputStream.file = file;
			aoos = new AppendObjectOutputStream(file);
			//循环保存对象数据
			for (MatchedLogRec e : matchLogRecs) {
				if (e != null) {
					//把对象写入到文件中
					aoos.writeObject(e);
					aoos.flush();
				}
			}
		} catch (Exception ex) {
		} finally {
			if (aoos != null) {
				try {aoos.close();} catch (IOException e) {e.printStackTrace();}
			}
		}
	}

	//将匹配日志数据储存到数据库中
	public void saveMatchLogToDB(ArrayList<MatchedLogRec> matchLogs){
		DBUtil db = new DBUtil();
		try {
			db.getConnection();
			for(MatchedLogRec matchedLogRec : matchLogs){
				//获取登录信息
				LogRec login = matchedLogRec.getLogin();
				//获取登出信息
				LogRec logout = matchedLogRec.getLogout();
				//保存匹配记录中的登录日志
				String sql = "INSERT INTO gather_logrec(id,TIME,address,TYPE,username,ip,logtype) VALUES(?,?,?,?,?,?,?)";
				Object[] param = new Object[]{ login.getId(),
						new Timestamp(login.getTime().getTime()),
						login.getAddress(), login.getType(), login.getUser(),
						login.getIp(), login.getLogType()
				};
				db.executeUpdate(sql,param);
				//保存匹配记录中的登出日志
				param = new Object[] { logout.getId(),
						new Timestamp(logout.getTime().getTime()),
						logout.getAddress(), logout.getType(), logout.getUser(),
						logout.getIp(), logout.getLogType()
				};
				db.executeUpdate(sql,param);
				//保存匹配记录中的ID
				sql = "INSERT INTO matched_logrec(loginid,logoutid) VALUES(?,?)";
				param = new Object[] { login.getId(), logout.getId() };
				db.executeUpdate(sql,param);
				JOptionPane.showMessageDialog(null, "匹配的日志数据已保存到文件和数据库中！","提示",JOptionPane.INFORMATION_MESSAGE);
			}
			//关闭数据库连接，释放资源
			db.closeAll();
		} catch (SQLException throwables) {
		} catch (IllegalAccessException e) {
		} catch (InstantiationException e) {
		} catch (ClassNotFoundException e) {
			JOptionPane.showConfirmDialog(null, "保存失败！","错误",JOptionPane.ERROR_MESSAGE);
		}
	}

	//从数据库读取匹配日志信息
	public ArrayList<MatchedLogRec> readMatchedLogFromDB() {
		ArrayList<MatchedLogRec> matchedLogRecs = new ArrayList<MatchedLogRec>();
		DBUtil db = new DBUtil();
		try {
			//获取数据库连接
			db.getConnection();
			//查询匹配的物流
			String sql = "SELECT i.id,i.time,i.address,i.type,i.username,i.ip,i.logtype,"
					+ "o.id,o.time,o.address,o.type,o.username,o.ip,o.logtype "
					+ "FROM matched_logrec m,gather_logrec i,gather_logrec o "
					+ "WHERE m.loginid=i.id AND m.logoutid=o.id";
			ResultSet rs = db.executeQuery(sql,null);
			while(rs.next()){
				//获取登录记录
				LogRec login = new LogRec(rs.getInt(1),rs.getDate(2),
						rs.getString(3),rs.getInt(4),rs.getString(5),
						rs.getString(6),rs.getInt(7));
				//获取登出记录
				LogRec logout = new LogRec(rs.getInt(8),rs.getDate(9),
						rs.getString(10),rs.getInt(11),rs.getString(12),
						rs.getString(13),rs.getInt(14));
				//添加匹配登录信息到匹配集合
				MatchedLogRec matchedLog = new MatchedLogRec(login,logout);
				matchedLogRecs.add(matchedLog);
			}
			//关闭数据库
			db.closeAll();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return matchedLogRecs;
	}
	

	public ResultSet readLogResult() {		
		DBUtil db = new DBUtil();
		ResultSet rs=null;
		try {
			// 获取数据库链接
			Connection conn=db.getConnection();
			// 查询匹配日志，设置ResultSet可以使用除了next()之外的方法操作结果集
			Statement st=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			String sql = "SELECT * from gather_logrec";
			rs = st.executeQuery(sql);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	/*
	public ResultSet readLogResult() { 	//获取rs
		DBUtil db = new DBUtil();
		ResultSet rs = null;
		try {
			db.getConnection();
			String sql = "SELECT i.id,i.time,i.address,i.type,i.username,i.ip,i.logtype,"
					+ "o.id,o.time,o.address,o.type,o.username,o.ip,o.logtype "
					+ "FROM matched_logrec m,gather_logrec i,gather_logrec o "
					+ "WHERE m.loginid=i.id AND m.logoutid=o.id";
			rs = db.executeQuery(sql,null);
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(rs==null)
			System.out.println("1");
		System.out.println("2");
		return rs;
	}
	 */
	
}
	
