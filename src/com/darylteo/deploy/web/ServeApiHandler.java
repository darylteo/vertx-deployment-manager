package com.darylteo.deploy.web;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

public class ServeApiHandler implements Handler<HttpServerRequest> {
	
	private static final Path MODULES_PATH = Paths.get("modules");
	
	@Override
	public void handle(HttpServerRequest request) {
		Path path = Paths.get(request.uri);

		path = Paths.get("/api").relativize(path);
		
		System.out.println("API Call: " + path);
		
		if(path.equals(MODULES_PATH)){
			this.listModules(request);
			return;
		}
		
		request.response.statusCode = 404;
		request.response.end("");
	}
	
	private void listModules(HttpServerRequest request){
		request.response.end("Modules");
	}
}
