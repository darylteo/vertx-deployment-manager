(function(){
	angular.module("App.Controllers",[])
	
		.controller("App2Controller", function($scope,model){
			$scope.refresh = function(){
				model.refresh();
			}
		})
	
		.controller("ModulesController", function($scope){
			$scope.modules = [
				{
					"name" : "engine"
				}
			];
		})
	
		.controller("DeploymentsController", function($scope,server){
			$scope.deployments = [
				{
					"name" : "engine",
					"deployments" : [
						"abc123",
						"abc124",
						"abc125"
					]
				}
			];
			
			$scope.deploy = function(name,instances){
				if(instances > 0){
					server.deployModule(name,instances);
				}else{
					server.undeployModule(name,0-instances);
				}
			}
		})
		
		.$inject = ["$scope","model","server"];
})();