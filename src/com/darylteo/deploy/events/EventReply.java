package com.darylteo.deploy.events;

import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

public class EventReply {

	private Message<JsonObject> message;

	public EventReply(Message<JsonObject> message) {
		super();
		this.message = message;
	}

	public void send(JsonObject replyData) {
		message.reply(replyData);
	}
}
