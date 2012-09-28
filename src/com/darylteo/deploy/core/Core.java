package com.darylteo.deploy.core;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

import com.darylteo.deploy.deployments.Deployment;
import com.darylteo.deploy.deployments.Deployments;
import com.darylteo.deploy.events.EventReply;
import com.darylteo.deploy.events.EventsHandler;
import com.darylteo.deploy.events.Events;
import com.darylteo.deploy.modules.Module;
import com.darylteo.deploy.modules.Modules;

public class Core {

	private Modules modules;
	private Deployments deployments;

	private Events events;
	private EventsHandler eventsHandler;

	public Core(final Verticle verticle) {
		this.modules = this.loadModules();

		this.deployments = this.loadDeployments(verticle);

		this.eventsHandler = new MyHandler();
		this.events = this.loadEvents(verticle, this.eventsHandler);
	}

	private Modules loadModules() {
		Path workingDir = Paths.get(System.getProperty("user.dir"));
		Path modsDir = workingDir.resolve("mods");

		System.out.printf("Running Deployment Manager In %s\n", workingDir);

		return new Modules(modsDir);
	}

	private Deployments loadDeployments(final Verticle verticle) {
		Deployments deployments = new Deployments(verticle);

		return deployments;
	}

	private Events loadEvents(final Verticle verticle,
			final EventsHandler eventsHandler) {
		Events events = new Events(verticle, eventsHandler);

		return events;
	}

	/* Events Handler */
	private class MyHandler implements EventsHandler {

		@Override
		public void ping(JsonObject message, EventReply reply) {
			reply.send(new JsonObject().putString("reply", "pong"));
		}

		@Override
		public void getListOfModules(JsonObject message, EventReply reply) {
			JsonObject replyMessage = new JsonObject();
			JsonArray replyArray = new JsonArray();

			Module[] modules = Core.this.modules.getModules();

			for (Module module : modules) {
				replyArray.addObject(new JsonObject().putString("name",
						module.getName()));
			}

			replyMessage.putArray("modules", replyArray);

			reply.send(replyMessage);
		}

		@Override
		public void getListOfDeployments(JsonObject message, EventReply reply) {
			JsonObject replyMessage = new JsonObject();
			JsonArray replyArray = new JsonArray();

			Deployment[] deployments = Core.this.deployments.getDeployments();

			for (Deployment deployment : deployments) {
				replyArray.addObject(new JsonObject().putString("name",
						deployment.getDeployedModule().getName()).putString(
						"deploymentID", deployment.getDeploymentID()));
			}

			replyMessage.putArray("deployments", replyArray);

			reply.send(replyMessage);
		}

		@Override
		public void deployModule(JsonObject message, EventReply reply) {
			String moduleName = message.getString("module_name");
			
			try{
				Module module = Core.this.modules.getModuleWithName(moduleName);
				
				Core.this.deployments.deployModule(module);
				reply.send(new JsonObject().putString("success", "1"));
			}catch(Exception e){
				reply.send(new JsonObject().putString("success", "0"));
			}
		}

		@Override
		public void undeployModule(JsonObject message, EventReply reply) {
			String deploymentID = message.getString("deployment_id");
			
			try{			
				Core.this.deployments.undeployModuleWithDeploymentID(deploymentID);
				reply.send(new JsonObject().putString("success", "1"));
			}catch(Exception e){
				reply.send(new JsonObject().putString("success", "0"));
			}
		}

	}
}
