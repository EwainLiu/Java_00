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

//��־ҵ����
public class LogRecService {
	// ��־���ݲɼ�
	public LogRec inputLog() {
		LogRec log = null;
		// ����һ���Ӽ��̽������ݵ�ɨ����
		Scanner scanner = new Scanner(System.in);
		try {
			// ��ʾ�û�����ID��ʶ
			System.out.println("������ID��ʶ��");
			// ���ռ������������
			int id = scanner.nextInt();
			// ��ȡ��ǰϵͳʱ��
			Date nowDate = new Date();
			// ��ʾ�û������ַ
			System.out.println("�������ַ��");
			// ���ռ���������ַ�����Ϣ
			String address = scanner.next();
			// ����״̬�ǡ��ɼ���
			int type = DataBase.GATHER;
			// ��ʾ�û������¼�û���
			System.out.println("�������û�����");
			// ���ռ���������ַ�����Ϣ
			String user = scanner.next();
			// ��ʾ�û���������IP
			System.out.println("����������IP��");
			// ���ռ���������ַ�����Ϣ
			String ip = scanner.next();
			while(!ipaddress(ip)){
				System.out.println("��������ȷ��IP��");
				ip = scanner.next();
			}
			// ��ʾ�û������¼״̬���ǳ�״̬
			System.out.println("�������¼״̬:1�ǵ�¼��0�ǵǳ�");
			int logType = scanner.nextInt();
			// ������־����
			log = new LogRec(id, nowDate, address, type, user, ip, logType);
		} catch (Exception e) {
			System.out.println("�ɼ�����־��Ϣ���Ϸ�");
		}
		// ������־����
		return log;
	}

	public static boolean ipaddress(String str){
		String[] s = str.split("\\.");
		if(s.length == 4){
			for(int i = 0; i < s.length; i++){
				char[] ch = s[i].toCharArray();
				boolean z = s[i].matches("[0-9]+");//�ж��ַ����Ƿ��Ǵ�����
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

	// ��־��Ϣ���
	public void showLog(LogRec... logRecs) {
		for (LogRec e : logRecs) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// ƥ����־��Ϣ������ɱ����
	public void showMatchLog(MatchedLogRec... matchLogs) {
		for (MatchedLogRec e : matchLogs) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// ƥ����־��Ϣ���,�����Ǽ���
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
			// ѭ�������������
			for (MatchedLogRec e : matchLogs) {
				if (e != null) {
					// �Ѷ���д�뵽�ļ���
					obs.writeObject(e);
					obs.flush();
				}
			}
			// �ļ�ĩβ����һ��null���󣬴����ļ�����
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
		/*		// ѭ�����ļ��еĶ���
			while ((matchLogRec = (MatchedLogRec) ois.readObject()) != null) {
				// ��������ӵ����ͼ�����
				matchLogRecs.add(matchLogRec);
			} */
			while (true) {
				try {
					//��������ӵ����ͼ�����
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
			//ѭ�������������
			for (MatchedLogRec e : matchLogRecs) {
				if (e != null) {
					//�Ѷ���д�뵽�ļ���
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

	//��ƥ����־���ݴ��浽���ݿ���
	public void saveMatchLogToDB(ArrayList<MatchedLogRec> matchLogs){
		DBUtil db = new DBUtil();
		try {
			db.getConnection();
			for(MatchedLogRec matchedLogRec : matchLogs){
				//��ȡ��¼��Ϣ
				LogRec login = matchedLogRec.getLogin();
				//��ȡ�ǳ���Ϣ
				LogRec logout = matchedLogRec.getLogout();
				//����ƥ���¼�еĵ�¼��־
				String sql = "INSERT INTO gather_logrec(id,TIME,address,TYPE,username,ip,logtype) VALUES(?,?,?,?,?,?,?)";
				Object[] param = new Object[]{ login.getId(),
						new Timestamp(login.getTime().getTime()),
						login.getAddress(), login.getType(), login.getUser(),
						login.getIp(), login.getLogType()
				};
				db.executeUpdate(sql,param);
				//����ƥ���¼�еĵǳ���־
				param = new Object[] { logout.getId(),
						new Timestamp(logout.getTime().getTime()),
						logout.getAddress(), logout.getType(), logout.getUser(),
						logout.getIp(), logout.getLogType()
				};
				db.executeUpdate(sql,param);
				//����ƥ���¼�е�ID
				sql = "INSERT INTO matched_logrec(loginid,logoutid) VALUES(?,?)";
				param = new Object[] { login.getId(), logout.getId() };
				db.executeUpdate(sql,param);
				JOptionPane.showMessageDialog(null, "ƥ�����־�����ѱ��浽�ļ������ݿ��У�","��ʾ",JOptionPane.INFORMATION_MESSAGE);
			}
			//�ر����ݿ����ӣ��ͷ���Դ
			db.closeAll();
		} catch (SQLException throwables) {
		} catch (IllegalAccessException e) {
		} catch (InstantiationException e) {
		} catch (ClassNotFoundException e) {
			JOptionPane.showConfirmDialog(null, "����ʧ�ܣ�","����",JOptionPane.ERROR_MESSAGE);
		}
	}

	//�����ݿ��ȡƥ����־��Ϣ
	public ArrayList<MatchedLogRec> readMatchedLogFromDB() {
		ArrayList<MatchedLogRec> matchedLogRecs = new ArrayList<MatchedLogRec>();
		DBUtil db = new DBUtil();
		try {
			//��ȡ���ݿ�����
			db.getConnection();
			//��ѯƥ�������
			String sql = "SELECT i.id,i.time,i.address,i.type,i.username,i.ip,i.logtype,"
					+ "o.id,o.time,o.address,o.type,o.username,o.ip,o.logtype "
					+ "FROM matched_logrec m,gather_logrec i,gather_logrec o "
					+ "WHERE m.loginid=i.id AND m.logoutid=o.id";
			ResultSet rs = db.executeQuery(sql,null);
			while(rs.next()){
				//��ȡ��¼��¼
				LogRec login = new LogRec(rs.getInt(1),rs.getDate(2),
						rs.getString(3),rs.getInt(4),rs.getString(5),
						rs.getString(6),rs.getInt(7));
				//��ȡ�ǳ���¼
				LogRec logout = new LogRec(rs.getInt(8),rs.getDate(9),
						rs.getString(10),rs.getInt(11),rs.getString(12),
						rs.getString(13),rs.getInt(14));
				//���ƥ���¼��Ϣ��ƥ�伯��
				MatchedLogRec matchedLog = new MatchedLogRec(login,logout);
				matchedLogRecs.add(matchedLog);
			}
			//�ر����ݿ�
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
			// ��ȡ���ݿ�����
			Connection conn=db.getConnection();
			// ��ѯƥ����־������ResultSet����ʹ�ó���next()֮��ķ������������
			Statement st=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			String sql = "SELECT * from gather_logrec";
			rs = st.executeQuery(sql);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	/*
	public ResultSet readLogResult() { 	//��ȡrs
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
	
