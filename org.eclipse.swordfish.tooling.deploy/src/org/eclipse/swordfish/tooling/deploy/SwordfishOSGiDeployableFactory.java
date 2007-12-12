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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.project.facet.core.internal.FacetedProjectNature;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ModuleDelegate;
import org.eclipse.wst.server.core.util.ProjectModuleFactoryDelegate;

/**
 * Swordfish OSGi module factory delegate implementation.
 * 
 * @author Alex Tselesh
 */
public class SwordfishOSGiDeployableFactory extends ProjectModuleFactoryDelegate {

	protected Map moduleDelegates = new HashMap(5);
	
	public SwordfishOSGiDeployableFactory() {
		super();
	}

	protected IModule[] createModules(IProject project) {
		Logger.info("SwordfishOSGiDeployableFactory.createModules(IProject " + project + ")");
		try {
			ModuleCoreNature nature = (ModuleCoreNature) project.getNature(IModuleConstants.MODULE_NATURE_ID);
			//ModuleCoreNature nature = (ModuleCoreNature) project.getNature(JavaCore.NATURE_ID);
			//ModuleCoreNature nature = (ModuleCoreNature) project.getNature(FacetedProjectNature.NATURE_ID);
			Logger.info("SwordfishOSGiDeployableFactory.createModules(): nature - " + nature);
			if (nature != null)
				return createModules(nature);
		} catch (CoreException e) {
			Logger.error(e);
		}
		return null;
	}

	protected IModule[] createModules(ModuleCoreNature nature) {
		IProject project = nature.getProject();
		try {
			IVirtualComponent comp = ComponentCore.createComponent(project);
			Logger.info("SwordfishOSGiDeployableFactory.createModules(ModuleCoreNature nature): IVirtualComponent - " + comp);
			return createModuleDelegates(comp);
		} catch (Exception e) {
			Logger.error(e);
		}
		return null;
	}

	public ModuleDelegate getModuleDelegate(IModule module) {
		Logger.info("SwordfishOSGiDeployableFactory.getModuleDelegate(IModule " + module + ")");
		return (ModuleDelegate) moduleDelegates.get(module);
	}

	protected IModule[] createModuleDelegates(IVirtualComponent component) {
		List projectModules = new ArrayList();
		try {
			if(OSGiProjectUtilities.isLegacyOSGiProject(component.getProject())){
				Logger.info("SwordfishOSGiDeployableFactory.createModuleDelegates(IVirtualComponent component): isLegacyOSGiProject - true");

				String type = OSGiProjectUtilities.getOSGiProjectType(component.getProject());
				Logger.info("SwordfishOSGiDeployableFactory.createModuleDelegates(IVirtualComponent component): type - " + type);

				String version = OSGiProjectUtilities.getOSGiProjectVersion(component.getProject());
				Logger.info("SwordfishOSGiDeployableFactory.createModuleDelegates(IVirtualComponent component): version - " + version);

				IModule module = createModule(component.getDeployedName(), component.getDeployedName(), type, version, component.getProject());
				Logger.info("SwordfishOSGiDeployableFactory.createModuleDelegates(IVirtualComponent component): module - " + module);

				OSGiFlexProjDeployable moduleDelegate = new OSGiFlexProjDeployable(component.getProject(), component);
				Logger.info("SwordfishOSGiDeployableFactory.createModuleDelegates(IVirtualComponent component): moduleDelegate - " + moduleDelegate);

				moduleDelegates.put(module, moduleDelegate);
				projectModules.add(module);
			} else  {
				return null;
			}
		} catch (Exception e) {
			Logger.error(e);
		}
		return (IModule[]) projectModules.toArray(new IModule[projectModules.size()]);
	}

	/**
	 * Returns the list of resources that the module should listen to
	 * for state changes. The paths should be project relative paths.
	 * Subclasses can override this method to provide the paths.
	 *
	 * @return a possibly empty array of paths
	 */
	protected IPath[] getListenerPaths() {
		return new IPath[] {};
	}

	protected void clearCache() {
		moduleDelegates = new HashMap(5);
	}

}
