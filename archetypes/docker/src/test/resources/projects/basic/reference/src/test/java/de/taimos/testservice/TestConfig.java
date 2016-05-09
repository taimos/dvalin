package de.taimos.testservice;

import de.taimos.daemon.spring.SpringDaemonTestRunner.RunnerConfig;
import de.taimos.dvalin.daemon.DvalinTestRunnerConfig;

public class TestConfig extends DvalinTestRunnerConfig {

	public TestConfig() {
		Integer port = RunnerConfig.randomPort();

//      MongoDB Config
//		this.addProperty("mongodb.name", "testservice");
//		this.addProperty("mongodb.demodata", "false");
//		this.addProperty("mongodb.type", "fake");
//		this.addProperty("mongobee.enabled", "true");
//		this.addProperty("mongobee.basePackage", "de.taimos.testservice.changelog");

//      JAX-RS Config
//      this.addProperty("svc.port", Integer.toString(port));
//		this.addProperty("server.url", "http://127.0.0.1:" + port);
//		this.addProperty("server.weburl", "http://127.0.0.1:3000");

//      AWS Config
//      this.addProperty("aws.accessKeyId", "");
//		this.addProperty("aws.secretKey", "");
	}

	@Override
	public String getServicePackage() {
		return "de.taimos.testservice";
	}

}
