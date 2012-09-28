package com.darylteo.deploy.web;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.deploy.Verticle;

public class WebAdmin {

	private static ServeFileHandler fileHandler = new ServeFileHandler();
	private static ServeApiHandler apiHandler = new ServeApiHandler();

	public WebAdmin(final Verticle verticle) {
		final Vertx vertx = verticle.getVertx();

		HttpServer server = vertx.createHttpServer();
		server.requestHandler(new Handler<HttpServerRequest>() {

			@Override
			public void handle(HttpServerRequest request) {
				Path path = Paths.get(request.uri);

				if (path.startsWith("/api")) {
					apiHandler.handle(request);
					return;
				}

				fileHandler.handle(request);
			}
		});

		SockJSServer sockjsServer = vertx.createSockJSServer(server);

		JsonObject config = new JsonObject().putString("prefix", "/eventbus");
		JsonArray inbound = new JsonArray();
		inbound.addObject(new JsonObject().putString("address_re",
				"deployment-manager\\.server\\..+"));
		JsonArray outbound = new JsonArray();
		outbound.addObject(new JsonObject().putString("address_re",
				"deployment-manager\\.client\\..+"));

		sockjsServer.bridge(config, inbound, outbound);

		server.listen(8080, "localhost");
	}
}
