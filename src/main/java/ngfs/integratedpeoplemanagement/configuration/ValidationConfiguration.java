package ngfs.integratedpeoplemanagement.configuration;

import org.apache.cxf.validation.BeanValidationFeature;
import org.apache.cxf.validation.BeanValidationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfiguration {
    @Bean
    public HibernateValidationProviderResolver validationProviderResolver() {
        return new HibernateValidationProviderResolver();
    }

    @Bean
    public BeanValidationProvider beanValidationProvider(HibernateValidationProviderResolver resolver) {
        return new BeanValidationProvider(resolver);
    }

    @Bean
    public BeanValidationFeature beanValidationFeature(BeanValidationProvider provider) {
        BeanValidationFeature feature = new BeanValidationFeature();
        feature.setProvider(provider);
        return feature;
    }
}
