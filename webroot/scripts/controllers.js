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

	.controller("DeploymentsController", function($scope,model){
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
		}
		function unloadModule(m){
			delete $scope.deploymentMap[m.module_name];
		}

		function loadDeployments(d){
			for (i in d){
				addDeployment(d[i]);
			}
		}
		function addDeployment(d){
			var entry = $scope.deploymentMap[d.module_name];
			console.log("Loaded Entry");
			console.log(entry);
			entry.deployments.push({
				"deployment_id" : d.deployment_id
			});
		}
		function removeDeployment(d){
			var entry = $scope.deploymentMap[d.module_name];
			for(i in entry.deployments){
				var d = entry.deployments[i];
				if(d.deployment_id === d.deployment_id){
					entry.deployments.splice(i,1);
					return;
				}
			}

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

		$scope.$on("module-installed",function(e,module){
			loadModule(module);

			$scope.$digest();
		});
		$scope.$on("module-uninstalled",function(e,module){
			unloadModule(module);

			$scope.$digest();
		});

		$scope.$on("module-deployed",function(e,deployment){
			addDeployment(deployment);

			$scope.$digest();
		});
		$scope.$on("module-undeployed",function(e,deployment){
			removeDeployment(deployment);

			$scope.$digest();
		});

	})

	.run(function(model){
		model.connect();
	});


})();