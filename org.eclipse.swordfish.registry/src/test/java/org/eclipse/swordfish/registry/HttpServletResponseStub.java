/*******************************************************************************
* Copyright (c) 2008, 2009 SOPERA GmbH.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* SOPERA GmbH - initial API and implementation
*******************************************************************************/

package org.eclipse.swordfish.registry;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class HttpServletResponseStub implements HttpServletResponse {
	
	private String contentType;

	private String characterEncoding;
	
	private int error;
	

	public String getCharacterEncoding() {
		return characterEncoding;
	}

	public String getContentType() {
		return contentType;
	}

	public int getError() {
		return error;
	}
	
	public void flushBuffer() throws IOException {
	}

	public int getBufferSize() {
		return 0;
	}

	public Locale getLocale() {
		return null;
	}

	public ServletOutputStream getOutputStream() throws IOException {
		return null;
	}

	public PrintWriter getWriter() throws IOException {
		return null;
	}

	public boolean isCommitted() {
		return false;
	}

	public void reset() {
	}

	public void resetBuffer() {
	}

	public void setBufferSize(int size) {
	}

	public void setCharacterEncoding(String charset) {
		characterEncoding = charset;
	}

	public void setContentLength(int len) {
	}

	public void setContentType(String type) {
		contentType = type;
	}

	public void setLocale(Locale loc) {
	}

	public void addCookie(Cookie cookie) {
	}

	public void addDateHeader(String name, long date) {
	}

	public void addHeader(String name, String value) {
	}

	public void addIntHeader(String name, int value) {
	}

	public boolean containsHeader(String name) {
		return false;
	}

	public String encodeRedirectUrl(String url) {
		return null;
	}

	public String encodeRedirectURL(String url) {
		return null;
	}

	public String encodeUrl(String url) {
		return null;
	}

	public String encodeURL(String url) {
		return null;
	}

	public void sendError(int sc, String msg) throws IOException {
		error = sc;
	}

	public void sendError(int sc) throws IOException {
		error = sc;
	}

	public void sendRedirect(String location) throws IOException {
	}

	public void setDateHeader(String name, long date) {
	}

	public void setHeader(String name, String value) {
	}

	public void setIntHeader(String name, int value) {
	}

	public void setStatus(int sc, String sm) {
	}

	public void setStatus(int sc) {
	}
}