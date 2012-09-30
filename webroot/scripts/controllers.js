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
			$scope.modules = data.modules;
			$scope.$digest();
		});
	})

	.controller("DeploymentsController", function($scope,model){
		$scope.deploymentList = [];
		$scope.deploymentMap = {};

		function loadModules(m){
			for(i in m){
				loadModule(m[i]);
			}
		}
		function loadModule(m){
			entry = {
				"module_name" : m.module_name,
				"deployments" : []
			};

			$scope.deploymentMap[m.module_name] = entry;
			$scope.deploymentList.push(entry);
		}

		function loadDeployments(d){
			for (i in d){
				loadDeployment(d[i]);
			}
		}
		function loadDeployment(d){
			var entry = $scope.deploymentMap[d.module_name];
			console.log("Loaded Entry");
			console.log(entry);
			entry.deployments.push({
				"deployment_id" : d.deployment_id
			});
		}


		/* Outlet Actions */
		$scope.deploy = function(name,instances){
			if(instances > 0){
				model.deployModule(name,instances);
			}else{
				model.undeployModule(name,0-instances);
			}
		}

		/* Load all the relevant data when loaded */
		$scope.$on("data-loaded",function(e,data){
			$scope.deploymentList = [];
			$scope.deploymentMap = {};

			loadModules(data.modules);
			loadDeployments(data.deployments);

			$scope.$digest();
		});

		$scope.$on("new-module",function(e,module){
			loadModule(module);

			$scope.$digest();
		});
		$scope.$on("new-deployment",function(e,deployment){
			loadDeployment(deployment);

			$scope.$digest();
		});

	})

	.run(function(model){
		model.connect();
	});


})();