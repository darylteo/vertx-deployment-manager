package com.darylteo.deploy.modules;

interface ModuleLoaderDelegate {
	void moduleInstalled(String moduleName);
	void moduleModified(String moduleName);
	void moduleUninstalled(String moduleName);
}
