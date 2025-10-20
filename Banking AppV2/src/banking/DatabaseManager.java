package BankingApplication;


import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DatabaseManager {

	public void executeQuery(String query, boolean access) {
		Properties props = new Properties();
		// Path to the file
		String resourcePath = "BankingApplication/dbconfig.properties";
		try (InputStream fis =  DatabaseManager.class.getClassLoader().getResourceAsStream(resourcePath)){
			props.load(fis);

			String url = props.getProperty("db.url");
			String user = props.getProperty("db.username");
			String pass = props.getProperty("db.password");

			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Connection con = DriverManager.getConnection(url, user, pass);
			Statement st = con.createStatement();
			con.setAutoCommit(false);

			if (access) {
				ResultSet rs = st.executeQuery(query);
				if (rs.next()) {
					//first is account number
					String acNumber = rs.getString(1);
					//second is account name
					String name = rs.getString(2);
					//third is balance
					double balance = rs.getDouble(3);
					//last is time of the database table
					Timestamp time = rs.getTimestamp("createdAt");
					//here we print that 
					System.out.println("Account: " + acNumber + "\nname of the account holder: " + name +
							".\nBalance: " + balance + ".\nDate: " + time);
				}
				rs.close();
			} else {
				int number = st.executeUpdate(query);
				if (number > 0) {
					con.commit();
					System.out.println("Action is successful!");
				} else {
					con.rollback();
					System.out.println("Action failed!");
				}
			}

			con.close();
			st.close();

		} catch (IOException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
}