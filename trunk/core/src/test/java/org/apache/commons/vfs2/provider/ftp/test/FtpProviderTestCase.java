/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.vfs2.provider.ftp.test;

import java.io.IOException;
import java.net.URL;

import junit.framework.Test;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.ftp.FtpFileProvider;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.ftp.FtpFileType;
import org.apache.commons.vfs2.test.AbstractProviderTestConfig;
import org.apache.commons.vfs2.test.ProviderTestConfig;
import org.apache.commons.vfs2.test.ProviderTestSuite;
import org.apache.commons.vfs2.util.FreeSocketPortUtil;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.junit.Assert;

/**
 * Tests for FTP file systems.
 */
public class FtpProviderTestCase extends AbstractProviderTestConfig implements ProviderTestConfig
{
    private static int SocketPort;

    /**
     * Use %40 for @ in URLs
     */
    private static String ConnectionUri;

    private static FtpServer Server;

    private static final String TEST_URI = "test.ftp.uri";

    private static final String USER_PROPS_RES = "org.apache.ftpserver/users.properties";

    static String getConnectionUri()
    {
        return ConnectionUri;
    }

    static int getSocketPort()
    {
        return SocketPort;
    }

    private static String getSystemTestUriOverride()
    {
        return System.getProperty(TEST_URI);
    }

    static void init() throws IOException
    {
        SocketPort = FreeSocketPortUtil.findFreeLocalPort();
        // Use %40 for @ in a URL
        ConnectionUri = "ftp://test:test@localhost:" + SocketPort;
    }

    /**
     * Creates and starts an embedded Apache FTP Server (MINA).
     *
     * @throws FtpException
     * @throws IOException
     */
    static void setUpClass() throws FtpException, IOException
    {
        if (Server != null)
        {
            return;
        }
        init();
        final FtpServerFactory serverFactory = new FtpServerFactory();
        final PropertiesUserManagerFactory propertiesUserManagerFactory = new PropertiesUserManagerFactory();
        final URL userPropsResource = ClassLoader.getSystemClassLoader().getResource(USER_PROPS_RES);
        Assert.assertNotNull(USER_PROPS_RES, userPropsResource);
        propertiesUserManagerFactory.setUrl(userPropsResource);
        final UserManager userManager = propertiesUserManagerFactory.createUserManager();
        final BaseUser user = (BaseUser) userManager.getUserByName("test");
        // Pickup the home dir value at runtime even though we have it set in the user prop file
        // The user prop file requires the "homedirectory" to be set
        user.setHomeDirectory(getTestDirectoryString());
        serverFactory.setUserManager(userManager);
        ListenerFactory factory = new ListenerFactory();
        // set the port of the listener
        factory.setPort(SocketPort);

        // replace the default listener
        serverFactory.addListener("default", factory.createListener());

        // start the server
        Server = serverFactory.createServer();
        Server.start();
    }

    /**
     * Creates the test suite for the ftp file system.
     */
    public static Test suite() throws Exception
    {
        return new ProviderTestSuite(new FtpProviderTestCase())
        {
            @Override
            protected void setUp() throws Exception
            {
                if (getSystemTestUriOverride() == null)
                {
                    setUpClass();
                }
                super.setUp();
            }

            @Override
            protected void tearDown() throws Exception
            {
                tearDownClass();
                super.tearDown();
            }
        };
    }

    /**
     * Stops the embedded Apache FTP Server (MINA).
     */
    static void tearDownClass()
    {
        if (Server != null)
        {
            Server.stop();
            Server = null;
        }
    }

    /**
     * Returns the base folder for tests. You can override the DEFAULT_URI by using the system property name defined by
     * TEST_URI.
     */
    @Override
    public FileObject getBaseTestFolder(final FileSystemManager manager) throws Exception
    {
        String uri = getSystemTestUriOverride();
        if (uri == null)
        {
            uri = ConnectionUri;
        }
        FileSystemOptions opts = new FileSystemOptions();
        final FtpFileSystemConfigBuilder builder = FtpFileSystemConfigBuilder.getInstance();
        builder.setPassiveMode(opts, true);
        // FtpFileType.BINARY is the default
        builder.setFileType(opts, FtpFileType.BINARY);
        builder.setConnectTimeout(opts, 10000);
        builder.setControlEncoding(opts, "UTF-8");
        return manager.resolveFile(uri, opts);
    }

    /**
     * Prepares the file system manager.
     */
    @Override
    public void prepare(final DefaultFileSystemManager manager) throws Exception
    {
        manager.addProvider("ftp", new FtpFileProvider());
    }
}
