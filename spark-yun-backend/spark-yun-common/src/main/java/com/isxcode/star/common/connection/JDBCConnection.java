package com.isxcode.star.common.connection;

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConnection {
	public static Connection getConnection(String url, String username, String password, String driver,
			String className) throws Exception {
		if (driver != null && className != null) {
			dynamicLoadJdbc(driver, className);
		}

		return getConnection(url, username, password);
	}

	public static Connection getConnection(String url, String username, String password) throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}

	private static void dynamicLoadJdbc(String driverFile, String className) throws Exception {
		URL u = new URL("jar:file:" + driverFile + "!/");
		URLClassLoader ucl = new URLClassLoader(new URL[]{u});
		Driver driver = (Driver) Class.forName(className, true, ucl).newInstance();
		DriverShim driverShim = new DriverShim(driver, driverFile, className);
		DriverManager.registerDriver(driverShim);
	}

	private static void dynamicUnLoadJdbc(Driver driver) throws SQLException {
		DriverManager.deregisterDriver(driver);
	}

}
