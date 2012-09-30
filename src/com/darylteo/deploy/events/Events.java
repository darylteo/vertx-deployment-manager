package com.darylteo.deploy.events;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

public class Events {
	private EventsHandler eventsHandler;
	private EventBus eb;

	public Events(final Verticle verticle, final EventsHandler eventHandler) {
		this.eventsHandler = eventHandler;

		this.eb = verticle.getVertx().eventBus();

		final Events that = this;

		this.registerHandler("ping", new Handler<Message<JsonObject>>() {
			@Override
			public void handle(Message<JsonObject> message) {
				System.out.println("EventBus Received Message: Ping");
				that.eventsHandler.ping(new EventReply(
						message), that);
			}
		});

		this.registerHandler("load-all", new Handler<Message<JsonObject>>() {
			@Override
			public void handle(Message<JsonObject> message) {
				System.out
				.println("EventBus Received Message: Load All Data");

				that.eventsHandler.getListOfModules(new EventReply(message), that);
			}
		});

		this.registerHandler("list-modules",
				new Handler<Message<JsonObject>>() {
					@Override
					public void handle(Message<JsonObject> message) {
						System.out
								.println("EventBus Received Message: List Modules");
						that.eventsHandler.getListOfModules(
								new EventReply(message), that);
					}
				});

		this.registerHandler("list-deployments",
				new Handler<Message<JsonObject>>() {
					@Override
					public void handle(final Message<JsonObject> message) {
						System.out
								.println("EventBus Received Message: List Deployments");
						that.eventsHandler.getListOfDeployments(
								new EventReply(message), that);
					}
				});

		this.registerHandler("deploy-module",
				new Handler<Message<JsonObject>>() {
					@Override
					public void handle(final Message<JsonObject> message) {
						System.out
								.println("EventBus Received Message: Deploy Module");
						that.eventsHandler.deployModule(message.body,
								new EventReply(message), that);
					}
				});

		this.registerHandler("undeploy-module",
				new Handler<Message<JsonObject>>() {
					@Override
					public void handle(final Message<JsonObject> message) {
						System.out
								.println("EventBus Received Message: Undeploy Module");
						that.eventsHandler.undeployModule(message.body,
								new EventReply(message), that);
					}
				});
	}

	/* Private Methods */
	private void registerHandler(final String address,
			final Handler<Message<JsonObject>> handler) {
		final String serverAddress = "deployment-manager.server." + address;

		this.eb.registerHandler(serverAddress, handler);
	}

	/* Event Sending */
	public void notifyNewModule(String moduleName) {

		this.eb.send("deployment-manager.client.new-module",
				new JsonObject().putString("module_name", moduleName));
	}

	public void notifyNewDeployment(String moduleName, String deploymentID) {
		System.out.printf("New Deployment Notificiation {%s: %s}\n",
				moduleName, deploymentID);
		this.eb.send("deployment-manager.client.new-deployment",
				new JsonObject().putString("module_name", moduleName)
						.putString("deployment_id", deploymentID));
	}
}
