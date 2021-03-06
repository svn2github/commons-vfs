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
package org.apache.commons.vfs.provider.http;

import org.apache.commons.vfs.FileSystemConfigBuilder;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.UserAuthenticator;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

/**
 * Configuration options for HTTP
 *
 * @author <a href="mailto:imario@apache.org">Mario Ivankovits</a>
 * @version $Revision$ $Date$
 * @deprecated Use HttpFileSystemOptions instead.
 */
public class HttpFileSystemConfigBuilder extends FileSystemConfigBuilder
{
    private final static HttpFileSystemConfigBuilder builder = new HttpFileSystemConfigBuilder();

    public static HttpFileSystemConfigBuilder getInstance()
    {
        return builder;
    }

    protected HttpFileSystemConfigBuilder(String prefix)
    {
        super(prefix);
    }

    private HttpFileSystemConfigBuilder()
    {
        super("http.");
    }

    /**
     * Set the charset used for url encoding<br>
     *
     * @param chaset the chaset
     */
    public void setUrlCharset(FileSystemOptions opts, String chaset)
    {
        HttpFileSystemOptions.getInstance(opts).setUrlCharset(chaset);
    }

    /**
     * Set the charset used for url encoding<br>
     *
     * @return the chaset
     */
    public String getUrlCharset(FileSystemOptions opts)
    {
        return HttpFileSystemOptions.getInstance(opts).getUrlCharset();
    }

    /**
     * Set the proxy to use for http connection.<br>
     * You have to set the ProxyPort too if you would like to have the proxy relly used.
     *
     * @param proxyHost the host
     * @see #setProxyPort
     */
    public void setProxyHost(FileSystemOptions opts, String proxyHost)
    {
        HttpFileSystemOptions.getInstance(opts).setProxyHost(proxyHost);
    }

    /**
     * Set the proxy-port to use for http connection
     * You have to set the ProxyHost too if you would like to have the proxy relly used.
     *
     * @param proxyPort the port
     * @see #setProxyHost
     */
    public void setProxyPort(FileSystemOptions opts, int proxyPort)
    {
        HttpFileSystemOptions.getInstance(opts).setProxyPort(proxyPort);
    }

    /**
     * Get the proxy to use for http connection
     * You have to set the ProxyPort too if you would like to have the proxy relly used.
     *
     * @return proxyHost
     * @see #setProxyPort
     */
    public String getProxyHost(FileSystemOptions opts)
    {
        return HttpFileSystemOptions.getInstance(opts).getProxyHost();
    }

    /**
     * Get the proxy-port to use for http the connection
     * You have to set the ProxyHost too if you would like to have the proxy relly used.
     *
     * @return proxyPort: the port number or 0 if it is not set
     * @see #setProxyHost
     */
    public int getProxyPort(FileSystemOptions opts)
    {
        return HttpFileSystemOptions.getInstance(opts).getProxyPort();
    }

    /**
     * Set the proxy authenticator where the system should get the credentials from
     */
    public void setProxyAuthenticator(FileSystemOptions opts, UserAuthenticator authenticator)
    {
        HttpFileSystemOptions.getInstance(opts).setProxyAuthenticator(authenticator);
    }

    /**
     * Get the proxy authenticator where the system should get the credentials from
     */
    public UserAuthenticator getProxyAuthenticator(FileSystemOptions opts)
    {
        return HttpFileSystemOptions.getInstance(opts).getProxyAuthenticator();
    }

    /**
     * The cookies to add to the reqest
     */
    public void setCookies(FileSystemOptions opts, Cookie[] cookies)
    {
        HttpFileSystemOptions.getInstance(opts).setCookies(cookies);
    }

    /**
     * The cookies to add to the reqest
     */
    public Cookie[] getCookies(FileSystemOptions opts)
    {
        return HttpFileSystemOptions.getInstance(opts).getCookies();
    }

    /**
     * The maximum number of connections allowed
     */
    public void setMaxTotalConnections(FileSystemOptions opts, int maxTotalConnections)
    {
        HttpFileSystemOptions.getInstance(opts).setMaxTotalConnections(maxTotalConnections);
    }

    /**
     * Retrieve the maximum number of connections allowed.
     * @param opts The FileSystemOptions.
     * @return The maximum number of connections allowed.
     */
    public int getMaxTotalConnections(FileSystemOptions opts)
    {
        return HttpFileSystemOptions.getInstance(opts).getMaxTotalConnections();
    }

    /**
     * The maximum number of connections allowed to any host
     */
    public void setMaxConnectionsPerHost(FileSystemOptions opts, int maxHostConnections)
    {
        HttpFileSystemOptions.getInstance(opts).setMaxConnectionsPerHost(maxHostConnections);
    }

    /**
     * Retrieve the maximum number of connections allowed per host.
     * @param opts The FileSystemOptions.
     * @return The maximum number of connections allowed per host.
     */
    public int getMaxConnectionsPerHost(FileSystemOptions opts)
    {
        return HttpFileSystemOptions.getInstance(opts).getMaxConnectionsPerHost();
    }

    protected Class getConfigClass()
    {
        return HttpFileSystem.class;
    }
}
