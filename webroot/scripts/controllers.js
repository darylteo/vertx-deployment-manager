(function(){
	angular.module(
		"App.Controllers",
		[
			"App.Model"
		]
	)

	.controller("AppController", function($scope,model){
		$scope.refresh = function(){
			model.refreshAllModules();
		}

		/* Whenever Server is connected, refresh all the data */
		$scope.$on("server-open",function(e,data){
			model.loadData();
		});
	})

	.controller("ModulesController", function($scope,model){
		$scope.modules = [];

		/* Push the new module when one is loaded */
		$scope.$on("new-module",function(e,data){
			$scope.modules.push(data);
			$scope.$digest();
		});

		/* Load all the relevant data when loaded */
		$scope.$on("data-loaded",function(e,data){
			console.log("Data Loaded: ")
			console.log(data.modules);
			$scope.modules = data.modules;
			$scope.$digest();
		});
	})

	.controller("DeploymentsController", function($scope,model){
		$scope.deployments = [];

		/* Outlet Actions */
		$scope.deploy = function(name,instances){
			if(instances > 0){
				server.deployModule(name,instances);
			}else{
				server.undeployModule(name,0-instances);
			}
		}

		/* Load all the relevant data when loaded */
		$scope.$on("data-loaded",function(e,data){
			$scope.deployments = data.deployments;
			$scope.$digest();
		});
	})

	.run(function(model){
		model.connect();
	});


})();