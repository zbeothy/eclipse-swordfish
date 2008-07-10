/*******************************************************************************
 * Copyright (c) 2007 Deutsche Post AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Deutsche Post AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.swordfish.core.management.components.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.FileHandler;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;

/**
 * The Class LogGroupReader.
 */
public class LogGroupReader {

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(LogGroupReader.class);

    /** The Constant LINES. */
    private static final int LINES = 25;

    /** The Constant SCROLL. */
    private static final int SCROLL = LINES * 100;

    /** The current file. */
    private RandomAccessFile currentFile;

    /** The files. */
    private File[] files = new File[0];

    /** The lines. */
    private String[] lines;

    /** logfile currently displayed. */
    private int fileNumber;

    /** The at end. */
    private boolean atEnd;

    /** The at start. */
    private boolean atStart;

    /** The pattern. */
    private String pattern;

    /** The handler. */
    private FileHandler handler;

    /** Start of currently displayed loglines. */
    private long startIndex;

    /** End of currently displayed loglines. */
    private long endIndex;

    /**
     * Instantiates a new log group reader.
     * 
     * @param handler
     *        the handler
     */
    public LogGroupReader(final FileHandler handler) {
        this.files = this.getHandlerFiles(handler);
        this.pattern = this.getHandlerPattern(handler);
        this.handler = handler;
        if (this.files.length > 0) {
            this.setFileNumber(new Integer(0));
            this.goTail();
        }
    }

    /**
     * Gets the file number.
     * 
     * @return the file number
     */
    public Integer getFileNumber() {
        return new Integer(this.fileNumber);
    }

    /**
     * Gets the files available.
     * 
     * @return the files available
     */
    public Integer getFilesAvailable() {
        return new Integer(this.files.length);
    }

    /**
     * Gets the index.
     * 
     * @return the index
     */
    public Long getIndex() {
        return new Long(this.startIndex);
    }

    /**
     * Gets the lines.
     * 
     * @return the lines
     */
    public String[] getLines() {
        if (this.atEnd) {
            this.goTail();
        }
        return this.lines;
    }

    /**
     * Gets the pattern.
     * 
     * @return the pattern
     */
    public String getPattern() {
        return this.pattern;
    }

    /**
     * Gets the total lines.
     * 
     * @return the total lines
     */
    public Long getTotalLines() {
        Long ret = new Long(-1);
        if (null != this.currentFile) {
            try {
                ret = new Long(this.currentFile.length());
            } catch (IOException e) {
                LOG.warn("While getting length of logfile", e);
            }
        }
        return ret;
    }

    /**
     * Go backward.
     * 
     * @return the string
     */
    public String goBackward() {
        if (null == this.currentFile) return "No current file";
        String ret = "ok";
        long post = 0;
        for (int i = 0; ((i < this.lines.length) && (i < 3)); i++) {
            post += this.lines[i].length();
        }
        this.atStart = false;
        this.atEnd = false;
        long newStart = this.startIndex - SCROLL;
        if (newStart < 0) {
            newStart = 0;
            this.atStart = true;
        }
        long newEnd = this.startIndex + post;
        try {
            this.currentFile.seek(newStart);
            this.readLines(newEnd);
            this.startIndex = newStart;
            this.endIndex = newEnd;
        } catch (IOException e) {
            LOG.warn("While reading logfile", e);
            this.lines = new String[1];
            this.lines[0] = "Error reading logfile: " + e.getMessage();
            ret = this.lines[0];
        }
        return ret;
    }

    /**
     * Go forward.
     * 
     * @return the string
     */
    public String goForward() {
        if (null == this.currentFile) return "No current file";
        String ret = "ok";
        long pre = 5;
        for (int i = 1; ((i < this.lines.length) && (i < 4)); i++) {
            pre += this.lines[this.lines.length - i].length();
        }
        this.atEnd = false;
        this.atStart = false;
        long newStart = this.endIndex - pre;
        if (newStart < 0) {
            newStart = 0;
            this.atStart = true;
        }
        long newEnd = newStart + SCROLL;
        try {
            this.currentFile.seek(newStart);
            this.readLines(newEnd);
            this.startIndex = newStart;
            this.endIndex = newEnd;
        } catch (IOException e) {
            LOG.warn("While reading logfile", e);
            this.lines = new String[1];
            this.lines[0] = "Error reading logfile: " + e.getMessage();
            ret = this.lines[0];
        }
        return ret;
    }

    /**
     * Go start.
     * 
     * @return the string
     */
    public String goStart() {
        if (null == this.currentFile) return "No current file";
        String ret = "ok";
        this.atStart = true;
        this.atEnd = false;
        try {
            this.currentFile.seek(0);
            this.readLines(SCROLL);
            this.startIndex = 0;
            this.endIndex = this.currentFile.getFilePointer();
        } catch (IOException e) {
            ret = e.getMessage();
            LOG.warn("While trying to read logfile", e);
        }
        return ret;
    }

    /**
     * Go tail.
     * 
     * @return the string
     */
    public String goTail() {
        if (null == this.currentFile) return "No current file";
        String ret = "ok";
        this.atEnd = true;
        this.atStart = false;
        try {
            this.startIndex = this.currentFile.length() - SCROLL;
            if (this.startIndex < 0) {
                this.startIndex = 0;
            }
            this.currentFile.seek(this.startIndex);
            this.readLines(this.currentFile.length() + 1000);
            this.endIndex = this.currentFile.getFilePointer();
        } catch (IOException e) {
            LOG.warn("While reading tail of logfile", e);
            this.lines = new String[1];
            this.lines[0] = "Error reading logfile: " + e.getMessage();
            ret = this.lines[0];
        }
        return ret;
    }

    /**
     * Refresh.
     */
    public void refresh() {
        this.files = this.getHandlerFiles(this.handler);
        this.currentFile = null;
        if (this.files.length >= 1) {
            this.setCurrentFile(this.files[0]);
        }
        this.goTail();
    }

    /**
     * Sets the file number.
     * 
     * @param in
     *        the in
     * 
     * @return the string
     */
    public String setFileNumber(final Integer in) {
        String ret = "ok";
        int num = in.intValue();
        if ((0 > num) || (this.files.length <= num)) {
            ret = "No file with index " + num;
        } else {
            this.setCurrentFile(this.files[num]);
            this.fileNumber = num;
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.pattern;
    }

    /**
     * Gets the files associated with a <code>FileHandler</code>
     * 
     * @param fileHandler
     *        the handler
     * 
     * @return the handler files
     */
    private File[] getHandlerFiles(final FileHandler fileHandler) {
        File[] ret = (File[]) AccessController.doPrivileged(new PrivilegedAction() {

            public Object run() {
                File[] nameFiles = null;
                try {
                    Field field = FileHandler.class.getDeclaredField("files");
                    field.setAccessible(true);
                    nameFiles = (File[]) field.get(fileHandler);
                } catch (Exception ex) {
                    LOG.warn("Could not get filename for FileHandler " + fileHandler, ex);
                }
                return nameFiles;
            }
        });
        return ret;
    }

    /**
     * Gets the pattern associated with a <code>FileHandler</code>
     * 
     * @param fileHandler
     *        the handler
     * 
     * @return the handler pattern
     */
    private String getHandlerPattern(final FileHandler fileHandler) {
        String ret = (String) AccessController.doPrivileged(new PrivilegedAction() {

            public Object run() {
                String sPattern = null;
                try {
                    Field field = FileHandler.class.getDeclaredField("pattern");
                    field.setAccessible(true);
                    sPattern = (String) field.get(fileHandler);
                } catch (Exception ex) {
                    LOG.warn("Could not get pattern for FileHandler " + fileHandler, ex);
                }
                return sPattern;
            }
        });
        return ret;
    }

    /**
     * Read lines.
     * 
     * @param newIndex
     *        the new index
     */
    private void readLines(final long newIndex) {
        boolean read = true;
        ArrayList newLines = new ArrayList(LINES);
        String line = null;
        if (!this.atStart) {
            try {
                // skip first line
                this.currentFile.readLine();
            } catch (IOException e) {
                newLines.add(e.getMessage());
                read = false;
            }
        }
        while (read) {
            try {
                line = this.currentFile.readLine();
                if (null == line) { // reached EOF
                    read = false;
                    line = "<<EOF";
                }
                if (newIndex < this.currentFile.getFilePointer()) {
                    read = false;
                }
            } catch (IOException e) {
                read = false;
                line = e.getMessage();
            }
            newLines.add(line);
        }
        this.lines = new String[newLines.size()];
        int i = 0;
        for (Iterator iter = newLines.iterator(); iter.hasNext();) {
            this.lines[i++] = (String) iter.next();
        }
    }

    /**
     * Sets the current file.
     * 
     * @param file
     *        the new current file
     */
    private void setCurrentFile(final File file) {
        if (null != this.currentFile) {
            try {
                this.currentFile.close();
                LOG.trace("Closed current logfile");
            } catch (IOException e) {
                this.currentFile = null;
                LOG.warn("Could not close current logfile", e);
            }
        }
        try {
            this.currentFile = new RandomAccessFile(file, "r");
            LOG.trace("Opened logfile for reading: " + file.getName());
        } catch (FileNotFoundException e) {
            this.currentFile = null;
            LOG.warn("Could not access logfile " + file.getName(), e);
        }
    }

}
