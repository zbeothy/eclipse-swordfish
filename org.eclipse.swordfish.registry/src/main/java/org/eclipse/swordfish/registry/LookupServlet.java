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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LookupServlet extends HttpServlet {
	
    private static final Logger LOGGER = LoggerFactory
    .getLogger(LookupServlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -8376659320998034145L;

	private WSDLRepository repository;

	public LookupServlet() {
	}

	public LookupServlet(WSDLRepository repository) {
		this.repository = repository;
	}

	@Override
	public void init() throws ServletException {
		InMemoryRepositoryImpl inMemoryRepos = new InMemoryRepositoryImpl();
		String location = getInitParameter("wsdlLocation");
		FileBasedLoader loader = new FileBasedLoader();
		try {
			loader.setDirectory(location);
			loader.fill(inMemoryRepos);
		} catch (RegistryException e) {
			throw new ServletException("Unable to load WSDL's from directory "
					+ location, e);
		}
		repository = inMemoryRepos;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		LOGGER.info("Recieved request:\n{}", req.getRequestURL());
		Resource resource = null;

		String type = req.getParameter("type");

		if (type == null) {
			String resourceId = getResourceId(req);
			if (resourceId == null) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			resource = repository.getWithId(resourceId);
			if (resource == null) {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;				
			}
		} else if ("portType".equals(type)) {
			String portTypeName = req.getParameter("name");
			String targetNameSpace = req.getParameter("targetNamespace");

			if (portTypeName == null || targetNameSpace == null) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "If the query parameter type is set to portType also the query parameters name and targetNamespace have to be defined");
				return;		
			}

			resource = repository.getByPortTypeName(new QName(targetNameSpace,
					portTypeName));

		} else if ("service".equals(type)) {
			String portTypeNamespace = req.getParameter("refPortTypeNamespace");
			String portTypeName = req.getParameter("refPortTypeName");

			if (portTypeNamespace == null || portTypeName == null) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "If the query parameter type is set to service also the query parameters refPortTypeNamespace and refPortTypeName have to be defined");
				return;		
			}

			resource = repository.getReferencingPortType(new QName(
					portTypeNamespace, portTypeName));
		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Only the values portType and service are accepted for the query parameter type");
			return;
		}
		
		resp.setContentType(resource.getContentType());
		resp.setCharacterEncoding(resource.getCharacterEncoding());
		resource.appendContent(resp.getWriter());
	}
	
	private static String getResourceId(HttpServletRequest req) {
		String pathInfo = req.getPathInfo();
		if (pathInfo != null && pathInfo.length() > 1) {
			return pathInfo.substring(1);
		}
		return null;
	}
}
