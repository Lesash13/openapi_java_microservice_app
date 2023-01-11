package ngfs.integratedpeoplemanagement.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import ngfs.integratedpeoplemanagement.IntegratedPeopleManagementApplication;
import ngfs.integratedpeoplemanagement.configuration.TestHelper;
import ngfs.integratedpeoplemanagement.configuration.TestJpaConfiguration;
import ngfs.integratedpeoplemanagement.entity.People;
import ngfs.integratedpeoplemanagement.peopleservice.model.AddressDto;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.emptyString;

@SpringBootTest(classes = {IntegratedPeopleManagementApplication.class, TestJpaConfiguration.class})
@ActiveProfiles("integration-test")
@Transactional
public class PositiveTests extends TestHelper {

    private static final Logger LOGGER = Logger.getLogger(PositiveTests.class.getName());

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
    public void CreateAndGetPeoplePositive() {

        vm.stubFor(get(urlEqualTo(houseServicePathForGet)).willReturn(
                aResponse().withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(houseJson)
                        .withStatus(HttpStatus.SC_OK)));

        PeopleDto postPerson = getPerson("Add", "Person");
        RestAssured.given()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .body(postPerson)
                .when()
                .post(pathForCreation)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(emptyString());

        Long personId = repository.findPeopleByFirstnameAndLastnameAndBirthdayAndStatus(postPerson.getFirstname(),
                postPerson.getLastname(), postPerson.getBirthday(),
                People.StatusEnum.fromValue(postPerson.getStatus().value())).get(0).getId();

        LOGGER.info("Send GET to created person with id = " + personId);

        PeopleDto getPerson = RestAssured.when()
                .get(pathForGetAndDelete, personId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .body()
                .as(PeopleDto.class);
        postPerson.setId(personId);
        Assertions.assertEquals(postPerson, getPerson, "Persons are different");
    }

    @Test
    public void DeletePeoplePositive() {

        PeopleDto newPerson = getPerson("Delete", "Person");
        LOGGER.info("Send POST to create a person");

        RestAssured.given()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .body(newPerson)
                .when()
                .post(pathForCreation)
                .then()
                .statusCode(HttpStatus.SC_OK);

        Long personId = repository.findPeopleByFirstnameAndLastnameAndBirthdayAndStatus(newPerson.getFirstname(),
                newPerson.getLastname(), newPerson.getBirthday(),
                People.StatusEnum.fromValue(newPerson.getStatus().value())).get(0).getId();

        LOGGER.info("Send DELETE to delete person");

        RestAssured.given()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .when()
                .delete(pathForGetAndDelete, personId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(emptyString());

        LOGGER.info("Send GET to check deletion");

        RestAssured.when()
                .get(pathForGetAndDelete, personId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body(emptyString());

    }

    @Test
    protected void GetChildrenAddressesPositive() {

        vm.stubFor(get(urlEqualTo(houseServicePathForGet)).willReturn(
                aResponse().withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(houseJson)
                        .withStatus(HttpStatus.SC_OK)));

        PeopleDto child = getPerson("Child", "One");
        List<PeopleDto> children = Collections.singletonList(child);

        LOGGER.info("Send POST to create a child");

        RestAssured.given()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .body(child)
                .when()
                .post(pathForCreation)
                .then()
                .statusCode(HttpStatus.SC_OK);

        PeopleDto parent = getPerson("Parent", "Person");
        parent.setChildren(children);

        LOGGER.info("Send POST to create a parent");

        RestAssured.given()
                .log()
                .body()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .body(parent)
                .when()
                .post(pathForCreation)
                .then()
                .statusCode(HttpStatus.SC_OK);

        Long personId = repository.findPeopleByFirstnameAndLastnameAndBirthdayAndStatus(parent.getFirstname(),
                        parent.getLastname(), parent.getBirthday(), People.StatusEnum.fromValue(parent.getStatus().value()))
                .get(0)
                .getId();

        LOGGER.info("Send GET to find children");

        AddressDto[] getAddress = RestAssured.given()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .when()
                .get(pathForGetChildren, personId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .body()
                .as(AddressDto[].class);

        Assertions.assertTrue(Arrays.asList(getAddress).contains(child.getAddressData()),
                "Address data of child doesn`t match with response");

        vm.verify(getRequestedFor(urlEqualTo(houseServicePathForGet)));
    }

    @Test
    protected void FindPersonByStatusPositive() {
        LOGGER.info("Set mock for HouseService");

        vm.stubFor(get(urlEqualTo(houseServicePathForGet)).atPriority(1)
                .willReturn(aResponse().withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(houseJson)
                        .withStatus(HttpStatus.SC_OK)));

        vm.stubFor(put(urlEqualTo(houseServicePathForPut)).atPriority(2)
                .willReturn(aResponse().withStatus(HttpStatus.SC_NO_CONTENT)));

        PeopleDto postPerson = getPerson("FindStatus", "Person");
        LOGGER.info("Send POST to create a person");

        RestAssured.given()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .body(postPerson)
                .when()
                .post(pathForCreation)
                .then()
                .statusCode(HttpStatus.SC_OK);

        LOGGER.info("Send GET with status and assert positive response");

        List people = RestAssured.given()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .queryParam("status", postPerson.getStatus().value().toLowerCase())
                .when()
                .get(pathForFindByStatus)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .body()
                .as(List.class);

        boolean exists = false;
        for (Object person : people) {
            ObjectMapper oMapper = new ObjectMapper();
            var map = oMapper.convertValue(person, Map.class);
            if (map.containsValue(postPerson.getFirstname())) {
                exists = true;
            }
        }
        Assertions.assertTrue(exists);

        vm.verify(getRequestedFor(urlEqualTo(houseServicePathForGet)));
        vm.verify(putRequestedFor(urlEqualTo(houseServicePathForPut)));

    }

    @Test
    public void UpdateExistedPeoplePutPositive() {

        vm.stubFor(get(urlEqualTo(houseServicePathForGet)).willReturn(
                aResponse().withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(houseJson)
                        .withStatus(HttpStatus.SC_OK)));

        LOGGER.info("Send POST to create a person");
        PeopleDto postPerson = getPerson("UpdateBirthday", "Person");

        RestAssured.given()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .body(postPerson)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .when()
                .post(pathForCreation);

        LOGGER.info("Send POST to update existing person");
        Long personId = repository.findPeopleByFirstnameAndLastnameAndBirthdayAndStatus(postPerson.getFirstname(),
                postPerson.getLastname(), postPerson.getBirthday(),
                People.StatusEnum.fromValue(postPerson.getStatus().value())).get(0).getId();
        postPerson.setBirthday(LocalDate.now().minusDays(5));
        postPerson.setId(personId);

        RestAssured.given()
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .body(postPerson)
                .when()
                .put(pathForCreation)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(emptyString());

        LOGGER.info("Send GET to check updated person and get data for assertion");

        PeopleDto getPerson = RestAssured.when()
                .get(pathForGetAndDelete, personId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .body()
                .as(PeopleDto.class);

        LOGGER.info("Start assertion");
        Assertions.assertEquals(LocalDate.now().minusDays(5), getPerson.getBirthday(), "Birthday dates are different!");
    }

    @Test
    public void UpdateExistedPeoplePostPositive() {

        vm.stubFor(get(urlEqualTo(houseServicePathForGet)).willReturn(
                aResponse().withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        .withBody(houseJson)
                        .withStatus(HttpStatus.SC_OK)));

        LOGGER.info("Send POST to create postPerson");
        PeopleDto postPerson = getPerson("UpdateName", "Person");

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

        LOGGER.info("Send POST to update postPerson in service");

        RestAssured.given()
                .contentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType())
                .formParam("firstname", "Upd" + postPerson.getFirstname())
                .formParam("lastname", "Upd" + postPerson.getLastname())
                .formParam("status", PeopleDto.StatusEnum.NOT_ALIVE)
                .when()
                .post(pathForGetAndDelete, personId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(emptyString());

        LOGGER.info("Send GET to check updated person and get data for assertion");

        PeopleDto getPerson = RestAssured.when()
                .get(pathForGetAndDelete, personId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .body()
                .as(PeopleDto.class);

        LOGGER.info("Start assertion");
        Assertions.assertEquals("Upd" + postPerson.getFirstname(), getPerson.getFirstname(),
                "Firstname are different!");
        Assertions.assertEquals("Upd" + postPerson.getLastname(), getPerson.getLastname(), "Lastname are different!");
        Assertions.assertEquals(PeopleDto.StatusEnum.NOT_ALIVE, getPerson.getStatus(), "Statuses are different!");

    }
}
