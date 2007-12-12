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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jem.workbench.utility.JemProjectUtilities;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;

public class OSGiProjectUtilities {

	//moduleType
	public final static String SWORDFISH_OSGI_MODULE = "osgi.swordfish";

	/**
	 * (this method only looks at the facet & their versions to determine the OSGi level)
	 * TODO: ???
	 * @param project
	 * @return true, if OSGi version 1.0 
	 */
	public static boolean isLegacyOSGiProject(IProject project){
		boolean ret = false;
		
		IFacetedProject facetedProject;
		try {
			facetedProject = ProjectFacetsManager.create(project);
			if (facetedProject == null)
				return false;
			
			if(isOSGiProject(facetedProject)){
				IProjectFacet osgiFacet = ProjectFacetsManager.getProjectFacet(
						OSGiProjectUtilities.SWORDFISH_OSGI_MODULE);
				ret = (facetedProject.hasProjectFacet(osgiFacet.getVersion("1.0"))
						|| facetedProject.hasProjectFacet(osgiFacet.getVersion("1.0")));
			} 
		} catch (CoreException e) {
			Logger.error(e);
		}
		return ret;
	}
	
	private static boolean isOSGiProject(IFacetedProject project) {
		return isProjectOfType(project, OSGiProjectUtilities.SWORDFISH_OSGI_MODULE);
	}

	private static boolean isProjectOfType(IFacetedProject facetedProject, String typeID) {
		if (facetedProject != null && ProjectFacetsManager.isProjectFacetDefined(typeID)) {
			IProjectFacet projectFacet = ProjectFacetsManager.getProjectFacet(typeID);
			return projectFacet != null && facetedProject.hasProjectFacet(projectFacet);
		}
		return false;
	}

	public static String getOSGiProjectType(IProject project) {
		if (null != project && project.isAccessible()) {
			IFacetedProject facetedProject = null;
			try {
				facetedProject = ProjectFacetsManager.create(project);
			} catch (CoreException e) {
				return "";
			}
			if (isOSGiProject(facetedProject))
				return OSGiProjectUtilities.SWORDFISH_OSGI_MODULE;
		}
		return "";
	}

	public static String getOSGiProjectVersion(IProject project) {
		String type = getOSGiProjectType(project);
		IFacetedProject facetedProject = null;
		IProjectFacet facet = null;
		try {
			facetedProject = ProjectFacetsManager.create(project);
			facet = ProjectFacetsManager.getProjectFacet(type);
		} catch (Exception e) {
			// Not Faceted project or not OSGi Project
		}
		if (facet != null && facetedProject.hasProjectFacet(facet))
			return facetedProject.getInstalledVersion(facet).getVersionString();
		return null;
	}

	/**
	 * Retrieve all the output containers for a given virtual component.
	 * 
	 * @param vc
	 * @return array of IContainers for the output folders
	 */
	public static IContainer[] getOutputContainers(IProject project) {
		List result = new ArrayList();
		try {
			if (!project.hasNature(JavaCore.NATURE_ID))
				return new IContainer[] {};
		} catch (Exception e) {
		}
		IPackageFragmentRoot[] sourceContainers = getSourceContainers(project);
		for (int i = 0; i < sourceContainers.length; i++) {
			IContainer outputFolder = getOutputContainer(project, sourceContainers[i]);
			if (outputFolder != null && !result.contains(outputFolder))
				result.add(outputFolder);
		}
		return (IContainer[]) result.toArray(new IContainer[result.size()]);
	}

	public static IContainer getOutputContainer(IProject project, IPackageFragmentRoot sourceContainer) {
		try {
			IJavaProject jProject = JavaCore.create(project);
			IPath outputPath = sourceContainer.getRawClasspathEntry().getOutputLocation();
			if (outputPath == null) {
				if (jProject.getOutputLocation().segmentCount() == 1)
					return project;
				return project.getFolder(jProject.getOutputLocation().removeFirstSegments(1));
			}
			return project.getFolder(outputPath.removeFirstSegments(1));
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * Retrieve all the source containers for a given virtual workbench component
	 * 
	 * @param vc
	 * @return the array of IPackageFragmentRoots
	 */
	public static IPackageFragmentRoot[] getSourceContainers(IProject project) {
		IJavaProject jProject = JemProjectUtilities.getJavaProject(project);
		if (jProject == null)
			return new IPackageFragmentRoot[0];
		List list = new ArrayList();
		IVirtualComponent vc = ComponentCore.createComponent(project);
		IPackageFragmentRoot[] roots;
		try {
			roots = jProject.getPackageFragmentRoots();
			for (int i = 0; i < roots.length; i++) {
				if (roots[i].getKind() != IPackageFragmentRoot.K_SOURCE)
					continue;
				IResource resource = roots[i].getResource();
				if (null != resource) {
					IVirtualResource[] vResources = ComponentCore.createResources(resource);
					boolean found = false;
					for (int j = 0; !found && j < vResources.length; j++) {
						if (vResources[j].getComponent().equals(vc)) {
							if (!list.contains(roots[i]))
								list.add(roots[i]);
							found = true;
						}
					}
				}
			}
		} catch (JavaModelException e) {
			Logger.error(e);
		}
		return (IPackageFragmentRoot[]) list.toArray(new IPackageFragmentRoot[list.size()]);
	}

    // array to string
	public static String toString(Object arr) {
        
        if (arr == null || !arr.getClass().isArray()) {
            return String.valueOf(arr);
        }

        StringBuffer sb = new StringBuffer();
        int len = Array.getLength(arr);

        sb.append('[');

        for (int i = 0; i < len; i++) {
            if (i > 0) {
                sb.append(',');
            }

            Object obj = Array.get(arr, i);
            sb.append(toString(obj));
        }
        sb.append(']');

        return sb.toString();
    }

	public static String printModulesInfo(IModule[] modules) {
		if (null == modules) {
			return "\t\t ### IModule[] module: null";
		} else {
			StringBuffer sb = new StringBuffer("\t\t ### IModule[] module: size - " + modules.length);
			for (int i = 0, count = modules.length; i < count; i++) {
				IModule mod = modules[i];
				sb.append("\t\t\t - [" + i + "] " + OSGiProjectUtilities.printModuleInfo(mod));
			}
			return sb.toString();
		}
	}
	
	public static String printModuleInfo(IModule module) {
		return "IModule {id: " + module.getId()
					+ "; name: " + module.getName()
					+ "; type: " + module.getModuleType()
					+ "; project: " + module.getProject() + "}";
	}

	public static String printIModules(List modules) {
		
		if (null == modules) {
			return "\t\t ### List modules: null";
		} else {
			StringBuffer sb = new StringBuffer("\t\t ### List modules: size - " + modules.size());
			for (int i = 0, count = modules.size(); i < count; i++) {
				sb.append(printModulesInfo((IModule[]) modules.get(i)));
			}
			return sb.toString();
		}
	}
	
	public static String resolveModuleState(int state) {
		
		if (state == IServer.STATE_STARTING) return "STATE_STARTING";
		if (state == IServer.STATE_STARTED)  return "STATE_STARTED";
		if (state == IServer.STATE_STOPPING) return "STATE_STOPPING";
		if (state == IServer.STATE_STOPPED)  return "STATE_STOPPED";
		if (state == IServer.STATE_UNKNOWN)  return "STATE_UNKNOWN";
		return "STATE_XYZ_KNOWN";
	}

	public static String resolvePublishKindState(int state) {
		if (state == IServer.PUBLISH_INCREMENTAL) return "PUBLISH_INCREMENTAL";
		if (state == IServer.PUBLISH_FULL)        return "PUBLISH_FULL";
		if (state == IServer.PUBLISH_AUTO)        return "PUBLISH_AUTOMATIC";
		if (state == IServer.PUBLISH_CLEAN)       return "PUBLISH_CLEAN";
		return "STATE_XYZ_KNOWN";
	}

	public static String resolvePublishState(int state) {
		if (state == IServer.PUBLISH_STATE_FULL)        return "PUBLISH_STATE_FULL";
		if (state == IServer.PUBLISH_STATE_INCREMENTAL) return "PUBLISH_STATE_INCREMENTAL";
		if (state == IServer.PUBLISH_STATE_NONE)        return "PUBLISH_STATE_NONE";
		if (state == IServer.PUBLISH_STATE_UNKNOWN)     return "PUBLISH_STATE_UNKNOWN";
		return "STATE_XYZ_KNOWN";
	}

	public static String resolveServerDeltaKindState(int state) {
		if (state == ServerBehaviourDelegate.REMOVED)   return "REMOVED";
		if (state == ServerBehaviourDelegate.CHANGED)   return "CHANGED";
		if (state == ServerBehaviourDelegate.ADDED)     return "ADDED";
		if (state == ServerBehaviourDelegate.NO_CHANGE) return "NO_CHANGE";
		return "STATE_XYZ_KNOWN";
	}
}
