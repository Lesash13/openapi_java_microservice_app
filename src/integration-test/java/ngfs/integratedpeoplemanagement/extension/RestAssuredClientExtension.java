package ngfs.integratedpeoplemanagement.extension;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.SSLConfig;
import ngfs.integratedpeoplemanagement.configuration.JacksonConfiguration;
import ngfs.integratedpeoplemanagement.configuration.TestProperties;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Type;

public class RestAssuredClientExtension implements BeforeAllCallback {

    private static boolean started = false;

    @Override
    public void beforeAll(ExtensionContext context) {
        if (!started) {
            started = true;

            RestAssured.port = Integer.parseInt(TestProperties.getProperty(TestProperties.PropertyName.SERVICE_PORT));
            RestAssured.baseURI = TestProperties.getProperty(TestProperties.PropertyName.SERVICE_URL);
            RestAssured.basePath = TestProperties.getProperty(TestProperties.PropertyName.SERVICE_BASE_PATH);

            RestAssured.config()
                    .sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation())
                    .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                            .jackson2ObjectMapperFactory(
                                    (Type cls, String charset) -> new JacksonConfiguration().objectMapper()));
        }
    }
}
