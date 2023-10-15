package com.isxcode.star.common.connection;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class DriverShim implements Driver {
	private final Driver driver;

	private final String fileName;

	private final String className;
	DriverShim(Driver d, String fileName, String className) {
		this.driver = d;
		this.fileName = fileName;
		this.className = className;
	}

	public Driver getDriver() {
		return driver;
	}

	public String getFileName() {
		return fileName;
	}

	public String getClassName() {
		return className;
	}

	public boolean acceptsURL(String u) throws SQLException {
		return this.driver.acceptsURL(u);
	}
	public Connection connect(String u, Properties p) throws SQLException {
		return this.driver.connect(u, p);
	}
	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		return this.driver.getPropertyInfo(url, info);
	}
	@Override
	public int getMajorVersion() {
		return this.driver.getMajorVersion();
	}
	@Override
	public int getMinorVersion() {
		return this.driver.getMinorVersion();
	}
	@Override
	public boolean jdbcCompliant() {
		return this.driver.jdbcCompliant();
	}
	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return this.driver.getParentLogger();
	}
}