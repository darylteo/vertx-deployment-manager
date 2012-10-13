package com.darylteo.deploy.modules;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class ModuleLoader {
	private final ModuleLoader that = this;
	private final Path folder;

	private final WatchService watcher;
	private final Map<WatchKey, String> keyModuleMap;

	private final ModuleLoaderDelegate delegate;

	public ModuleLoader(Path folder, ModuleLoaderDelegate delegate)
			throws Exception {
		this.folder = folder;

		System.out.printf("Looking for Modules In %s\n", this.folder);

		File f = this.folder.toFile();
		if (!f.exists()) {
			// Modules Directory does not exist
			throw new Exception("Modules Directory %s Does Not Exist!");
		}

		this.delegate = delegate;

		this.watcher = FileSystems.getDefault().newWatchService();
		this.keyModuleMap = new HashMap<>();

		loadExistingModules();
		setupWatchService();
	}

	private void loadExistingModules() {
		try {
			Files.walkFileTree(this.folder, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) throws IOException {

					/*
					 * Do a non-recursive watch on the root folder to detect
					 * module installation/uninstallation
					 */
					if (dir.equals(that.folder)) {
						that.registerRoot();
						return FileVisitResult.CONTINUE;
					}

					/*
					 * Recursively watch module directory to detect module
					 * modification
					 */
					String moduleName = that.folder.relativize(dir).getName(0)
							.toString();
					that.registerAll(dir, moduleName);
					that.delegate.moduleInstalled(moduleName);

					return FileVisitResult.SKIP_SUBTREE;
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* WatchService methods */

	/**
	 * Sets up the Watch Service
	 */
	private void setupWatchService() throws IOException {
		new Thread(new Runnable() {
			@Override
			public void run() {
				that.watch();
			}
		}).start();
	}

	/**
	 * Register the given directory with the WatchService
	 */
	private void registerRoot() throws IOException {
		WatchKey key = this.folder
				.register(watcher, ENTRY_CREATE, ENTRY_DELETE);
		this.keyModuleMap.put(key, "");

	}

	private void register(Path dir, String moduleName) throws IOException {
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY,
				ENTRY_DELETE);
		this.keyModuleMap.put(key, moduleName);

		System.out.printf("%s, %s -> %s\n", moduleName, key, dir);
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService, linked to a ModuleName.
	 */
	private void registerAll(final Path start, final String moduleName)
			throws IOException {
		// register directory and sub-directories
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) throws IOException {
				/* Register the Directory */
				register(dir, moduleName);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private void watch() {
		boolean exit = false;
		while (!exit) {
			WatchKey key;

			try {
				key = this.watcher.take();
			} catch (InterruptedException e) {
				return;
			}

			String moduleName = this.keyModuleMap.get(key);

			try {
				if (moduleName.equals("")) {
					handleRootEvent(key);
				} else {
					handleModuleEvent(key, moduleName);
				}
			} catch (IOException e) {
				// TODO: Error Handling
				e.printStackTrace();
			}

		}
	}

	private void handleRootEvent(WatchKey key) throws IOException {

		for (WatchEvent<?> event : key.pollEvents()) {

			if (event.kind() == OVERFLOW) {
				continue;
			}

			String moduleName = event.context().toString();
			Path targetPath = this.folder.resolve(moduleName);

			/* Add new module to watchservice, notify handler of installation */
			if (event.kind() == ENTRY_CREATE) {
				/* Ignore non-module files. Modules must be directories */
				if (!Files.isDirectory(targetPath, NOFOLLOW_LINKS)) {
					continue;
				}

				this.registerAll(targetPath, moduleName);
				this.delegate.moduleInstalled(moduleName);

			} else /* notify handler of deletion */
			{
				/*
				 * if not ENTRY_CREATE then must be ENTRY_DELETE as we're not
				 * tracking ENTRY_MODIFY
				 */
				/* Notify */
				this.delegate.moduleUninstalled(moduleName);
			}
		}

		/* Returns False if key is invalidated */
		if (!key.reset()) {
			/*
			 * TODO: Exception Handling... this key is for root, should never be
			 * invalidated
			 */
			System.out.println("Bad Error: Root WatchKey Invalidated");
			this.keyModuleMap.remove(key);
		}
	}

	private void handleModuleEvent(WatchKey key, String moduleName)
			throws IOException {

		for (WatchEvent<?> event : key.pollEvents()) {

			if (event.kind() == OVERFLOW) {
				continue;
			}

			Path targetPath = this.folder.resolve(moduleName);

			/* Register any new directories for watching */
			if (event.kind() == ENTRY_CREATE) {
				/* Ignore non-directories */
				if (!Files.isDirectory(targetPath, NOFOLLOW_LINKS)) {
					continue;
				}

				this.registerAll(targetPath, moduleName);
			}

			/* Notify */
			this.delegate.moduleModified(moduleName);
		}

		/* Returns False if key is invalidated */
		if (!key.reset()) {
			System.out.println("Key Invalidated: " + moduleName);
			this.keyModuleMap.remove(key);
		}
	}
}
