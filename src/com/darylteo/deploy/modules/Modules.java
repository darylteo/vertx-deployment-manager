package com.darylteo.deploy.modules;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.*;

import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

import com.darylteo.deploy.events.Events;

public class Modules implements ModuleLoaderDelegate {
	/* Instance Variables */
	private final Modules that = this;
	private final Verticle verticle;

	private final Events events;

	private final ModuleLoader loader;

	private final Map<String, Module> modules = new HashMap<>();
	private final Map<String, List<Deployment>> deployments = new HashMap<>();

	/* Constructors */
	public Modules(final Verticle verticle, final Path modulesDir,
			final Events events) throws Exception {
		this.verticle = verticle;
		this.events = events;
		this.loader = new ModuleLoader(modulesDir, this);
	}

	public Modules(final Verticle verticle, final String modulesDir,
			final Events events) throws Exception {
		this(verticle, Paths.get(modulesDir), events);
	}

	/* Accessors */
	public Module[] getModules() {
		return this.modules.values().toArray(new Module[this.modules.size()]);
	}

	public Deployment[] getDeployments() {
		List<Deployment> result = new LinkedList<>();

		for (List<Deployment> deploymentList : this.deployments.values()) {
			result.addAll(deploymentList);
		}

		return result.toArray(new Deployment[result.size()]);
	}

	public Module getModuleWithName(String name) throws Exception {
		if (!this.modules.containsKey(name)) {
			// TODO: Custom Exception
			throw new Exception("Module Not Found");
		}

		return this.modules.get(name);
	}

	/* Deployment Methods */
	public void deployModule(final String moduleName, final JsonObject config,
			final Handler<Deployment> completeHandler) throws Exception {

		final Module module = this.getModuleWithName(moduleName);
		final List<Deployment> deployments = this.deployments.get(moduleName);

		this.verticle.getContainer().deployModule(moduleName, config, 1,
				new Handler<String>() {

					@Override
					public void handle(String deploymentID) {
						System.out.println("Deployment Done : " + deploymentID);
						Deployment deployment = new Deployment(deploymentID,
								module);

						deployments.add(deployment);

						completeHandler.handle(deployment);
					}

				});

	}

	public void undeployModule(final String moduleName,
			final Handler<Deployment> completeHandle) throws Exception {

		final Module module = this.getModuleWithName(moduleName);
		final List<Deployment> deployments = this.deployments.get(moduleName);
		final Deployment deployment = deployments.remove(0);

		this.verticle.getContainer().undeployModule(
				deployment.getDeploymentID(), new Handler<Void>() {
					@Override
					public void handle(Void v) {
						System.out.println("Undeployment Done : "
								+ deployment.getDeploymentID());
						completeHandle.handle(deployment);
					}
				});
	}

	/* Module Loader Delegate methods */
	@Override
	public void moduleInstalled(String moduleName) {
		Module m = new Module(moduleName, "main");
		this.modules.put(moduleName, m);
		this.deployments.put(moduleName, new LinkedList<Deployment>());
		
		this.events.moduleInstalled(moduleName);
	}

	@Override
	public void moduleModified(String moduleName) {
		this.events.moduleModified(moduleName);
	}

	@Override
	public void moduleUninstalled(String moduleName) {
		this.modules.remove(moduleName);
		this.deployments.remove(moduleName);
		
		this.events.moduleUninstalled(moduleName);
	}

}
