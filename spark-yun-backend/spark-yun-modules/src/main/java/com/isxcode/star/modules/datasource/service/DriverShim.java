/*
 * This file is part of dependency-check-core.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * Copyright (c) 2014 Jeremy Long. All Rights Reserved.
 */
/**
 * https://jeremylong.github.io/DependencyCheck/dependency-check-core/jacoco/org.owasp.dependencycheck.data.nvdcve/DriverShim.java.html.
 */
package com.isxcode.star.modules.datasource.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.Properties;

/**
 * <p>
 * Driver shim to get around the class loader issue with the DriverManager. The following code is a
 * nearly identical copy (with more comments and a few more methods implemented) of the DriverShim
 * from:
 * </p>
 * <blockquote>http://www.kfu.com/~nsayer/Java/dyn-jdbc.html</blockquote>
 *
 * @author Jeremy Long
 * @see Driver
 */
@Slf4j
public class DriverShim implements Driver {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DriverShim.class);
    /**
     * The database driver being wrapped.
     */
    private final Driver driver;

    /**
     * Constructs a new wrapper around a Driver.
     *
     * @param driver the database driver to wrap
     */
    public DriverShim(Driver driver) {
        this.driver = driver;
    }

    /**
     * Wraps the underlying driver's call to acceptsURL. Returns whether or not the driver can open a
     * connection to the given URL.
     *
     * @param url the URL of the database
     * @return true if the wrapped driver can connect to the specified URL
     * @throws SQLException thrown if there is an error connecting to the database
     * @see Driver#acceptsURL(String)
     */
    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return this.driver.acceptsURL(url);
    }

    /**
     * Wraps the call to the underlying driver's connect method.
     *
     * @param url the URL of the database
     * @param info a collection of string/value pairs
     * @return a Connection object
     * @throws SQLException thrown if there is an error connecting to the database
     * @see Driver#connect(String, Properties)
     */
    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return this.driver.connect(url, info);
    }

    /**
     * Returns the wrapped driver's major version number.
     *
     * @return the wrapped driver's major version number
     * @see Driver#getMajorVersion()
     */
    @Override
    public int getMajorVersion() {
        return this.driver.getMajorVersion();
    }

    /**
     * Returns the wrapped driver's minor version number.
     *
     * @return the wrapped driver's minor version number
     * @see Driver#getMinorVersion()
     */
    @Override
    public int getMinorVersion() {
        return this.driver.getMinorVersion();
    }

    /**
     * Wraps the call to the underlying driver's getParentLogger method.
     *
     * @return the parent's Logger
     * @throws SQLFeatureNotSupportedException thrown if the feature is not supported
     * @see Driver#getParentLogger()
     */
    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        // return driver.getParentLogger();
        final Method m;
        try {
            m = driver.getClass().getMethod("getParentLogger");
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new SQLFeatureNotSupportedException();
        }
        if (m != null) {
            try {
                return (java.util.logging.Logger) m.invoke(m);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                log.error(ex.getMessage(), ex);
                LOGGER.trace("", ex);
            }
        }
        throw new SQLFeatureNotSupportedException();
    }

    /**
     * Wraps the call to the underlying driver's getPropertyInfo method.
     *
     * @param url the URL of the database
     * @param info a collection of string/value pairs
     * @return an array of DriverPropertyInfo objects
     * @throws SQLException thrown if there is an error accessing the database
     * @see Driver#getPropertyInfo(String, Properties)
     */
    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return this.driver.getPropertyInfo(url, info);
    }

    /**
     * Returns whether or not the wrapped driver is jdbcCompliant.
     *
     * @return true if the wrapped driver is JDBC compliant; otherwise false
     * @see Driver#jdbcCompliant()
     */
    @Override
    public boolean jdbcCompliant() {
        return this.driver.jdbcCompliant();
    }

    /**
     * Standard implementation of hashCode.
     *
     * @return the hashCode of the object
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(7, 97).append(driver).toHashCode();
    }

    /**
     * Standard implementation of equals.
     *
     * @param obj the object to compare
     * @return returns true if the objects are equal; otherwise false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DriverShim)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        final DriverShim rhs = (DriverShim) obj;
        return new EqualsBuilder().append(driver, rhs.driver).isEquals();
    }

    /**
     * Standard implementation of toString().
     *
     * @return the String representation of the object
     */
    @Override
    public String toString() {
        return "DriverShim{" + "driver=" + driver + '}';
    }
}
