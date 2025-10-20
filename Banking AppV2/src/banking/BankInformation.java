package BankingApplication;

import java.util.Properties;
import java.util.Scanner;
import java.io.InputStream;
import java.sql.*;

public class BankInformation {

	private double accountBalance;
	private Scanner scan = new Scanner(System.in);
	//I set access to false so when we whan to check the info we can change it to true
	private boolean access = false; 
	private DatabaseManager bank = new DatabaseManager();

	public void start() {

		while(true) {
			System.out.println("Enter 1 to add new Account:"
					+ "\n2 to deposite to an existing Account:"
					+ "\n3 to withdrawn from an existing Account:"
					+ "\n4 to view the thisrmation about an Account:"
					+ "\n0 to exit from the server.");
			int ans = Integer.valueOf(scan.nextLine());

			if(ans == 0) { break; } // here we can terminate the while loop

			switch(ans) {
			case 1 :
				this.createAccount();
				break;
			case 2 :
				this.depositeMoney();
				break;
			case 3 :
				this.withdrawnMoney();
				break;
			case 4 :
				this.viewInformation();
				break;
			default:
				System.out.println("your responce is incoorect! please try again");
				break;
			}
		}
	}


	public void createAccount(){
		System.out.println("Enter Account HolderName:");
		String name = scan.nextLine();
		System.out.println("Enter Initial Balance:");
		this.accountBalance = Double.valueOf(scan.nextLine());

		String query = "insert into AccountsData(Name, Balance)"
				+ " values('"+ name +"', "+this.accountBalance+");";

		bank.executeQuery(query, access);
	}

	public void depositeMoney() {
		System.out.println("Enter account number:");
		int acNumber = Integer.valueOf(scan.nextLine());
		System.out.println("Enter the amount you want to deposite:");
		double amount = Double.valueOf(scan.nextLine());

		if(amount > 0) { // we cannot take negative number
			String query = "update AccountsData"
					+" set Balance = Balance + " + amount
					+" where AccountId = " + acNumber +";";

			bank.executeQuery(query, access);
		} else System.out.println("Enter sufficient amount");
	}

	public void withdrawnMoney() {
		System.out.println("Enter account number:");
		int acNumber = Integer.valueOf(scan.nextLine());
		System.out.println("Enter the amount you want to withdraw:");
		double amount = Double.valueOf(scan.nextLine());

		if(amount > 0) {
			String query = "select * from AccountsData"
					+" where AccountId= " + acNumber +";";
			//below line is the path where I save url, username and password of the connections
			String resourcePath = "BankingApplication/dbconfig.properties";
			try (InputStream fis =  DatabaseManager.class.getClassLoader().getResourceAsStream(resourcePath)){
				Properties props = new Properties();
				props.load(fis);

				String url = props.getProperty("db.url");
				String user = props.getProperty("db.username");
				String password = props.getProperty("db.password");{
					Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");//connecting to the database
					Connection con = DriverManager.getConnection(url, user, password);//
					Statement st = con.createStatement(); 
					ResultSet rs = st.executeQuery(query);//execute the statement or query
					rs.next(); //the pointer is pointing at border, we use next so ponter can point at values
					double accountBalance = rs.getDouble(3);//third value where we store balance
					//here we check if we have enough money to take from the bank account
					if(accountBalance >= amount) {
						//here we create a new query
						String query1 = "update AccountsData"
								+" set Balance = Balance - " + amount
								+" where AccountId = " + acNumber +";";
						bank.executeQuery(query1, access);
					} else {
						System.out.println("Insufficient amount");
					}

				}
			} catch(Exception e) {
				e.getStackTrace();
			}
		} else {
			System.out.println("Enter sufficient amount");
		}
	}

	public void viewInformation(){
		//here I set it to true 
		this.access = true;
		System.out.println("Enter account number:");
		int acNumber = Integer.valueOf(scan.nextLine()); 
		String query = "select * from AccountsData"
				+" where AccountId= " + acNumber +";";

		bank.executeQuery(query, access);
		/*
		 * When we want to access the data base we use selece query from SQL
		 * to execute that query we get a result result as String set so we assing it to ResultSet
		 * in other time we use Update queries
		 * when execute it we get result as int so we assign it to int check the result in if statement
		 */
		this.access = false;
	}
	public static void main(String[] args) {
		BankInformation bank = new BankInformation();
		bank.start();
	}
}
