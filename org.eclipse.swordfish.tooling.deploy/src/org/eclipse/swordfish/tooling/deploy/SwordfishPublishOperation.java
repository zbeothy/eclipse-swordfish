/*******************************************************************************
 * Copyright (c) 2007 SOPERA GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     SOPERA GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.swordfish.tooling.deploy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.wst.server.core.IModule;
//import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.core.model.PublishOperation;

public class SwordfishPublishOperation extends PublishOperation {

	protected SwordfishServerBehaviourDelegate server;
	protected IModule[] module;
	protected int kind;
	protected int deltaKind;

	public SwordfishPublishOperation(SwordfishServerBehaviourDelegate server, IModule[] module) {
		super("Publish to server", "Publish OSGi bundle to Swordfish OSGi runtime");
		this.server = server;
		this.module = module;
		Logger.info("SwordfishPublishOperation(SwordfishServerBehaviourDelegate: " + server + ", IModule[]: " + module + ")");

	}

	public SwordfishPublishOperation(SwordfishServerBehaviourDelegate server, int kind, IModule[] module, int deltaKind) {
		this(server, module);
		this.kind = kind;
		this.deltaKind = deltaKind;
		Logger.info("SwordfishPublishOperation(SwordfishServerBehaviourDelegate: " + server + ", IModule[]: " + module + ", kind: " + kind + ", deltaKind: " + deltaKind + ")");
	}

	public void execute(IProgressMonitor monitor, IAdaptable info) throws CoreException {
		
		Logger.info("SwordfishPublishOperation.execute(IProgressMonitor: " + monitor + ", IAdaptable: " + info + ")");

		//TODO:
		Logger.info("execute publish operation...");
	}

	public int getOrder() {
		return 0;
	}
	
	/**
	 * Utility method to throw a CoreException based on the contents of a list of
	 * error and warning status.
	 * 
	 * @param status a List containing error and warning IStatus
	 * @throws CoreException
	 */
	protected static void throwException(List status) throws CoreException {
		if (status == null)
			status = new ArrayList();
		
		if (status == null || status.size() == 0)
			return;
		if (status.size() == 1) {
			IStatus status2 = (IStatus) status.get(0);
			throw new CoreException(status2);
		}
		IStatus[] children = new IStatus[status.size()];
		status.toArray(children);
		String message = "Publish error"; // Messages.errorPublish;
		MultiStatus status2 = new MultiStatus(DeployPlugin.PLUGIN_ID, 0, children, message, null);
		throw new CoreException(status2);
	}

	protected static void addArrayToList(List list, IStatus[] a) {
		if (list == null || a == null || a.length == 0)
			return;
		
		int size = a.length;
		for (int i = 0; i < size; i++)
			list.add(a[i]);
	}

//	// just for logging
//	public TaskModel getTaskModel() {
//		Logger.info("SwordfishPublishOperation.getTaskModel()");
//		return super.getTaskModel();
//	}

//	public void setTaskModel(TaskModel taskModel) {
//		StringBuffer sb = new StringBuffer();
//		sb.append("SwordfishPublishOperation.setTaskModel(TaskModel: " + taskModel + "): ");
//		sb.append("\n\t TaskModel.TASK_RUNTIME: " + taskModel.getObject(TaskModel.TASK_RUNTIME));
//		sb.append("\n\t TaskModel.TASK_SERVER: " + taskModel.getObject(TaskModel.TASK_SERVER));
//		sb.append("\n\t TaskModel.TASK_LAUNCH_MODE: " + taskModel.getObject(TaskModel.TASK_LAUNCH_MODE));
//		sb.append("\n\t TaskModel.TASK_MODULES: " + taskModel.getObject(TaskModel.TASK_MODULES));
//		Logger.info(sb.toString());
//		super.setTaskModel(taskModel);
//	}

}
