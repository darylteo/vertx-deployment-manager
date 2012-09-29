import java.util.Map;

import org.vertx.java.deploy.Verticle;

import com.darylteo.deploy.core.Core;
import com.darylteo.deploy.web.WebAdmin;

public class DeploymentManager extends Verticle {
	
	@Override
	public void start() throws Exception {
		/* Start Web Admin Web Server */
		Core core = new Core(this);
		WebAdmin admin = new WebAdmin(this);
	}


}
