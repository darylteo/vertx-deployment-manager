import java.io.File;
import java.nio.file.*;

import org.vertx.java.deploy.Verticle;

import com.darylteo.deploy.modules.Modules;

public class DeploymentManager extends Verticle {
	
	private Modules modules;
	
	@Override
	public void start() throws Exception {
		Path workingDir = Paths.get(System.getProperty("user.dir"));
		Path modsDir = workingDir.resolve("mods");

		System.out.printf("Running Deployment Manager In %s\n", workingDir);

		this.modules = new Modules(modsDir);
		
		this.container.deployModule("engine");
	}


}
