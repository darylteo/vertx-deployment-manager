package com.darylteo.deploy.events;

import org.vertx.java.core.json.*;

public interface EventsHandler {
	/* .ping */
	void ping(JsonObject message, EventReply reply);

	/* .list-modules */
	void getListOfModules(JsonObject message, EventReply reply);

	/* .list-deployments */
	void getListOfDeployments(JsonObject message, EventReply reply);

	/* .deploy-module */
	void deployModule(JsonObject message, EventReply reply);

	/* .undeploy-module */
	void undeployModule(JsonObject message, EventReply reply);
}
