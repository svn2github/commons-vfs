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

package org.apache.commons.vfs.provider.res.test;

import junit.framework.Test;
import org.apache.commons.AbstractVfsTestCase;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.provider.jar.JarFileProvider;
import org.apache.commons.vfs.provider.res.ResourceFileProvider;
import org.apache.commons.vfs.provider.url.UrlFileProvider;
import org.apache.commons.vfs.test.AbstractProviderTestConfig;
import org.apache.commons.vfs.test.ProviderTestSuite;

/**
 * Test cases for the resource provider.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ResourceProviderTestCase extends AbstractProviderTestConfig
{
    public static Test suite() throws Exception
    {
        return new ProviderTestSuite(new ResourceProviderTestCase());
    }

    /**
     * Prepares the file system manager.  This implementation does nothing.
     */
    public void prepare(DefaultFileSystemManager manager)
        throws Exception
    {
        manager.addProvider("res", new ResourceFileProvider());
        manager.addProvider("file", new UrlFileProvider());
        manager.addProvider("jar", new JarFileProvider());
    }

    /**
     * Returns the base folder for tests.
     */
    public FileObject getBaseTestFolder(FileSystemManager manager)
        throws Exception
    {
        String baseDir = AbstractVfsTestCase.getResourceTestDirectory();
        return manager.resolveFile("res:" + baseDir);
    }
}