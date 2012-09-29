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
		
		this.eb.registerHandler("deployment-manager.server.ping", new Handler<Message<JsonObject>>() {
			@Override
			public void handle(Message<JsonObject> message) {
				System.out.println("EventBus Received Message: Ping");
				Events.this.eventsHandler.ping(message.body, new EventReply(message));
			}
		});

		this.eb.registerHandler("deployment-manager.server.list-modules", new Handler<Message<JsonObject>>() {
			@Override
			public void handle(Message<JsonObject> message) {
				System.out.println("EventBus Received Message: List Modules");
				Events.this.eventsHandler.getListOfModules(message.body, new EventReply(message));
			}
		});
		
		this.eb.registerHandler("deployment-manager.server.list-deployments", new Handler<Message<JsonObject>>() {
			@Override
			public void handle(final Message<JsonObject> message) {
				System.out.println("EventBus Received Message: List Deployments");
				Events.this.eventsHandler.getListOfDeployments(message.body, new EventReply(message));
			}
		});
		
		this.eb.registerHandler("deployment-manager.server.deploy-module", new Handler<Message<JsonObject>>() {
			@Override
			public void handle(final Message<JsonObject> message) {
				System.out.println("EventBus Received Message: Deploy Module");
				Events.this.eventsHandler.deployModule(message.body, new EventReply(message));
			}
		});
		
		this.eb.registerHandler("deployment-manager.server.undeploy-module", new Handler<Message<JsonObject>>() {
			@Override
			public void handle(final Message<JsonObject> message) {
				System.out.println("EventBus Received Message: Undeploy Module");
				Events.this.eventsHandler.undeployModule(message.body, new EventReply(message));
			}
		});
	}
}
