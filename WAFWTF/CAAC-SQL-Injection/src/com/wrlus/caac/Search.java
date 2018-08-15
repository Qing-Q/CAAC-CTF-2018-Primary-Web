package com.wrlus.caac;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.Scanner;

public class Search {
	
	public static String doSearch(String air) throws SecurityException {
		if(null==air || air.equals("")) {
			return "";
		}
		if(doWaf(air)==false) {
			throw new SecurityException("很抱歉，您提交的参数可能具有攻击行为，已被系统管理员设置拦截。");
		}
		Properties dbproperties = new Properties();
		InputStream dbconfig = Search.class.getResourceAsStream("/config/config.properties");
		try {
			dbproperties.load(dbconfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = (Connection)DriverManager.getConnection("jdbc:mysql://127.0.0.1/caac", dbproperties);
			String sql = "select name,details from air where id = "+air;
			System.out.println(sql);
			PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet resultSet = ps.executeQuery();
			String result = "";
			while(resultSet.next()==true) {
				result += resultSet.getString(1)+" ";
				result += resultSet.getString(2)+" ";
				result += "<br>";
			}
			return result;
		} catch (Exception e) {
			return e.getLocalizedMessage();
		}
	}
	
	public static boolean doWaf(String air) {
		Scanner waffile = new Scanner(Search.class.getResourceAsStream("/config/waf.txt"));
		while(waffile.hasNextLine()==true) {
			String wafkeyword = waffile.nextLine();
			if (air.contains(wafkeyword)) {
				waffile.close();
				return false;
			}
		}
		waffile.close();
		return true;
	}
	
}
