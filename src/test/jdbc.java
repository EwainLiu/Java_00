package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class jdbc {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        //���ݿ������ַ���
        String url = "jdbc:mysql://localhost:3306/misaki?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
        String user = "root";
        String password = "Ly993658396";
        Connection con = DriverManager.getConnection(url, user,password);
        //����������
        Statement stmt = con.createStatement();

        String sql = "insert into temp1 values('003','li','�Ϻ�')";
        String sql2 = "delete from temp1 where id = '003'";
        stmt.executeUpdate(sql);
    }

}
