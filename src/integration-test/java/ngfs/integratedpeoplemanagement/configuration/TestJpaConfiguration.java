package ngfs.integratedpeoplemanagement.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

import static ngfs.integratedpeoplemanagement.configuration.TestProperties.getProperty;

@Configuration
@EnableJpaRepositories(basePackages = "ngfs.integratedpeoplemanagement.repository")
@EnableTransactionManagement
public class TestJpaConfiguration {
    @Bean
    @Profile("integration-test")
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(getProperty(TestProperties.PropertyName.JDBC_DRIVER_CLASS_NAME));
        dataSource.setUrl(getProperty(TestProperties.PropertyName.JDBC_URL));
        dataSource.setUsername(getProperty(TestProperties.PropertyName.JDBC_USERNAME));
        dataSource.setPassword(getProperty(TestProperties.PropertyName.JDBC_PASSWORD));

        return dataSource;
    }
}