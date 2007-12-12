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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swordfish.tooling.deploy.DeployPlugin;
import org.eclipse.swordfish.tooling.deploy.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.swordfish.tooling.deploy.ui.SWTUtil;
import org.eclipse.swordfish.tooling.deploy.ui.SwordfishOSGiRuntimeComposite;

/**
 * Swordfish runtime wizard fragment composite implementation.
 * 
 * @author Alex Tselesh
 */
public class SwordfishOSGiRuntimeComposite extends Composite {

	protected IRuntimeWorkingCopy runtimeWC;

	protected IWizardHandle wizard;

	protected Text deployDir;
	protected Text name;

	public SwordfishOSGiRuntimeComposite(Composite parent, IWizardHandle wizard) {

		super(parent, SWT.NONE);
		this.wizard = wizard;

		Logger.info("SwordfishOSGiRuntimeComposite()");

		wizard.setTitle("Swordfish Server");
		wizard.setDescription("Specify the deployment directory");
		wizard.setImageDescriptor(DeployPlugin.getImageDescriptor("swordfish_big.jpg"));

		createControl();
	}

	protected void setRuntime(IRuntimeWorkingCopy newRuntime) {
		if (newRuntime == null) {
			runtimeWC = null;
		} else {
			runtimeWC = newRuntime;
		}

		init();
		validate();
	}

	/**
	 * Provide a wizard page to change the Swordfish deployment directory.
	 */
	protected void createControl() {

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		setLayout(layout);
		setLayoutData(new GridData(GridData.FILL_BOTH));

		Label label = new Label(this, SWT.NONE);
		label.setText("Na&me:");
		GridData data = new GridData();
		data.horizontalSpan = 2;
		label.setLayoutData(data);

		name = new Text(this, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		name.setLayoutData(data);
		name.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				runtimeWC.setName(name.getText());
				validate();
			}
		});

		label = new Label(this, SWT.NONE);
		label.setText("Swordfish deployment &directory:");
		data = new GridData();
		data.horizontalSpan = 2;
		label.setLayoutData(data);

		deployDir = new Text(this, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		deployDir.setLayoutData(data);
		deployDir.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (null != deployDir.getText()
						&& 0 < deployDir.getText().length()) {
					runtimeWC.setLocation(new Path(deployDir.getText()));
				}
				validate();
			}
		});

		Button browse = SWTUtil.createButton(this, "B&rowse...");
		browse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				DirectoryDialog dialog = new DirectoryDialog(
						SwordfishOSGiRuntimeComposite.this.getShell());
				dialog.setMessage("Select Swordfish deployment directory.");
				dialog.setFilterPath(deployDir.getText());
				String selectedDirectory = dialog.open();
				if (selectedDirectory != null)
					deployDir.setText(selectedDirectory);
			}
		});

		init();

		validate();

		Dialog.applyDialogFont(this);

		name.forceFocus();
	}

	protected void init() {

		if (name == null || runtimeWC == null)
			return;

		if (runtimeWC.getName() != null)
			name.setText(runtimeWC.getName());
		else
			name.setText("");

		if (runtimeWC.getLocation() != null)
			deployDir.setText(runtimeWC.getLocation().toOSString());
		else
			deployDir.setText("");

	}

	protected void validate() {
		if (runtimeWC == null) {
			wizard.setMessage("", IMessageProvider.ERROR);
			return;
		}

		IStatus status = runtimeWC.validate(null);
		if (status == null || status.isOK())
			wizard.setMessage(null, IMessageProvider.NONE);
		else if (status.getSeverity() == IStatus.WARNING)
			wizard.setMessage(status.getMessage(), IMessageProvider.WARNING);
		else
			wizard.setMessage(status.getMessage(), IMessageProvider.ERROR);
		wizard.update();
	}
}
