var eb = (function(){
	var ebHost = window.location.protocol + '//' + window.location.hostname + ':' + window.location.port + '/eventbus';
	var eb = new vertx.EventBus(ebHost);
	console.log('Opening ' + ebHost);
	
	eb.onopen = function(){
		console.log("Event Bus Opened");
		
		eb.registerHandler(
			"deployment-manager.client.ping",
			function(message,replier){
				console.log(message);
				replier({
					data : "pong"
				});
			}
		);
	}
	eb.onclose = function(){
		console.log("Event Bus Closed");
	}
	
	return eb;
})();