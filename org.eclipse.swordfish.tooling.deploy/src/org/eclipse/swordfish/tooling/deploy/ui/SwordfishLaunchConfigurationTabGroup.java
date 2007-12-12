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

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.wst.server.ui.ServerLaunchConfigurationTab;

/**
 * Swordfish launch configuration tab group.
 * 
 * @author Alex Tselesh
 */
public class SwordfishLaunchConfigurationTabGroup extends
		AbstractLaunchConfigurationTabGroup {
	/*
	 * @see ILaunchConfigurationTabGroup#createTabs(ILaunchConfigurationDialog,
	 *      String)
	 */
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		// ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[6];
		// //tabs[0] = new ServerLaunchConfigurationTab(new String[] {
		// "org.eclipse.jst.server.preview" });
		// tabs[0] = new ServerLaunchConfigurationTab(new String[] {
		// "org.swordfish.core.server" });
		// //tabs[0] = new ServerLaunchConfigurationTab(new String[] {
		// "org.swordfish.server" });
		// tabs[0].setLaunchConfigurationDialog(dialog);
		// tabs[1] = new JavaArgumentsTab();
		// tabs[1].setLaunchConfigurationDialog(dialog);
		// tabs[2] = new JavaClasspathTab();
		// tabs[2].setLaunchConfigurationDialog(dialog);
		// tabs[3] = new SourceLookupTab();
		// tabs[3].setLaunchConfigurationDialog(dialog);
		// tabs[4] = new EnvironmentTab();
		// tabs[4].setLaunchConfigurationDialog(dialog);
		// tabs[5] = new CommonTab();
		// tabs[5].setLaunchConfigurationDialog(dialog);
		// setTabs(tabs);

		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[2];
		tabs[0] = new ServerLaunchConfigurationTab(
				new String[] { "org.swordfish.core.server" });
		tabs[0].setLaunchConfigurationDialog(dialog);
		tabs[1] = new CommonTab();
		tabs[1].setLaunchConfigurationDialog(dialog);
		setTabs(tabs);

		// ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[1];
		// tabs[0] = new ServerLaunchConfigurationTab(new String[] {
		// "org.swordfish.core.server" });
		// tabs[0].setLaunchConfigurationDialog(dialog);
		// setTabs(tabs);
	}
}