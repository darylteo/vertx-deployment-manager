package com.darylteo.deploy.modules;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.*;

import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

public class Modules {
	/* Instance Variables */
	private Verticle verticle;

	private Path modulesDir;
	private Map<String, Module> modules = new HashMap<>();
	private Map<String, List<Deployment>> deployments = new HashMap<>();

	/* Constructors */
	public Modules(final Verticle verticle, Path modulesDir) {
		this.modulesDir = modulesDir;
		this.verticle = verticle;

		loadModules();
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

	/* Private Methods */
	private void loadModules() {
		System.out.printf("Looking for Modules In %s\n", this.modulesDir);

		File f = this.modulesDir.toFile();
		if (!f.exists()) {
			// Modules Directory does not exist
			System.out.printf("Modules Directory %s Does Not Exist!\n",
					this.modulesDir);
		}

		String[] files = f.list();

		for (String moduleName : files) {
			try {
				validateModule(moduleName);

				Module m = new Module(moduleName, "main");
				this.modules.put(moduleName, m);
				this.deployments.put(moduleName, new LinkedList<Deployment>());

				System.out.printf("Module Loaded: %s\n", moduleName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void validateModule(String moduleName) throws Exception {
		Path modulePath = this.modulesDir.resolve(moduleName);
		System.out.printf("Checking Module in Directory: %s\n", modulePath);

		File dir = modulePath.toFile();
		if (!dir.isDirectory()) {
			// File is not a Directory, skip this one... We're only looking
			throw new Exception("Non-Directory detected in Module Directory.");
		}

		File json = modulePath.resolve("mod.json").toFile();

		if (!json.exists()) {
			throw new Exception(
					"mod.json Configuration Found not found for Module.");
		}
	}
}
