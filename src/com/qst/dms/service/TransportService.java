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

import com.mysql.cj.protocol.Resultset;
import com.qst.dms.db.DBUtil;
import com.qst.dms.entity.*;

public class TransportService {
	// �������ݲɼ�
	public Transport inputTransport() {
		Transport trans = null;

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
			System.out.println("��������ﾭ���ˣ�");
			// ���ռ���������ַ�����Ϣ
			String handler = scanner.next();
			// ��ʾ�û���������IP
			System.out.println("������ �ջ���:");
			// ���ռ���������ַ�����Ϣ
			String reciver = scanner.next();
			// ��ʾ������������״̬
			System.out.println("����������״̬��1�����У�2�ͻ��У�3��ǩ��");
			// ��������״̬
			int transportType = scanner.nextInt();
			// ����������Ϣ����
			trans = new Transport(id, nowDate, address, type, handler, reciver,
					transportType);
		} catch (Exception e) {
			System.out.println("�ɼ�����־��Ϣ���Ϸ�");
		}
		// ������������
		return trans;
	}

	// ������Ϣ���
	public void showTransport(Transport... transports) {
		for (Transport e : transports) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// ƥ���������Ϣ������ɱ����
	public void showMatchTransport(MatchedTransport... matchTrans) {
		for (MatchedTransport e : matchTrans) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// ƥ���������Ϣ����������Ǽ���
	public void showMatchTransport(ArrayList<MatchedTransport> matchTrans) {
		for (MatchedTransport e : matchTrans) {
			if (e != null) {
				System.out.println(e.toString());
			}
		}
	}

	// ƥ��������Ϣ���棬�����Ǽ���
	public void saveMatchedTransport(ArrayList<MatchedTransport> matchTrans) {
		// ����һ��ObjectOutputStream������������������ļ������
		// �Կ�׷�ӵķ�ʽ�����ļ�����������ݱ��浽MatchedTransports.txt�ļ���
		try (ObjectOutputStream obs = new ObjectOutputStream(
				new FileOutputStream("MatchedTransports.txt", true))) {
			// ѭ�������������
			for (MatchedTransport e : matchTrans) {
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

	// ��ƥ��������Ϣ���棬�����Ǽ���
	public ArrayList<MatchedTransport> readMatchedTransport() {
		ArrayList<MatchedTransport> matchTrans = new ArrayList<>();
		// ����һ��ObjectInputStream�������������������ļ�����������MatchedTransports.txt�ļ���
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				"MatchedTransports.txt"))) {
			MatchedTransport matchTran;
		/*	// ѭ�����ļ��еĶ���
			while ((matchTran = (MatchedTransport) ois.readObject()) != null) {
				// ��������ӵ����ͼ�����
				matchTrans.add(matchTran);
			}*/
			while (true) {
				try {
					//��������ӵ����ͼ�����
					matchTran = (MatchedTransport) ois.readObject();
					matchTrans.add(matchTran);
				} catch (EOFException ex) {
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return matchTrans;
	}

	public void saveAndAppendMatchedTransport(ArrayList<MatchedTransport> matchTrans) {
		AppendObjectOutputStream aoos = null;
		File file = new File("MatchedTransports.txt");
		try {
			AppendObjectOutputStream.file = file;
			aoos = new AppendObjectOutputStream(file);
			//ѭ�������������
			for (MatchedTransport e : matchTrans) {
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

	//���������ݴ��浽���ݿ���
	public void saveMatchTransportToDB(ArrayList<MatchedTransport> matchTrans){
		DBUtil db = new DBUtil();
		try {
			db.getConnection();
			for(MatchedTransport matchedTransport : matchTrans){
				//��ȡƥ��ķ�������
				Transport send = matchedTransport.getSend();
				//��ȡƥ�����������
				Transport trans = matchedTransport.getTrans();
				//��ȡƥ��Ľ�������
				Transport receive = matchedTransport.getReceive();
				//����ƥ���¼�еķ���״̬
				String sql = "insert into gather_transport(id,time,address,type,handler,reciver,transporttype) values(?,?,?,?,?,?,?)";
				Object[] param = new Object[] { send.getId(),
						new Timestamp(send.getTime().getTime()),
						send.getAddress(), send.getType(), send.getHandler(),
						send.getReciver(), send.getTransportType()
				};
				db.executeUpdate(sql,param);
				//����ƥ���¼�е�����״̬
				param = new Object[] { trans.getId(),
						new Timestamp(trans.getTime().getTime()),
						trans.getAddress(), trans.getType(), trans.getHandler(),
						trans.getReciver(), trans.getTransportType()
				};
				db.executeUpdate(sql,param);
				//����ƥ���¼�еĽ���״̬
				param = new Object[] { receive.getId(),
						new Timestamp(receive.getTime().getTime()),
						receive.getAddress(), receive.getType(), receive.getHandler(),
						receive.getReciver(), receive.getTransportType()
				};
				db.executeUpdate(sql,param);
				//����ƥ����־��ID
				sql = "INSERT INTO matched_transport(sendid,transid,receiveid) VALUES(?,?,?)";
				param = new Object[]{
						send.getId(),trans.getId(),receive.getId()
				};
				db.executeUpdate(sql,param);
				JOptionPane.showMessageDialog(null, "ƥ�����־�����ѱ��浽�ļ������ݿ��У�","��ʾ",JOptionPane.INFORMATION_MESSAGE);
			}
			//�ر����ݿ����ӣ��ͷ���Դ
			db.closeAll();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			JOptionPane.showConfirmDialog(null, "����ʧ�ܣ�","����",JOptionPane.ERROR_MESSAGE);
		}
	}

	//�����ݿ��ж�ȡ������Ϣ
	public ArrayList<MatchedTransport> readMatchedTransportFromDB() {
		ArrayList<MatchedTransport> matchedTransports = new ArrayList<MatchedTransport>();
		DBUtil db = new DBUtil();
		try {
			//��ȡ���ݿ�����
			db.getConnection();
			//��ѯƥ�������
			String sql = "SELECT s.id,s.time,s.address,s.type,s.handler,s.reciver,s.transporttype,"
					+ "t.id,t.time,t.address,t.type,t.handler,t.reciver,t.transporttype,"
					+ "r.id,r.time,r.address,r.type,r.handler,r.reciver,r.transporttype "
					+ "FROM matched_transport m,gather_transport s,gather_transport t,gather_transport r "
					+ "WHERE m.sendid=s.id AND m.transid=t.id AND m.receiveid=r.id";
			ResultSet rs = db.executeQuery(sql,null);
			while(rs.next()){
				//��ȡ���ͼ�¼
				Transport send = new Transport(rs.getInt(1),rs.getDate(2),
						rs.getString(3),rs.getInt(4),rs.getString(5),
						rs.getString(6),rs.getInt(7));
				//��ȡ�����¼
				Transport trans = new Transport(rs.getInt(8),rs.getDate(9),
						rs.getString(10),rs.getInt(11),rs.getString(12),
						rs.getString(13),rs.getInt(14));
				//��ȡ���ռ�¼
				Transport receive = new Transport(rs.getInt(15),rs.getDate(16),
						rs.getString(17),rs.getInt(18),rs.getString(19),
						rs.getString(20),rs.getInt(21));
				//���ƥ���¼��Ϣ��ƥ�伯��
				MatchedTransport matchedTrans = new MatchedTransport(send,trans,receive);
				matchedTransports.add(matchedTrans);
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
		return matchedTransports;
	}
	
	public ResultSet readTransResult() {		
		DBUtil db = new DBUtil();
		ResultSet rs=null;
		try {
			// ��ȡ���ݿ�����
			Connection conn=db.getConnection();
			// // ��ѯƥ������������ResultSet����ʹ�ó���next()֮��ķ������������
			Statement st=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			String sql = "SELECT * from gather_transport";
			rs = st.executeQuery(sql);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
}