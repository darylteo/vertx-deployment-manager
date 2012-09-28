package com.darylteo.deploy.modules;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.*;

public class Modules {
	/* Instance Variables */
	private Path modulesDir;
	private Map<String,Module> modules = new HashMap<>();

	/* Constructors */
	public Modules(Path modulesDir) {
		this.modulesDir = modulesDir;

		loadModules();
	}

	public Modules(String modulesDir) {
		this(Paths.get(modulesDir));
	}
	
	/* Accessors */
	public Module[] getModules() {
		return this.modules.values().toArray(new Module[this.modules.size()]);
	}
	
	public Module getModuleWithName(String name) throws Exception{
		if(!this.modules.containsKey(name)){
			// TODO: Custom Exception
			throw new Exception("Module Not Found");
		}
		
		return this.modules.get(name);
	}
	
	/* Private Methods */
	private void loadModules() {
		System.out.printf("Looking for Modules In %s\n", this.modulesDir);

		File f = this.modulesDir.toFile();
		if (!f.exists()) {
			// Modules Directory does not exist
			System.out.printf("Modules Directory %s Does Not Exist!\n",
					this.modulesDir);
		}

		String[] files = f.list();

		for (String moduleName : files) {
			try {
				validateModule(moduleName);

				Module m = new Module(moduleName, "main");
				this.modules.put(moduleName,m);

				System.out.printf("Module Loaded: %s\n",moduleName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void validateModule(String moduleName) throws Exception {
		Path modulePath = this.modulesDir.resolve(moduleName);
		System.out.printf("Checking Module in Directory: %s\n", modulePath);

		File dir = modulePath.toFile();
		if (!dir.isDirectory()) {
			// File is not a Directory, skip this one... We're only looking
			throw new Exception("Non-Directory detected in Module Directory.");
		}

		File json = modulePath.resolve("mod.json").toFile();

		if (!json.exists()) {
			throw new Exception(
					"mod.json Configuration Found not found for Module.");
		}
	}
}
