package com.capynotes.authservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.*;

@SpringBootApplication
public class AuthServiceApplication {
	@Value("${spring.datasource.username}")
	private static String userName;

	@Value("${spring.datasource.password}")
	private static String password;

	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(AuthServiceApplication.class);
		Connection connection = null;
		Statement statement = null;
		createDB(logger, connection, statement);
		SpringApplication.run(AuthServiceApplication.class, args);
	}

	private static void createDB(Logger logger, Connection connection, Statement statement) {
		try {
			logger.debug("Creating database if not exist...");
			connection = DriverManager.getConnection("jdbc:postgresql://db:5432/", "postgres", "admin");
			statement = connection.createStatement();
			statement.executeQuery("SELECT count(*) FROM pg_database WHERE datname = 'authservicedb'");
			ResultSet resultSet = statement.getResultSet();
			resultSet.next();
			int count = resultSet.getInt(1);

			if (count <= 0) {
				statement.executeUpdate("CREATE DATABASE authservicedb");
				logger.debug("Database created.");
			} else {
				logger.debug("Database already exist.");
			}
		} catch (SQLException e) {
			logger.error(e.toString());
		} finally {
			try {
				if (statement != null) {
					statement.close();
					logger.debug("Closed Statement.");
				}
				if (connection != null) {
					logger.debug("Closed Connection.");
					connection.close();
				}
			} catch (SQLException e) {
				logger.error(e.toString());
			}
		}
	}

}
