package com.darylteo.deploy.core;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.vertx.java.core.Handler;
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
	private Core that = this;

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
		public void ping(EventReply reply, Events events) {
			reply.send(new JsonObject().putString("reply", "pong"));
		}

		@Override
		public void loadAllInformation(EventReply reply, Events events) {
			JsonObject replyMessage = new JsonObject();
			JsonArray modulesArray = new JsonArray();
			JsonArray deploymentsArray = new JsonArray();

			Module[] modules = that.modules.getModules();
			Deployment[] deployments = that.deployments.getDeployments();

			for (Module module : modules) {
				modulesArray.addObject(new JsonObject().putString("module_name",
						module.getName()));
			}
			for (Deployment deployment : deployments) {
				deploymentsArray.addObject(new JsonObject().putString("module_name",
						deployment.getDeployedModule().getName()).putString(
						"deployment_id", deployment.getDeploymentID()));
			}

			replyMessage.putArray("modules", modulesArray);
			replyMessage.putArray("deployments", deploymentsArray);

			reply.send(replyMessage);
		}

		@Override
		public void getListOfModules(EventReply reply,
				Events events) {
			JsonObject replyMessage = new JsonObject();
			JsonArray modulesArray = new JsonArray();

			Module[] modules = that.modules.getModules();
			for (Module module : modules) {
				modulesArray.addObject(new JsonObject().putString("module_name",
						module.getName()));
			}

			replyMessage.putArray("modules", modulesArray);

			reply.send(replyMessage);
		}

		@Override
		public void getListOfDeployments(EventReply reply,
				Events events) {
			JsonObject replyMessage = new JsonObject();
			JsonArray deploymentsArray = new JsonArray();

			Deployment[] deployments = that.deployments.getDeployments();
			for (Deployment deployment : deployments) {
				deploymentsArray.addObject(new JsonObject().putString("module_name",
						deployment.getDeployedModule().getName()).putString(
						"deployment_id", deployment.getDeploymentID()));
			}

			replyMessage.putArray("deployments", deploymentsArray);

			reply.send(replyMessage);
		}

		@Override
		public void deployModule(final JsonObject message,
				final EventReply reply, final Events events) {
			final String moduleName = message.getString("module_name");

			try {
				Module module = that.modules.getModuleWithName(moduleName);

				that.deployments.deployModule(module, new JsonObject(), 1,
						new Handler<String>() {
							@Override
							public void handle(String deploymentID) {
								reply.send(new JsonObject().putString(
										"success", "1"));
								events.notifyNewDeployment(moduleName,
										deploymentID);
							}

						});
			} catch (Exception e) {
				reply.send(new JsonObject().putString("success", "0"));
			}
		}

		@Override
		public void undeployModule(JsonObject message, EventReply reply,
				Events events) {
			String deploymentID = message.getString("deployment_id");

			try {
				that.deployments
						.undeployModuleWithDeploymentID(deploymentID);
				reply.send(new JsonObject().putString("success", "1"));
			} catch (Exception e) {
				reply.send(new JsonObject().putString("success", "0"));
			}
		}



	}
}
