(function(){
	angular.module(
		"App.Model",
		[
			"App.Server"
		]
	)
	.factory("model",function(server){
		function refresh(){
			server.listModules();
			server.listDeployments();
		}
		
		return {
			refresh: refresh
		}
	})
	
	.run(function(model,server){
		server.onopen = function(){
			model.refresh();
		};
		
		server.connect();
	});
	
	
})();