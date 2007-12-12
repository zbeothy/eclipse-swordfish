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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.server.core.PublishUtil;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.ModuleFile;
import org.eclipse.wst.server.core.internal.ModuleFolder;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.PublishOperation;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;

/**
 * Swordfish server behaviour implementation.
 * 
 * @author Alex Tselesh
 */
public class SwordfishServerBehaviourDelegate extends ServerBehaviourDelegate {

	private static final String PUBLISHED_STATE_STORAGE = "publish.txt";

	private SwordfishServerOSGiFrameworkMediator osgiMediator;
	
	/**
	 * SwordfishServerBehaviour.
	 */
	public SwordfishServerBehaviourDelegate() {
		super();
		Logger.info("SwordfishServerBehaviourDelegate()");
		
		osgiMediator = new SwordfishServerOSGiFrameworkMediator(this);
	}

	public void initialize(IProgressMonitor monitor) {
		Logger.info("SwordfishServerBehaviourDelegate.initialize()");

		IPath path = getTempDirectory().append(PUBLISHED_STATE_STORAGE);
		Properties p = readPublishedData(path);

		p = osgiMediator.init(p);
		
		storePublishedData(path , p);
	}

	public SwordfishOSGiRuntimeDelegate getSwordfishRuntime() {
		if (getServer().getRuntime() == null)
			return null;
		
		return (SwordfishOSGiRuntimeDelegate) getServer().getRuntime().loadAdapter(SwordfishOSGiRuntimeDelegate.class, null);
	}

	public SwordfishServerDelegate getSwordfishServer() {
		return (SwordfishServerDelegate) getServer().loadAdapter(SwordfishServerDelegate.class, null);
	}

	protected void setServerStarted() {
		setServerState(IServer.STATE_STARTED);
	}

	final void setChangedModuleState(IModule[] module, int state) {
		super.setModuleState(module, state);
	}
	
	protected void stopImpl() throws CoreException {
		
		IModule[] modules = getServer().getModules();
		if (null != modules) {
			for (int i = 0, count = modules.length; i < count; i++) {
				IModule module = modules[i];
				
				this.stopModule(new IModule[] {module}, new NullProgressMonitor());
				
				this.setModuleState(new IModule[] {module}, IServer.STATE_UNKNOWN);
			}
		}
		
		setServerState(IServer.STATE_STOPPED);
	}

	protected void publishServer(int kind, IProgressMonitor monitor) throws CoreException {
		
		Logger.info("SwordfishServerBehaviourDelegate.publishServer(kind: " + OSGiProjectUtilities.resolvePublishKindState(kind) + ", IProgressMonitor: " + monitor + ")");

		Logger.info("SwordfishServerBehaviourDelegate.publishServer(...): this.getServer() - " + getServer());
		Logger.info("SwordfishServerBehaviourDelegate.publishServer(...): this.getServer().getRuntime() - " + getServer().getRuntime());
		
//		if (getServer().getRuntime() == null)
//			return;

		monitor = (null == monitor) ? new NullProgressMonitor() : monitor;
		monitor.done();

		setServerPublishState(IServer.PUBLISH_STATE_NONE);
	}

	private Properties readPublishedData(IPath path) {
		Properties p = new Properties();
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(path.toFile());
			p.load(fin);
		} catch (Exception e) {
			// ignore
		} finally {
			try {
				fin.close();
			} catch (Exception ex) {
				// ignore
			}
		}
		return p;
	}
	
	private void storePublishedData(IPath path, Properties p) {
		try {
			p.store(new FileOutputStream(path.toFile()), "Swordfish publish data");
		} catch (Exception e) {
			// ignore
		}
	}
	
	/*
	 * Publishes the given module to the server.
	 */
	protected void publishModule(int kind, int deltaKind, IModule[] moduleTree, IProgressMonitor monitor) throws CoreException {

		Logger.info("SwordfishServerBehaviourDelegate.publishModule(kind: " + OSGiProjectUtilities.resolvePublishKindState(kind)
						+ ", deltaKind: " + OSGiProjectUtilities.resolveServerDeltaKindState(deltaKind)
						+ ", IModule[] moduleTree: " + ((null == moduleTree) ? "null" : "[" + moduleTree.length + "]")
						+ ", IProgressMonitor: " + monitor + ")");
		Logger.info("\tIModule[] moduleTree - " + OSGiProjectUtilities.printModulesInfo(moduleTree));
		

		IModule[] installedModules = getServer().getModules();
		Logger.info("SwordfishServerBehaviourDelegate.publishModule(...) - already installed modules: "
						+ OSGiProjectUtilities.printModulesInfo(installedModules));
		
		if (getServer().getServerState() != IServer.STATE_STOPPED) {
			if (deltaKind == ServerBehaviourDelegate.ADDED || deltaKind == ServerBehaviourDelegate.REMOVED)
				setServerRestartState(true);
		}

		IPath path = getTempDirectory().append(PUBLISHED_STATE_STORAGE);
		Properties p = readPublishedData(path);


		publishBundle(kind, deltaKind, p, moduleTree, monitor);

		setModulePublishState(moduleTree, IServer.PUBLISH_STATE_NONE);
		
		storePublishedData(path, p);
	}
	

	private void publishBundle(int kind, int deltaKind, Properties p, IModule[] module, IProgressMonitor monitor) throws CoreException {
		
		Logger.info("SwordfishServerBehaviourDelegate.publishBundle(kind: " + OSGiProjectUtilities.resolvePublishKindState(kind) + ", deltaKind: " + OSGiProjectUtilities.resolveServerDeltaKindState(deltaKind) + ", Properties: " + p + ", IModule[]: " + ((null == module) ? "null" : "[" + module.length + "]") + ", IProgressMonitor: " + monitor + ")");
		Logger.info("IModule[] module - " + OSGiProjectUtilities.printModulesInfo(module));
		
		String moduleId = module[0].getId();
		boolean alreadyPublished = (null != osgiMediator.getInstalledBundleLocation(moduleId));
		Logger.info("SwordfishServerBehaviourDelegate.publishBundle(): is module already published - " + alreadyPublished);
		
		IPath jarPath = osgiMediator.getBundleLocation(module[0]);
		Logger.info("SwordfishServerBehaviourDelegate.publishBundle(...): jarPath - " + jarPath);

		if (deltaKind == REMOVED) {
			try {
				osgiMediator.uninstall(module[0]);
				//SwordfishOSGiFramework.getInstance().uninstallBundle(moduleId);
				try { // delete bundle jar from deploy directory
					String publishPath = (String) p.get(moduleId);
					new File(publishPath).delete();
				} catch (Exception e) {
					throw new CoreException(new Status(IStatus.WARNING, DeployPlugin.PLUGIN_ID, 0, "Could not remove module", e));
				}
			} catch (SwordfishOSGiFrameworkException e) {
				Logger.error("Error unistall bundle", e);
			}
			setModuleState(module, IServer.STATE_UNKNOWN);
		} else {
			IModuleResource[] mr = getResources(module);
			Logger.info("SwordfishServerBehaviourDelegate.publishBundle(...): IModuleResource[" + mr.length + "] - " + mr);
			IStatus[] stat = PublishUtil.publishZip(mr, jarPath, monitor);
			List status = new ArrayList();
			SwordfishPublishOperation.addArrayToList(status, stat);
			SwordfishPublishOperation.throwException(status);
			p.put(moduleId, jarPath.toOSString());

			setModuleState(module, IServer.STATE_UNKNOWN);
			
			boolean isStateStarted = true;
			try {
				//isStateStarted = SwordfishOSGiFramework.getInstance().deployBundle(jarPath.toOSString());
				osgiMediator.install(module[0]);
				isStateStarted = IServer.STATE_STARTED == osgiMediator.getModuleState(module[0]);
			} catch (SwordfishOSGiFrameworkException e) {
				IStatus errorStatus = new Status(IStatus.ERROR, DeployPlugin.PLUGIN_ID, 0, "Could not deploy module", e);
				Logger.log(errorStatus);
				throw new CoreException(errorStatus);
			}

			Logger.info("SwordfishServerBehaviourDelegate.publishBundle(...): after publish module state - " + OSGiProjectUtilities.resolveModuleState(getServer().getModuleState(module)));
			Logger.info("SwordfishServerBehaviourDelegate.publishBundle(...): after publish is bundle active - " + isStateStarted);
			if (isStateStarted) {
				setModuleState(module, IServer.STATE_STARTED);
			} else {
//				setModuleState(module, IServer.STATE_UNKNOWN);
				startModule(module, monitor);
			}
		}
	}
	
	protected void publishFinish(IProgressMonitor monitor) throws CoreException {
        IModule[] modules = this.getServer().getModules();
        boolean allpublished= true;
        for (int i = 0; i < modules.length; i++) {
        	if(this.getServer().getModulePublishState(new IModule[]{modules[i]})!=IServer.PUBLISH_STATE_NONE)
                allpublished=false;
        }
        if(allpublished)
            setServerPublishState(IServer.PUBLISH_STATE_NONE);
	}

    /**
     * Setup for starting the server. Checks all ports available 
     * and sets server state and mode.
     * 
     * @param launch ILaunch
     * @param launchMode String
     * @param monitor IProgressMonitor
     */
    protected void setupLaunch(ILaunch launch, String launchMode, IProgressMonitor monitor)
    		throws CoreException {

    	setServerRestartState(false);
    	setServerState(IServer.STATE_STARTING);

		IModule[] modules = getServer().getModules();
		if (null != modules) {
			for (int i = 0, count = modules.length; i < count; i++) {
				IModule module = modules[i];
				this.startModule(new IModule[] {module}, monitor);
			}
		}
    	
    	setMode(launchMode);
    }

	/**
	 * Cleanly shuts down and terminates the server.
	 * 
	 * @param force <code>true</code> to kill the server
	 */
	public void stop(boolean force) {
		Logger.info("SwordfishServerBehaviourDelegate.stop(force: " + force + ")");

		if (force) {
			terminate();
			return;
		}
		int state = getServer().getServerState();
		if (state == IServer.STATE_STOPPED)
			return;
		else if (state == IServer.STATE_STARTING || state == IServer.STATE_STOPPING) {
			terminate();
			return;
		}
		
		// should really try to stop normally
		terminate();
	}

	
	
	/**
	 * Terminates the server.
	 */
	protected void terminate() {
		Logger.info("SwordfishServerBehaviourDelegate.terminate()");

		if (getServer().getServerState() == IServer.STATE_STOPPED)
			return;

		try {
			setServerState(IServer.STATE_STOPPING);

			Logger.info("Killing the Swordfish process");

			stopImpl();

		} catch (Exception e) {
			Logger.error("Error killing the process", e);
		}
	}

	public String toString() {
		return "SwordfishServer";
	}

	protected IModuleResource[] getResources(IModule[] module) {

		Logger.info("SwordfishServerBehaviourDelegate.getResources(IModule[] module: " + ((null == module) ? "null" : "[" + module.length + "]") + ")");
		Logger.info("IModule[] module - " + OSGiProjectUtilities.printModulesInfo(module));

//		return super.getResources(module);
		try {
			List list = new ArrayList();
			
			IJavaProject javaProject = JavaCore.create(module[0].getProject());
			IPath outputFolderPath = javaProject.getOutputLocation();

			IFolder outputFolder = module[0].getProject().getWorkspace().getRoot().getFolder(outputFolderPath);
			Logger.info("\t\t SwordfishServerBehaviourDelegate.getResources(...) output folder - " + outputFolder);
			list.addAll(Arrays.asList(getModuleResources(Path.EMPTY, outputFolder)));
			
			
			IFolder metainfFolder = module[0].getProject().getFolder("META-INF");
			//IFile metainfFile = metainfFolder.getFile("MANIFEST.MF");
			//metainfFile.getContents()
			
			Logger.info("\t\t SwordfishServerBehaviourDelegate.getResources(...) metainf folder - " + metainfFolder);
			list.addAll(Arrays.asList(getModuleResources(new Path("META-INF"), metainfFolder)));
			
			IModuleResource[] moduleResources = new IModuleResource[list.size()];
			list.toArray(moduleResources);

			return moduleResources;
		} catch (CoreException e) {
			Logger.error(e);
		}
		return new IModuleResource[0];
	}

	
	/**
	 * Return the module resources for a given path.
	 * TODO: use from OSGiFlexProjDeployable (which extends public abstract class org.eclipse.wst.server.core.util.ProjectModule extends ModuleDelegate)
	 * 
	 * @param path a path
	 * @param container a container
	 * @return an array of module resources
	 * @throws CoreException
	 */
	protected IModuleResource[] getModuleResources(IPath path, IContainer container) throws CoreException {
		IResource[] resources = container.members();
		if (resources != null) {
			int size = resources.length;
			List list = new ArrayList(size);
			for (int i = 0; i < size; i++) {
				IResource resource = resources[i];
				if (resource != null && resource.exists()) {
					String name = resource.getName();
					if (resource instanceof IContainer) {
						IContainer container2 = (IContainer) resource;
						ModuleFolder mf = new ModuleFolder(container2, name, path);
						mf.setMembers(getModuleResources(path.append(name), container2));
						list.add(mf);
					} else if (resource instanceof IFile) {
						ModuleFile mf = new ModuleFile((IFile) resource, name, path);
						list.add(mf);
					}
				}
			}
			IModuleResource[] moduleResources = new IModuleResource[list.size()];
			list.toArray(moduleResources);
			return moduleResources;
		}
		return new IModuleResource[0];
	}

	// just for logging
	
	protected void addRemovedModules(List moduleList, List kindList) {

		Logger.info("SwordfishServerBehaviourDelegate.addRemovedModules(List: " + ((null == moduleList) ? "null" : "{" + moduleList.size() + "}") + ", List: " + kindList + ")");
		Logger.info("\t IModule[] moduleList - " + OSGiProjectUtilities.printIModules(moduleList));
		super.addRemovedModules(moduleList, kindList);
	}

	public boolean canControlModule(IModule[] module) {
		// fake
		return true;
	}

	protected IModuleResource[] getPublishedResources(IModule[] module) {

		Logger.info("SwordfishServerBehaviourDelegate.getPublishedResources(IModule[]: " + ((null == module) ? "null" : "[" + module.length + "]") + ")");
		Logger.info("\t IModule[] module - " + OSGiProjectUtilities.printModulesInfo(module));
		return super.getPublishedResources(module);
	}

	protected boolean hasBeenPublished(IModule[] module) {

		Logger.info("SwordfishServerBehaviourDelegate.hasBeenPublished(IModule[]: " + ((null == module) ? "null" : "[" + module.length + "]") + ")");
		Logger.info("\tIModule[] module - " + OSGiProjectUtilities.printModulesInfo(module));
		boolean hasBeenPublished = super.hasBeenPublished(module);
		Logger.info("SwordfishServerBehaviourDelegate.hasBeenPublished(...) - " + hasBeenPublished);
		return hasBeenPublished;
	}

	protected MultiStatus performTasks(PublishOperation[] tasks, IProgressMonitor monitor) {

		Logger.info("SwordfishServerBehaviourDelegate.performTasks(PublishOperation[]: [" + ((null == tasks) ? "null" : tasks.length) + "], IProgressMonitor: " + monitor + ")");
		if (null == tasks) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0 , count = tasks.length; i < count; i++) {
				PublishOperation pubOp = tasks[i];
				sb.append("SwordfishServerBehaviourDelegate.performTasks: PublishOperation [" + i + "]");
				sb.append("\t\t label - " + pubOp.getLabel());
				sb.append("\t\t description - " + pubOp.getDescription());
				sb.append("\t\t kind - " + pubOp.getKind());
				sb.append("\t\t order - " + pubOp.getOrder());
				sb.append("\t\t task model - " + pubOp.getTaskModel());
				sb.append("\n");
			}
			Logger.info(sb.toString());
		}
		return super.performTasks(tasks, monitor);
	}

	public void startModule(IModule[] module, IProgressMonitor monitor) throws CoreException {

		Logger.info("SwordfishServerBehaviourDelegate.startModule(IModule[]: " + ((null == module) ? "null" : "[" + module.length + "]") + ", IProgressMonitor: " + monitor + ")");
		Logger.info("\t IModule[] module - " + OSGiProjectUtilities.printModulesInfo(module));

		int state = getServer().getModuleState(module);
		if (IServer.STATE_STARTED != state && IServer.STATE_STARTING != state) {

			Logger.info("\t [before start] module status - " + getModuleStatus(module) + ", module state - " + OSGiProjectUtilities.resolveModuleState(state));
			
			setModuleState(module, IServer.STATE_STARTING);

			try {
				//SwordfishOSGiFramework.getInstance().startBundle(module[0].getId());
				osgiMediator.start(module[0]);
			} catch (SwordfishOSGiFrameworkException e) {
				setModuleState(module, IServer.STATE_UNKNOWN);
				Logger.error("Error starting bundle", e);
				return;
			}
			
			setModuleState(module, IServer.STATE_STARTED);
	
			state = getServer().getModuleState(module);
			Logger.info("\t [after start] module status - " + getModuleStatus(module) + ", module state - " + OSGiProjectUtilities.resolveModuleState(state));
		}
	}

	public void stopModule(IModule[] module, IProgressMonitor monitor) throws CoreException {

		Logger.info("SwordfishServerBehaviourDelegate.stopModule(IModule[]: " + ((null == module) ? "null" : "[" + module.length + "]") + ", IProgressMonitor: " + monitor + ")");
		Logger.info("\t IModule[] module - " + OSGiProjectUtilities.printModulesInfo(module));

		int state = getServer().getModuleState(module);
		if (IServer.STATE_STOPPED != state && IServer.STATE_STOPPING != state) {
			Logger.info("\t [before stop] module status - " + getModuleStatus(module) + ", module state - " + OSGiProjectUtilities.resolveModuleState(state));
	
			setModuleState(module, IServer.STATE_STOPPING);

			try {
				//SwordfishOSGiFramework.getInstance().stopBundle(module[0].getId());
				osgiMediator.stop(module[0]);
			} catch (SwordfishOSGiFrameworkException e) {
				setModuleState(module, IServer.STATE_UNKNOWN);
				Logger.error("Error stoping bundle", e);
				return;
			}
			
			setModuleState(module, IServer.STATE_STOPPED);
	
			state = getServer().getModuleState(module);
			Logger.info("\t [after stop] module status - " + getModuleStatus(module) + ", module state - " + OSGiProjectUtilities.resolveModuleState(state));
		}
	}
	
	private IStatus getModuleStatus(IModule[] module) {
		return ((Server) getServer()).getModuleStatus(module);
	}
}
