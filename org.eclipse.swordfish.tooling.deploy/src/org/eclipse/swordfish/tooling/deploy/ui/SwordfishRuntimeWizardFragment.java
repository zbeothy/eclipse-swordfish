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
package org.eclipse.swordfish.tooling.deploy.ui;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swordfish.tooling.deploy.DeployPlugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
import org.eclipse.swordfish.tooling.deploy.ui.SwordfishOSGiRuntimeComposite;

/**
 * Swordfish runtime wizard fragment implementation.
 * 
 * @author Alex Tselesh
 */
public class SwordfishRuntimeWizardFragment extends WizardFragment {

	protected SwordfishOSGiRuntimeComposite comp;

	public SwordfishRuntimeWizardFragment() {
		// do nothing
	}

	public boolean hasComposite() {
		return true;
	}

	public boolean isComplete() {
		IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel()
				.getObject(TaskModel.TASK_RUNTIME);
		if (runtime == null)
			return false;

		IStatus status = runtime.validate(null);
		return (status == null || status.getSeverity() != IStatus.ERROR);
	}

	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		comp = new SwordfishOSGiRuntimeComposite(parent, wizard);
		return comp;
	}

	public void enter() {
		if (comp != null) {
			IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel()
					.getObject(TaskModel.TASK_RUNTIME);
			comp.setRuntime(runtime);
		}
	}

	public void exit() {
		IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel()
				.getObject(TaskModel.TASK_RUNTIME);
		IPath path = runtime.getLocation();
		if (runtime.validate(null).getSeverity() != IStatus.ERROR) {
			DeployPlugin.setPreference("location"
					+ runtime.getRuntimeType().getId(), path.toString());
		}
	}
}
