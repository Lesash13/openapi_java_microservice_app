package ngfs.integratedpeoplemanagement.configuration;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class DmnEngineConfiguration {
    private final static Logger logger = LoggerFactory.getLogger(DmnEngineConfiguration.class);

    @Bean
    public DmnEngine getDmnEngine() {
        return org.camunda.bpm.dmn.engine.DmnEngineConfiguration.createDefaultDmnEngineConfiguration().buildEngine();
    }

    @Bean
    public DmnDecision getDmnDecision() {
        DmnDecision dmnDecision = null;
        try (InputStream inputStream = DmnEngineConfiguration.class.getResourceAsStream("/status-decision.xml")) {
            dmnDecision = getDmnEngine().parseDecision("decision", inputStream);
        } catch (IOException e) {
            logger.error("Error during receiving status from decisionEngine. Error message: {}", e.getMessage());
        }
        return dmnDecision;
    }
}
