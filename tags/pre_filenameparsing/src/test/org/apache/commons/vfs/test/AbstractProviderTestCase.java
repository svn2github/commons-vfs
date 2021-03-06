/*
 * Copyright 2002, 2003,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.vfs.test;

import org.apache.commons.AbstractVfsTestCase;
import org.apache.commons.vfs.Capability;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLConnection;
import java.util.Arrays;

/**
 * File system test cases, which verifies the structure and naming
 * functionality.
 * <p/>
 * Works from a base folder, and assumes a particular structure under
 * that base folder.
 *
 * @author <a href="mailto:adammurdoch@apache.org">Adam Murdoch</a>
 * @version $Revision$ $Date$
 */
public abstract class AbstractProviderTestCase
    extends AbstractVfsTestCase
{
    private FileObject baseFolder;
    private FileObject readFolder;
    private FileObject writeFolder;
    private DefaultFileSystemManager manager;
    private Method method;

    // Expected contents of "file1.txt"
    public static final String FILE1_CONTENT = "This is a test file.";

    // Expected contents of test files
    public static final String TEST_FILE_CONTENT = "A test file.";

    /**
     * Sets the test method.
     */
    public void setMethod(final Method method)
    {
        this.method = method;
    }

    /**
     * Configures this test.
     */
    public void setConfig(final DefaultFileSystemManager manager,
                          final FileObject baseFolder,
                          final FileObject readFolder,
                          final FileObject writeFolder)
    {
        this.manager = manager;
        this.baseFolder = baseFolder;
        this.readFolder = readFolder;
        this.writeFolder = writeFolder;
    }

    /**
     * Returns the file system manager used by this test.
     */
    protected DefaultFileSystemManager getManager()
    {
        return manager;
    }

    /**
     * Returns the base test folder.  This is the parent of both the read
     * test and write test folders.
     */
    public FileObject getBaseFolder()
    {
        return baseFolder;
    }

    /**
     * Returns the read test folder.
     */
    protected FileObject getReadFolder()
    {
        return readFolder;
    }

    /**
     * Returns the write test folder.
     */
    protected FileObject getWriteFolder()
    {
        return writeFolder;
    }

    /**
     * Returns the capabilities required by the tests of this test case.  The
     * tests are not run if the provider being tested does not support all
     * the required capabilities.  Return null or an empty array to always
     * run the tests.
     * <p/>
     * <p>This implementation returns null.
     */
    protected Capability[] getRequiredCaps()
    {
        return null;
    }

    /**
     * Runs the test.  This implementation short-circuits the test if the
     * provider being tested does not have the capabilities required by this
     * test.
     *
     * @todo Handle negative caps as well - ie, only run a test if the provider does not have certain caps.
     * @todo Figure out how to remove the test from the TestResult if the test is skipped.
     */
    protected void runTest() throws Throwable
    {
        // Check the capabilities
        final Capability[] caps = getRequiredCaps();
        if (caps != null)
        {
            for (int i = 0; i < caps.length; i++)
            {
                final Capability cap = caps[i];
                if (!readFolder.getFileSystem().hasCapability(cap))
                {
                    System.out.println("skipping " + getName() + " because fs does not have cap " + cap);
                    return;
                }
            }
        }

        // Provider has all the capabilities - execute the test
        if (method != null)
        {
            try
            {
                method.invoke(this, null);
            }
            catch (final InvocationTargetException e)
            {
                throw e.getTargetException();
            }
        }
        else
        {
            super.runTest();
        }
    }

    /**
     * Asserts that the content of a file is the same as expected. Checks the
     * length reported by getContentLength() is correct, then reads the content
     * as a byte stream and compares the result with the expected content.
     * Assumes files are encoded using UTF-8.
     */
    protected void assertSameURLContent(final String expected,
                                        final URLConnection connection)
        throws Exception
    {
        // Get file content as a binary stream
        final byte[] expectedBin = expected.getBytes("utf-8");

        // Check lengths
        assertEquals("same content length", expectedBin.length, connection.getContentLength());

        // Read content into byte array
        final InputStream instr = connection.getInputStream();
        final ByteArrayOutputStream outstr;
        try
        {
            outstr = new ByteArrayOutputStream();
            final byte[] buffer = new byte[256];
            int nread = 0;
            while (nread >= 0)
            {
                outstr.write(buffer, 0, nread);
                nread = instr.read(buffer);
            }
        }
        finally
        {
            instr.close();
        }

        // Compare
        assertTrue("same binary content", Arrays.equals(expectedBin, outstr.toByteArray()));
    }

    /**
     * Asserts that the content of a file is the same as expected. Checks the
     * length reported by getSize() is correct, then reads the content as
     * a byte stream and compares the result with the expected content.
     * Assumes files are encoded using UTF-8.
     */
    protected void assertSameContent(final String expected,
                                     final FileObject file)
        throws Exception
    {
        // Check the file exists, and is a file
        assertTrue(file.exists());
        assertSame(FileType.FILE, file.getType());

        // Get file content as a binary stream
        final byte[] expectedBin = expected.getBytes("utf-8");

        // Check lengths
        final FileContent content = file.getContent();
        assertEquals("same content length", expectedBin.length, content.getSize());

        // Read content into byte array
        final InputStream instr = content.getInputStream();
        final ByteArrayOutputStream outstr;
        try
        {
            outstr = new ByteArrayOutputStream(expectedBin.length);
            final byte[] buffer = new byte[256];
            int nread = 0;
            while (nread >= 0)
            {
                outstr.write(buffer, 0, nread);
                nread = instr.read(buffer);
            }
        }
        finally
        {
            instr.close();
        }

        // Compare
        assertTrue("same binary content", Arrays.equals(expectedBin, outstr.toByteArray()));
    }

    /**
     * Builds the expected structure of the read tests folder.
     */
    protected FileInfo buildExpectedStructure()
    {
        // Build the expected structure
        final FileInfo base = new FileInfo(getReadFolder().getName().getBaseName(), FileType.FOLDER);
        base.addFile("file1.txt", FILE1_CONTENT);
        base.addFile("empty.txt", "");
        base.addFolder("emptydir");

        final FileInfo dir = base.addFolder("dir1");
        dir.addFile("file1.txt", TEST_FILE_CONTENT);
        dir.addFile("file2.txt", TEST_FILE_CONTENT);
        dir.addFile("file3.txt", TEST_FILE_CONTENT);

        final FileInfo subdir1 = dir.addFolder("subdir1");
        subdir1.addFile("file1.txt", TEST_FILE_CONTENT);
        subdir1.addFile("file2.txt", TEST_FILE_CONTENT);
        subdir1.addFile("file3.txt", TEST_FILE_CONTENT);

        final FileInfo subdir2 = dir.addFolder("subdir2");
        subdir2.addFile("file1.txt", TEST_FILE_CONTENT);
        subdir2.addFile("file2.txt", TEST_FILE_CONTENT);
        subdir2.addFile("file3.txt", TEST_FILE_CONTENT);

        final FileInfo subdir3 = dir.addFolder("subdir3");
        subdir3.addFile("file1.txt", TEST_FILE_CONTENT);
        subdir3.addFile("file2.txt", TEST_FILE_CONTENT);
        subdir3.addFile("file3.txt", TEST_FILE_CONTENT);

        return base;
    }
}
