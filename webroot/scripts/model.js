(function(){
	angular.module(
		"App.Model",
		[]
	)

	/* Values */
	.value("host", window.location.protocol + '//' + window.location.hostname + ':' + window.location.port + '/eventbus')

	.factory("model",function($rootScope,host){
		/* Declaring Event Bus */
		var eb = new vertx.EventBus();
		eb.onopen = function(){
			console.log("Event Bus Open");

			/* Handlers */
			var events = [
				"new-module",
				"new-deployment"
			];

			for(i in events){
				registerHandler(events[i], function(data){
					(function(){
						var name = events[i];
						console.log("Received Message: " + name);
						console.log(data);

						broadcast(name, data);
					})();
				});
			}

			broadcast("server-open");
		}
		eb.onclose = function(){
			console.log("Event Bus Closed");
			broadcast("server-closed");
		}

		var _model = {
			connect : function(){
				eb.open(host);

				return _model;
			},
			close : function(){
				eb.close();

				return _model;
			},

			loadData : function(){
				send(
					"load-all",
					{},
					function(data){
						broadcast("data-loaded", data);
					}
				);

				return _model;
			},
			deployModule : function(name,instances){
				for(i = 0; i < instances; i++){
					send(
						"deploy-module",
						{
							"module_name" : name
						},
						function(r){
							console.log("Deployment: " + JSON.stringify(r));
						}
					)
				}

				return _model;
			},
			undeployModule : function(name,instances){
				for(i = 0; i < instances; i++){
					console.log("Undeploy Module");
					send(
						"undeploy-module",
						{
							"module_name" : name
						},
						function(r){
							console.log(JSON.stringify(r));
						}
					)
				}

				return _model;
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
					console.log("Complete Sending Message: " + address);
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

		function broadcast(event, data){
			$rootScope.$broadcast(event,data);
		}

		function registerHandler(address, handler){
			address = "deployment-manager.client." + address;
			eb.registerHandler(address,handler);
		}

		return _model;
	})
})();