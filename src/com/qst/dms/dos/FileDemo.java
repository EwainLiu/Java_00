package com.qst.dms.dos;

import java.util.ArrayList;
import java.util.Date;

import com.qst.dms.entity.DataBase;
import com.qst.dms.entity.LogRec;
import com.qst.dms.entity.MatchedLogRec;
import com.qst.dms.entity.MatchedTransport;
import com.qst.dms.entity.Transport;
import com.qst.dms.service.LogRecService;
import com.qst.dms.service.TransportService;

public class FileDemo {
    public static void main(String[] args) {
        // ����һ����־ҵ����
        LogRecService logService = new LogRecService();
        ArrayList<MatchedLogRec> matchLogs = new ArrayList<>();
        matchLogs.add(new MatchedLogRec(
                new LogRec(1001, new Date(), "�人",DataBase.GATHER, "zhangsan", "192.168.1.1", 1),
                new LogRec(1002, new Date(), "�人", DataBase.GATHER, "zhangsan",	"192.168.1.1", 0)));
        matchLogs.add(new MatchedLogRec(
                new LogRec(1003, new Date(), "����",DataBase.GATHER, "lisi", "192.168.1.6", 1),
                new LogRec(1004, new Date(), "����", DataBase.GATHER, "lisi", "192.168.1.6", 0)));
        matchLogs.add(new MatchedLogRec(
                new LogRec(1005, new Date(), "�Ϻ�",DataBase.GATHER, "wangwu", "192.168.1.89", 1),
                new LogRec(1006, new Date(), "�Ϻ�", DataBase.GATHER,	"wangwu", "192.168.1.89", 0)));
        //20191113����ƥ�����־��Ϣ���ļ���
        logService.saveAndAppendMatchLog(matchLogs);
        //logService.saveMatchLog(matchLogs);
        //����ƥ�����־��Ϣ�����ݿ���
        //logService.saveMatchLogToDB(matchLogs);
        //���ļ��ж�ȡƥ�����־��Ϣ
        ArrayList<MatchedLogRec> list1 = logService.readMatchLog();
        logService.showMatchLog(list1);

        // ����һ������ҵ����
        TransportService tranService = new TransportService();
        ArrayList<MatchedTransport> matchTrans = new ArrayList<>();
        matchTrans.add(new MatchedTransport(
                new Transport(2001, new Date(), "����",DataBase.GATHER,"zhangsan","zhaokel",1),
                new Transport(2002, new Date(), "����",DataBase.GATHER,"lisi","zhaokel",2),
                new Transport(2003, new Date(), "����",DataBase.GATHER,"wangwu","zhaokel",3)));
        matchTrans.add(new MatchedTransport(
                new Transport(2004, new Date(), "����",DataBase.GATHER,"maliu","zhaokel",1),
                new Transport(2005, new Date(), "����",DataBase.GATHER,"sunqi","zhaokel",2),
                new Transport(2006, new Date(), "����",DataBase.GATHER,"fengba","zhaokel",3)));
        //����ƥ���������Ϣ���ļ���
        //tranService.saveMatchedTransport(matchTrans);
        tranService.saveAndAppendMatchedTransport(matchTrans);
        //����ƥ���������Ϣ�����ݿ���
        //tranService.saveMatchTransportToDB(matchTrans);
        //���ļ��ж�ȡƥ���������Ϣ
        ArrayList<MatchedTransport> list2 = tranService.readMatchedTransport();
        tranService.showMatchTransport(list2);
    }
}