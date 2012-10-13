package com.darylteo.deploy.modules;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.*;

import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

public class Modules implements ModuleLoaderDelegate {
	/* Instance Variables */
	private Modules that = this;

	private Verticle verticle;

	private ModuleLoader loader;
	private Map<String, Module> modules = new HashMap<>();
	private Map<String, List<Deployment>> deployments = new HashMap<>();

	/* Constructors */
	public Modules(final Verticle verticle, Path modulesDir) {
		this.verticle = verticle;

		try {
			this.loader = new ModuleLoader(modulesDir, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Modules(final Verticle verticle, String modulesDir) {
		this(verticle, Paths.get(modulesDir));
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
		System.out.println("Module Installed:" + moduleName);
		Module m = new Module(moduleName, "main");
		that.modules.put(moduleName, m);
		that.deployments.put(moduleName, new LinkedList<Deployment>());
	}

	@Override
	public void moduleModified(String moduleName) {
		System.out.println("Module Modified:" + moduleName);
	}

	@Override
	public void moduleUninstalled(String moduleName) {
		System.out.println("Module Uninstalled:" + moduleName);
		that.modules.remove(moduleName);
		that.deployments.remove(moduleName);
	}

}
