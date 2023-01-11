package ngfs.integratedpeoplemanagement.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestProperties {

    private static final TestProperties INSTANCE = new TestProperties();

    Properties prop;

    private TestProperties() {
        prop = new Properties();
        InputStream propStream = this.getClass().getClassLoader().getResourceAsStream("test.properties");
        try {
            if (propStream != null) {
                prop.load(propStream);
            } else {
                throw new IllegalArgumentException("File not found!");
            }
        } catch (IOException ioexc) {
            throw new IllegalArgumentException("Cannot read file!");
        }
    }

    public static String getProperty(String propName) {
        return INSTANCE.prop.getProperty(propName);
    }

    public static Boolean getBooleanProperty(String propName) {
        return Boolean.parseBoolean(INSTANCE.prop.getProperty(propName));
    }

    public static Integer getIntProperty(String propName) {
        return Integer.parseInt(INSTANCE.prop.getProperty(propName));
    }

    public static class PropertyName {

        public final static String SERVICE_PORT = "service.port";

        public final static String SERVICE_URL = "service.url";

        public final static String SERVICE_BASE_PATH = "service.basepath";

        public final static String MOCK_SERVER_PORT = "mock.server.port";

        public final static String MOCK_SERVER_BASEPATH = "mock.server.basepath";

        public final static String MOCK_SERVER_SECURE = "mock.server.secure";

        public final static String MOCK_SERVER_URL = "mock.server.url";

        public final static String JDBC_DRIVER_CLASS_NAME = "jdbc.driverClassName";

        public final static String JDBC_URL = "jdbc.url";

        public final static String JDBC_USERNAME = "jdbc.username";

        public final static String JDBC_PASSWORD = "jdbc.password";
    }
}
