(function(){
	var _app = angular.module(
		"App",
		[
			"App.Controllers",
			"App.Server",
			"App.Model"
		]);
	
	window.App = _app;
})();

