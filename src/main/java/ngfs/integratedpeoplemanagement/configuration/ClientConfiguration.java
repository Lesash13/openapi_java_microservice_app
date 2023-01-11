package ngfs.integratedpeoplemanagement.configuration;

import ngfs.integratedpeoplemanagement.houseservice.api.HouseApi;
import ngfs.integratedpeoplemanagement.service.IntegratedPeopleMngmtServiceImpl;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.client.spring.EnableJaxRsProxyClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableJaxRsProxyClient
@Configuration
public class ClientConfiguration {
    @Bean
    public HouseApi client(HouseApi client, IntegratedPeopleMngmtServiceImpl service) {
        service.setClient(client);
        WebClient.getConfig(client).setSynchronousTimeout(1);
        return client;
    }
}
