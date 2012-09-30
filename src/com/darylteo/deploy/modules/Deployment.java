package com.darylteo.deploy.modules;


public class Deployment {
	private String deploymentID;
	private Module deployedModule;
	
	/* Constructor */
	public Deployment(String deploymentID, Module deployedModule) {
		super();
		this.deploymentID = deploymentID;
		this.deployedModule = deployedModule;
	}
	
	/* Accessors */
	public String getDeploymentID() {
		return deploymentID;
	}
	public void setDeploymentID(String deploymentID) {
		this.deploymentID = deploymentID;
	}
	public Module getDeployedModule() {
		return deployedModule;
	}
	public void setDeployedModule(Module deployedModule) {
		this.deployedModule = deployedModule;
	}
	
	
}
