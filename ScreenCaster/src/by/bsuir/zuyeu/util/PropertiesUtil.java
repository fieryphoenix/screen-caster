package by.bsuir.zuyeu.util;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private static final String APP_CONFIG = "../../../../app-config.properties";

    private PropertiesUtil() {

    }

    public static String getProperty(final String key) {
	logger.info("getProperty() - start;");
	String result = null;
	final Properties properties = new Properties();
	try {
	    properties.load(PropertiesUtil.class.getResourceAsStream(APP_CONFIG));
	    result = properties.getProperty(key);
	} catch (final IOException e) {
	    logger.error("getProperty", e);
	}
	logger.info("getProperty() - end: result = {}", result);
	return result;
    }

    public static void main(String[] args) {
	final String host = PropertiesUtil.getProperty("monitor_server_host");
	System.out.println(host);
    }
}
