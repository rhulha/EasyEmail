package net.raysforge.email;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

	public final String server;
	public final String user;
	public final String pw;
	public final String root_cert_file;

	public Configuration(String configFile) throws IOException {
		Properties props = new Properties();
		FileInputStream is = new FileInputStream(configFile);
		props.load(is);
		is.close();
		this.server = props.getProperty("server");
		this.user = props.getProperty("user");
		this.pw = props.getProperty("pw");
		this.root_cert_file = props.getProperty("root_cert_file");
	}

}
