package database;

import java.sql.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import java.lang.StringBuffer;

public class Database {
	
	// Класс соединения с БД
	// Из внешнего кода не трогать, использовать только обёрточные функции
	private Connection connection;
	
	// Имя ходовой БД
	private String dbName;
	
	/**
	*  Get name of database with bot data
	*  No parameters
	*  Returns nothing
	*/
	// Имя базы данных, чтобы вставлять его в запросы
	public String getDBName() { return dbName; }
	
	/**
	*  Set up connection with database in this object.
	*  Creates connection with given host on given user.
	*  
	*  @param url - database host
	*  @param username - database user name
	*  @param password - database user password
	*  
	*  Returns nothing
	*  
	*  @throws ClassNotFoundException
	*  @throws SQLException
	*/
	// Установить соединение с БД
	public void connect(String url, String username, String password) throws ClassNotFoundException, SQLException {
		connection = DriverManager.getConnection(url, username, password);
	}
	
	/**
	*  Set up connection with database in this object.
	*  Reads configuration file db_connection.cfg
	*  from main/resources folder and creates connection
	*  with given host on given user.
	*  
	*  No parameters
	*  Returns nothing
	*  
	*  @throws IOException
	*  @throws ClassNotFoundException
	*  @throws SQLException
	*/
	// Установить соединение с БД (автоматически по данным из конфига)
	public void connect() throws IOException, ClassNotFoundException, SQLException {
		// Конфигурация соединения
		Properties prop = new Properties();
		// Читаю файл с конфигами
		prop.load(new FileInputStream("resources/db_connection.cfg"));
		// Назначить драйвер базы данных
		Class.forName(prop.getProperty("driver").toString());
		
		connect(
			prop.getProperty("host").toString(), 
			prop.getProperty("username").toString(), 
			prop.getProperty("password").toString()
		);
		
		dbName = prop.getProperty("dbname").toString();
		
		// System.out.println("CONNECTION: " + connection);
	}
	
	/**
	*  Close connection with database
	*  
	*  No parameters
	*  Returns nothing
	*  
	*  @throws SQLException
	*/
	// Завершить сеанс соединения с базой данных
	public void disconnect() throws SQLException {
		if (isActive()) connection.close();
	}
	
	/**
	*  Checks, if this connection is open and valid.
	*  Warning! Executes query to dataabse server,
	*  don't use it often.
	*  
	*  @return boolean value. If this connection is open and valid
	*  
	*  @throws SQLException
	*/
	// Проверка, активно ли соединение
	public boolean isActive() {
		try { return connection != null && !connection.isClosed() && connection.isValid(10); }
		catch (SQLException e) { return false; }
	}
	
	/**
	*  Executes database query
	*  (using JDBC driver)
	*  
	*  @param query - string with query to database
	*  
	*  @return set of results (ResultSet). DB answer.
	*  
	*  @throws SQLException
	*/
	// Запрос к базе данных
	// Возвращает набор значений - ответ БД на запрос
	public ResultSet returnQuery(String query) throws SQLException {
		
		Statement stmt = connection.createStatement();
		ResultSet res = stmt.executeQuery(query);
		// if (stmt != null) stmt.close();
		
		return res;
	}
	
	/**
	*  Executes database query without answer
	*  (using JDBC driver)
	*  
	*  @param query - string with query to database
	*  
	*  Returns nothing
	*  
	*  @throws SQLException
	*/
	// Запрос к базе данных (команда)
	// Без ответа
	public void simpleQuery(String query) throws SQLException {
		returnQuery(query);
	}
	
	/*=========================================================================================*/
	
	// Бото-специфичные функции
	
	/**
	*  Add new user to `users` database
	*  @param userID - user id from Telegram
	*  @param chatID - chat ID from Telegram
	*  @param username - name to identify user
	*  
	*  @return true if user was added successfully, false if not.
	*/
	// Добавление пользователя
	public boolean addUser(long userID, long chatID, String userName) {
		StringBuffer sb = new StringBuffer();
		sb
			.append("INSERT INTO `")
			.append(dbName)
			.append("`.`users` (`id`, `chat`, `name`, `active_first`, `active_last`) VALUES (")
			.append(Long.toString(userID))
			.append(", ")
			.append(Long.toString(chatID))
			.append(", '")
			.append(userName)
			.append("', NOW(), NOW());");
		
		try {
			simpleQuery(sb.toString());
			return true;
		}
		catch (Exception e) {
			System.out.println("SQL query error.\n" + sb.toString());
			return false;
		}
	}
	
	/**
	*  Remove user information from `users` and `subscriptions` databases
	*  @param userID - user id from Telegram
	*  @return true if everything was deleted successfully, false if not.
	*/
	// Удаление пользователя
	public boolean removeUser(long userID) {
		StringBuffer sb = new StringBuffer();
		sb
			.append("DELETE FROM `")
			.append(dbName)
			.append("`.`users` WHERE `id` = ")
			.append(Long.toString(userID))
			.append(";");
		
		try {
			simpleQuery(sb.toString());
		}
		catch (Exception e) {
			System.out.println("SQL query error.\n" + sb.toString());
			return false;
		}
		
		sb.setLength(0);
		
		sb
			.append("DELETE FROM `")
			.append(dbName)
			.append("`.`subscriptions` WHERE `usr` = ")
			.append(Long.toString(userID))
			.append(";");
		
		try {
			simpleQuery(sb.toString());
		}
		catch (Exception e) {
			System.out.println("SQL query error.\n" + sb.toString());
			return false;
		}
		
		return true;
	}
	
	/**
	*  Get all chat IDs in database
	*  No parameters
	*  
	*  @return List of Long integers. Maybe empty.
	*/
	// Получить список всех Chat ID
	public List<Long> getAllChatIDs() {
		StringBuffer sb = new StringBuffer();
		sb
			.append("SELECT `chat` FROM ")
			.append(dbName)
			.append("`.`users`;");
		
		List<Long> resultlist = new ArrayList<Long>();
		
		try {
			ResultSet res = returnQuery(sb.toString());
			
			while(res.next()) {
				resultlist.add(res.getLong("chat"));
			}
		}
		catch (SQLException e) { System.out.println("SQL query error.\n" + sb.toString()); }
		
		return resultlist;
	}
	
	// Set<Long> getChatIDs()
}
