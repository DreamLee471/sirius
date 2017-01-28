package org.sirius.core.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.sirius.domain.SiriusConf;

public class ConfigParser {
	
	
	public static final SiriusConf build(String path) throws IOException{
		return build(new FileInputStream(path));
	}

	private static SiriusConf build(InputStream fileInputStream) throws IOException {
		Properties p = new Properties();
		p.load(fileInputStream);
		
		SiriusConf conf = new SiriusConf();
		conf.setSeedNode(p.getProperty("seedNode"));
		
		return null;
	}
	

}
