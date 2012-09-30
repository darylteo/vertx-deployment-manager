//package com.darylteo.deploy.modules;
//
//import java.util.*;
//
//import org.vertx.java.core.Handler;
//import org.vertx.java.core.json.JsonObject;
//import org.vertx.java.deploy.Container;
//import org.vertx.java.deploy.Verticle;
//
//
//public class Deployments {
//
//	private Deployments that = this;
//	private List<Deployment> deployments = new LinkedList<>();
//
//	private Container container;
//
//	public Deployments(final Verticle verticle) {
//		this.container = verticle.getContainer();
//	}
//
//	/* Accessors */
//	public Deployment[] getDeployments() {
//		return this.deployments.toArray(
//				new Deployment[this.deployments.size()]);
//	}
//
//	public void deployModule(final Module module, final JsonObject config,
//			final Handler<Deployment> completeHandler) {
//
//		this.container.deployModule(module.getName(), config, 1,
//				new Handler<String>() {
//
//					@Override
//					public void handle(String deploymentID) {
//						System.out.println("Deployment Done : " + deploymentID);
//						Deployment deployment = new Deployment(deploymentID,
//								module);
//
//						that.deployments.add(deployment);
//
//						completeHandler.handle(deployment);
//					}
//
//				});
//
//	}
//
//	public void undeployModuleWithDeploymentID(
//			final Handler<Deployment> completeHandle) {
//
//		final Deployment deployment = this.deployments.remove(0);
//		
//		this.container.undeployModule(deployment.getDeploymentID(), new Handler<Void>() {
//			@Override
//			public void handle(Void v) {
//				System.out.println("Undeployment Done : " + deployment.getDeploymentID());
//				completeHandle.handle(deployment);
//			}
//		});
//	}
//
//}
