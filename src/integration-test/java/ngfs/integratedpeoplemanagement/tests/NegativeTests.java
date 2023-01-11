package ngfs.integratedpeoplemanagement.tests;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import io.restassured.RestAssured;
import ngfs.integratedpeoplemanagement.IntegratedPeopleManagementApplication;
import ngfs.integratedpeoplemanagement.configuration.TestHelper;
import ngfs.integratedpeoplemanagement.configuration.TestJpaConfiguration;
import ngfs.integratedpeoplemanagement.entity.People;
import ngfs.integratedpeoplemanagement.peopleservice.model.PeopleDto;
import ngfs.integratedpeoplemanagement.repository.PeopleRepository;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.logging.Logger;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.emptyString;

@SpringBootTest(classes = {IntegratedPeopleManagementApplication.class, TestJpaConfiguration.class})
@ActiveProfiles("integration-test")
@Transactional
public class NegativeTests extends TestHelper {

    private static final Logger LOGGER = Logger.getLogger(NegativeTests.class.getName());

    @Autowired
    private PeopleRepository repository;

    @Autowired
    private EntityManager entityManager;

    @BeforeTransaction
    public void cleanDb() {
        LOGGER.warning("Cleaning db..");
        repository.deleteAll();
    }

    @AfterEach
    public void flush() {
        entityManager.flush();
    }

    @Test
    protected void GetPersonByID() {

        LOGGER.info("Set mock delay more than 1 min");
        vm.stubFor(get(urlEqualTo(houseServicePathForGet)).willReturn(
                aResponse().withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(houseJson)
                        .withStatus(HttpStatus.SC_OK)
                        .withFixedDelay(62000)));

        PeopleDto postPerson = getPerson("FindById", "Person");
        LOGGER.info("Send POST to create a person");

        RestAssured.given()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .body(postPerson)
                .when()
                .post(pathForCreation)
                .then()
                .statusCode(HttpStatus.SC_OK);

        Long personId = repository.findPeopleByFirstnameAndLastnameAndBirthdayAndStatus(postPerson.getFirstname(),
                postPerson.getLastname(), postPerson.getBirthday(),
                People.StatusEnum.fromValue(postPerson.getStatus().value())).get(0).getId();

        LOGGER.info("Send GET after timeout and assert negative response");

        RestAssured.when()
                .get(pathForGetAndDelete, personId)
                .then()
                .statusCode(HttpStatus.SC_GATEWAY_TIMEOUT)
                .body(emptyString());

        vm.verify(1, new RequestPatternBuilder(RequestMethod.GET, urlEqualTo(houseServicePathForGet)));
    }

    @Test
    public void CreateAndGetPeopleNegative() {

        PeopleDto newPerson = getPerson("namenamenamenamenamen", "Person");

        LOGGER.info("Send POST to create invalid person");

        RestAssured.given()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .body(newPerson)
                .when()
                .post(pathForCreation)
                .then()
                .statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED)
                .body(emptyString());

        Assertions.assertEquals(repository.findPeopleByFirstnameAndLastnameAndBirthdayAndStatus(newPerson.getFirstname(),
                newPerson.getLastname(), newPerson.getBirthday(),
                People.StatusEnum.fromValue(newPerson.getStatus().value())).size(), 0);
    }

    @Test
    public void DeletePeopleNegative() {

        LOGGER.info("Send DELETE for non-existent person");

        RestAssured.given()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .when()
                .delete(pathForGetAndDelete, 10000000)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(emptyString());

    }

    @Test
    public void GetNegative() {

        LOGGER.info("Send GET with invalid id and assert negative response");

        RestAssured.given()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .when()
                .get(pathForGetAndDelete, 10000000)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(emptyString());

        RestAssured.given()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .when()
                .get(pathForGetAndDelete, 0)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(emptyString());
    }

    @Test
    public void GetChildrenAddressesNegative() {

        LOGGER.info("Send GET with invalid id and assert negative response");

        RestAssured.given()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .when()
                .get(pathForGetChildren, 10000000)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(emptyString());
    }

    @Test
    public void FindPersonByStatusNegative() {

        LOGGER.info("Send GET with invalid status and assert negative response");

        RestAssured.given()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .queryParam("status", "invalidStatus")
                .when()
                .get(pathForFindByStatus)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(emptyString());

    }

    @Test
    public void UpdatePeoplePutInvalidDate() {

        PeopleDto newPerson = getPerson("UpdatedN", "Person");
        LOGGER.info("Send POST to create a person");

        RestAssured.given()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .body(newPerson)
                .when()
                .post(pathForCreation)
                .then()
                .statusCode(HttpStatus.SC_OK);

        newPerson.setId(12345L);
        RestAssured.given()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .body(newPerson)
                .when()
                .put(pathForCreation)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(emptyString());

        Long personId = repository.findPeopleByFirstnameAndLastnameAndBirthdayAndStatus(newPerson.getFirstname(),
                newPerson.getLastname(), newPerson.getBirthday(),
                People.StatusEnum.fromValue(newPerson.getStatus().value())).get(0).getId();
        newPerson.setBirthday(LocalDate.of(1979, 12, 31));
        newPerson.setId(personId);

        LOGGER.info("Send PUT to update person");

        RestAssured.given()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .body(newPerson)
                .when()
                .put(pathForCreation)
                .then()
                .statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED)
                .body(emptyString());

    }

    @Test
    public void UpdatePeopleProfilePostInvalidID() {

        LOGGER.info("Send POST with invalid id");

        RestAssured.given()
                .contentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType())
                .formParam("firstname", "UpdatedNehName")
                .formParam("lastname", "UpdatedNegSurname")
                .formParam("status", PeopleDto.StatusEnum.ALIVE)
                .when()
                .post(pathForGetAndDelete, 100000000)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(emptyString());

    }
}
