package ngfs.integratedpeoplemanagement.configuration;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.SSLConfig;
import ngfs.integratedpeoplemanagement.peopleservice.model.AddressDto;
import ngfs.integratedpeoplemanagement.peopleservice.model.PeopleDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Random;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class TestHelper {

    protected final String pathForCreation = "/people";
    protected final String pathForGetAndDelete = "/people/{id}";
    protected final String houseJson = "{" +
            "\"id\": 0," +
            "\"city\": \"St. Petersburh\"," +
            "\"street\": \"12-13 Line V.O.\"," +
            "\"number\": 14," +
            "\"lastUsed\": \"2020-03-16\"" +
            "}";
    protected final String houseServicePathForGet = "/house/0";
    protected final String pathForGetChildren = "/people/{id}/getChildrenAddresses";
    protected final String pathForFindByStatus = "/people/findByStatus";
    protected final String houseServicePathForPut = "/house";
    public WireMockServer vm = new WireMockServer(options().port(4443));

    public static PeopleDto getPerson(String firstname, String lastname) {
        PeopleDto person = new PeopleDto();
        person.setFirstname(firstname + new Random().nextInt(1000));
        person.setLastname(lastname);
        person.setBirthday(LocalDate.now());
        person.setStatus(PeopleDto.StatusEnum.ALIVE);

        AddressDto address = new AddressDto();
        address.setRegistrationId(0L);
        address.setHomeIds(Collections.singletonList(0L));
        person.setAddressData(address);
        person.setChildren(Collections.emptyList());

        return person;
    }

    @BeforeAll
    static void setRestUrl() {
        RestAssured.port = Integer.parseInt(TestProperties.getProperty(TestProperties.PropertyName.SERVICE_PORT));
        RestAssured.baseURI = TestProperties.getProperty(TestProperties.PropertyName.SERVICE_URL);
        RestAssured.basePath = TestProperties.getProperty(TestProperties.PropertyName.SERVICE_BASE_PATH);
        RestAssured.config()
                .sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation())
                .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                        .jackson2ObjectMapperFactory(
                                (Type cls, String charset) -> new JacksonConfiguration().objectMapper()));

    }

    @BeforeEach
    public void beforeEach() {
        vm.start();
        vm.resetRequests();
        vm.resetMappings();
    }

    @AfterEach
    public void afterEach() {
        vm.stop();
    }
}
