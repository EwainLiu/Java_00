package test;

import java.sql.ResultSet;
import java.sql.Statement;

import com.qst.dms.db.DBUtil;
import com.qst.dms.entity.User;
import com.qst.dms.service.UserService;

public class jdbc2 {
		public static void main(String[] args) {
			User user = new User();
			UserService userService = null;
			user = userService.findUserByName("abc");
			//user.getId();
			System.out.println(user.getId());
		}
}
