package ngfs.integratedpeoplemanagement.extension;

import com.github.tomakehurst.wiremock.client.WireMock;
import ngfs.integratedpeoplemanagement.configuration.TestProperties;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ngfs.integratedpeoplemanagement.configuration.TestProperties.PropertyName.*;

public class MockServerExtension implements BeforeAllCallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(MockServerExtension.class);

    @Override
    public void beforeAll(ExtensionContext context) {
        ExtensionContext.Store store = context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL);
        RemoteMockServer server = (RemoteMockServer) store.getOrComputeIfAbsent("mock_server",
                key -> new RemoteMockServer());
        server.start();
    }

    private static class RemoteMockServer extends WireMock implements ExtensionContext.Store.CloseableResource {
        private boolean isRunning = false;

        protected RemoteMockServer() {
            super(TestProperties.getBooleanProperty(MOCK_SERVER_SECURE) ? "https" : "http",
                    TestProperties.getProperty(MOCK_SERVER_URL), TestProperties.getIntProperty(MOCK_SERVER_PORT));

            WireMock.configureFor(this);
        }

        public void start() {
            if (!isRunning) {
                //some start actions
                LOGGER.info("MockServer starts!");

                this.resetRequests();
                this.resetScenarios();

                isRunning = true;
            }
        }

        @Override
        public void close() {
            if (isRunning) {
                //After all tests run hook.
                //Any additional desired action goes here
                this.resetRequests();
                this.resetScenarios();
                this.removeMappings();
                isRunning = false;
            }
        }
    }
}