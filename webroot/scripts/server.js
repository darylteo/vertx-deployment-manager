(function(){
	
	angular.module("App.Server",[])
	.value("host", window.location.protocol + '//' + window.location.hostname + ':' + window.location.port + '/eventbus')
	
	.factory("server", function(host){
		
		var eb = new vertx.EventBus();
		eb.onopen = function(){
			if(_server.onopen){
				_server.onopen();
			}
		};
		
		eb.onclose = function(){
			if(_server.onclose){
				_server.onclose();
			}		
		}
		
		var _server = {
			connect : function(){
				eb.open(host);
			},
			close : function(){
				eb.close();
			},
			
			listModules : function(){
				send(
					"list-modules",
					{},
					function(r){
						console.log(JSON.stringify(r));
					}
				);
			},
			listDeployments : function(){
				send(
					"list-deployments",
					{},
					function(r){
						console.log(JSON.stringify(r));
					}
				);
			},
			deployModule : function(name,instances){
				for(i = 0; i < instances; i++){
					console.log("Deploy Module");
					send(
						"deploy-module",
						{
							"module-name" : name
						},
						function(r){
							console.log(JSON.stringify(r));
						}
					)
				}
			},
			undeployModule : function(name,instances){
				for(i = 0; i < instances; i++){
					console.log("Undeploy Module");
					send(
						"undeploy-module",
						{
							"module-name" : name
						},
						function(r){
							console.log(JSON.stringify(r));
						}
					)
				}
			}
		};
			
		function send (address,message,replyHandler,failHandler){
			if(!eb.readyState == vertx.EventBus.OPEN){
				console.log("Bus Closed, Cannot Send Message");
				if(failHandler){
					failHandler("Bus Closed, Cannot Send Message");
				}
			}

			address = "deployment-manager.server." + address;
			console.log("Sending Message: " + address);

			try{
				eb.send(address,message,function(r){
					console.log("Complete Sending Message");
					if(replyHandler){
						replyHandler(r);
					}
				});
			}catch(err){
				console.log("Failed to Send Message: " + err);
				if (failHandler){
					failHandler(err);
				}
			}
		}
		
		window.server = _server;
		return _server;
	})
})();