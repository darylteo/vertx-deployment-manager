package com.darylteo.deploy.events;

import org.vertx.java.core.json.JsonObject;

public interface EventsHandler {
	/* .ping */
	void ping(EventReply reply, Events events);

	/* .load-all */
	void loadAllInformation(EventReply reply, Events events);

	/* .list-modules */
	void getListOfModules(EventReply reply, Events events);

	/* .list-deployments */
	void getListOfDeployments(EventReply reply, Events events);

	/* .deploy-module */
	void deployModule(JsonObject message, EventReply reply, Events events);

	/* .undeploy-module */
	void undeployModule(JsonObject message, EventReply reply, Events events);
}
