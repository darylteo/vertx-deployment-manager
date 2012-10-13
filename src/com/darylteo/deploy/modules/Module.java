package com.darylteo.deploy.modules;

public class Module {
	private String name;
	private String main;

	/**
	 * @param name
	 *            - Name of the Module (including Version)
	 * @param main
	 *            - String property of the Main verticle class/script
	 */
	public Module(String name, String main) {
		this.name = name;
		this.main = main;
	}

	/* Accessors */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMain() {
		return main;
	}

	public void setMain(String main) {
		this.main = main;
	}
}
