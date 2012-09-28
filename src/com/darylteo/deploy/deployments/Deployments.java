package com.darylteo.deploy.deployments;

import java.util.*;

import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Container;
import org.vertx.java.deploy.Verticle;

import com.darylteo.deploy.modules.Module;

public class Deployments {

	private Map<String, Deployment> deployments = new HashMap<>();

	private Container container;

	public Deployments(final Verticle verticle) {
		this.container = verticle.getContainer();
	}

	/* Accessors */
	public Deployment[] getDeployments() {
		return this.deployments.values().toArray(new Deployment[this.deployments.size()]);
	}	
	

	public void deployModule(Module module) {
		this.deployModule(module, new JsonObject());
	}

	public void deployModule(Module module, JsonObject config) {
		this.deployModule(module, config, 1);
	}

	public void deployModule(final Module module, final JsonObject config,
			final int instances) {

		this.container.deployModule(module.getName(), config, instances,
				new Handler<String>() {

					@Override
					public void handle(String deploymentID) {
						System.out.println("Deployment Done : " + deploymentID);
						Deployment deployment = new Deployment(deploymentID,
								module);

						Deployments.this.deployments.put(deploymentID,
								deployment);
					}

				});

	}

	public void undeployModuleWithDeploymentID(final String deploymentID) {
		if (!this.deployments.containsKey(deploymentID)) {
			// TODO: Error Handling?
			return;
		}

		this.container.undeployModule(deploymentID, new Handler<Void>() {
			@Override
			public void handle(Void v) {
				Deployments.this.deployments.remove(deploymentID);
			}
		});
	}

}
