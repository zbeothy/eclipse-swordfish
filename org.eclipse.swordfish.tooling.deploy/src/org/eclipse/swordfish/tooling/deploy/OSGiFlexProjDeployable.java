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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.web.internal.deployables.ComponentDeployable;

public class OSGiFlexProjDeployable extends ComponentDeployable {

	private IPackageFragmentRoot[] cachedSourceContainers;
	private IContainer[] cachedOutputContainers;
	private HashMap cachedOutputMappings;
	private HashMap cachedSourceOutputPairs;

	/**
	 * Constructor for OSGiFlexProjDeployable.
	 * 
	 * @param project
	 * @param aComponent
	 */
	public OSGiFlexProjDeployable(IProject project, IVirtualComponent aComponent) {
		super(project, aComponent);
	}
	

	/**
	 * Constructor for OSGiFlexProjDeployable.
	 * 
	 * @param project
	 */
	public OSGiFlexProjDeployable(IProject project) {
		this(project,ComponentCore.createComponent(project));
	}

	/**
	 * Returns the root folders for the resources in this module.
	 * 
	 * @return a possibly-empty array of resource folders
	 */
	public IContainer[] getResourceFolders() {
		List result = new ArrayList();
		IVirtualComponent vc = ComponentCore.createComponent(getProject());
		if (vc != null) {
			IVirtualFolder vFolder = vc.getRootFolder();
			if (vFolder != null) {
				IContainer[] underlyingFolders = vFolder.getUnderlyingFolders();
				result.addAll(Arrays.asList(underlyingFolders));
			}
		}
		return (IContainer[]) result.toArray(new IContainer[result.size()]);
	}

	/**
	 * Returns the root folders containing Java output in this module.
	 * 
	 * @return a possibly-empty array of Java output folders
	 */
	public IContainer[] getJavaOutputFolders() {
		if (cachedOutputContainers == null)
			cachedOutputContainers = getJavaOutputFolders(getProject());
		return cachedOutputContainers;
	}
	
	public IContainer[] getJavaOutputFolders(IProject project) {
		if (project == null)
			return new IContainer[0];
		return OSGiProjectUtilities.getOutputContainers(project);
	}

    /**
     * Find the source container, if any, for the given file.
     * 
     * @param file
     * @return IPackageFragmentRoot sourceContainer for IFile
     */
    protected IPackageFragmentRoot getSourceContainer(IFile file) {
    	if (file == null)
    		return null;
    	IPackageFragmentRoot[] srcContainers = getSourceContainers();
    	for (int i=0; i<srcContainers.length; i++) {
    		IPath srcPath = srcContainers[i].getPath();
    		if (srcPath.isPrefixOf(file.getFullPath()))
    			return srcContainers[i];
    	}
    	return null;
    }
    
    /**
     * Either returns value from cache or stores result as value in cache for the corresponding
     * output container for the given source container.
     * 
     * @param sourceContainer
     * @return IContainer output container for given source container
     */
    protected IContainer getOutputContainer(IPackageFragmentRoot sourceContainer) {
    	if (sourceContainer == null)
    		return null;
    	
    	HashMap pairs = getCachedSourceOutputPairs();
    	IContainer output = (IContainer) pairs.get(sourceContainer);
    	if (output == null) {
    		output = OSGiProjectUtilities.getOutputContainer(getProject(), sourceContainer);
    		pairs.put(sourceContainer,output);
    	}
    	return output;
    }
    
	private IPackageFragmentRoot[] getSourceContainers() {
		if (cachedSourceContainers != null)
			return cachedSourceContainers;
		return OSGiProjectUtilities.getSourceContainers(getProject());
	}
    
	/**
	 * This method will return from cache or add to cache whether or not an output container
	 * is mapped in the virtual component.
	 * 
	 * @param outputContainer
	 * @return if output container is mapped
	 */
	private boolean isOutputContainerMapped(IContainer outputContainer) {
		if (outputContainer == null)
			return false;
		
		HashMap outputMaps = getCachedOutputMappings();
		Boolean result = (Boolean) outputMaps.get(outputContainer);
		if (result == null) {
			// If there are any component resources for the container, we know it is mapped
			if (ComponentCore.createResources(outputContainer).length > 0)
				result = Boolean.TRUE;	
			// Otherwise it is not mapped
			else
				result = Boolean.FALSE;
			// Cache the result in the map for this output container
			outputMaps.put(outputContainer, result);
		}
		return result.booleanValue();
	}
	
	/**
	 * Lazy initialize the cached output mappings
	 * @return HashMap
	 */
	private HashMap getCachedOutputMappings() {
		if (cachedOutputMappings==null)
			cachedOutputMappings = new HashMap();
		return cachedOutputMappings;
	}
	
	/**
	 * Lazy initialize the cached source - output pairings
	 * @return HashMap
	 */
	private HashMap getCachedSourceOutputPairs() {
		if (cachedSourceOutputPairs==null)
			cachedSourceOutputPairs = new HashMap();
		return cachedSourceOutputPairs;
	}
	
	/**
	 * This file should be added to the members list from the virtual component maps only if:
	 * a) it is not in a source folder
	 * b) it is in a source folder, and the corresponding output folder is a mapped component resource
	 * 
	 * @return boolean should file be added to members
	 */
	protected boolean shouldAddComponentFile(IFile file) {
		IPackageFragmentRoot sourceContainer = getSourceContainer(file);
		// If the file is not in a source container, return true
		if (sourceContainer==null) {
			return true;
		// Else if it is a source container and the output container is mapped in the component, return true
		// Otherwise, return false.
		} else {
			IContainer outputContainer = getOutputContainer(sourceContainer);
			return outputContainer!=null && isOutputContainerMapped(outputContainer);		
		}
	}

}
