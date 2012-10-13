package com.darylteo.deploy.web;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

public class ServeFileHandler implements Handler<HttpServerRequest> {
	@Override
	public void handle(HttpServerRequest request) {
		Path path = Paths.get(request.uri);

		if (path.equals(Paths.get("/"))) {
			path = path.resolve("index.html");
		}
		path = Paths.get("webroot", path.toString());

		System.out.println("File Call: " + path);

		if (!Files.exists(path)) {
			request.response.statusCode = 404;
			request.response.end();
			return;
		}

		request.response.sendFile(path.toString());
	}
}
