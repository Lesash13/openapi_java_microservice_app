package ngfs.integratedpeoplemanagement.api;

import ngfs.integratedpeoplemanagement.exception.PeopleAlreadyExistException;
import ngfs.integratedpeoplemanagement.exception.PeopleNotExistException;
import ngfs.integratedpeoplemanagement.peopleservice.model.PeopleDto;
import ngfs.integratedpeoplemanagement.repository.PeopleRepository;
import ngfs.integratedpeoplemanagement.service.IntegratedPeopleMngmtServiceImpl;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionRuleResult;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class IntegratedPeopleMngmtApiTest {

    @MockBean
    PeopleRepository repository;

    @MockBean
    DmnDecision dmnDecision;

    @MockBean
    DmnEngine dmnEngine;

    @Autowired
    private PeopleApiImpl api;

    @MockBean
    private IntegratedPeopleMngmtServiceImpl service;

    private PeopleDto people;

    @BeforeEach
    public void setUp() {
        people = new PeopleDto();
        people.setId(0L);
        people.setFirstname("Firstname");
        people.setLastname("Lastname");
        people.setStatus(PeopleDto.StatusEnum.ALIVE);
        people.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    public void testFindPeopleByStatus() {
        VariableMap variableMap = Variables.putValue("statusInput", "alive");
        DmnDecisionTableResult result = mock(DmnDecisionTableResult.class);
        DmnDecisionRuleResult ruleResult = mock(DmnDecisionRuleResult.class);
        when(result.getSingleResult()).thenReturn(ruleResult);
        when(ruleResult.getSingleEntry()).thenReturn("alive");
        when(dmnEngine.evaluateDecisionTable(dmnDecision, variableMap)).thenReturn(result);
        try (Response response = api.findPeopleByStatus("alive")) {
            verify(service, times(1)).findPeopleByStatuses(new ArrayList<>(Collections.singletonList("alive")));
            Assertions.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        }
    }

    @Test
    public void testFindPeopleByStatusNonExist() {
        try (Response response = api.findPeopleByStatus("Alive")) {
            Assertions.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
        }
    }

    @Test
    public void testNewPeopleCreation() throws PeopleAlreadyExistException {
        try (Response response = api.addPeople(people)) {
            verify(service, times(1)).addPeople(people);
            Assertions.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        }
    }

    @Test
    public void testExistingPeopleDeletion() throws PeopleNotExistException {
        try (Response response = api.deletePeople(people.getId())) {
            verify(service, times(1)).deletePeople(people.getId());
            Assertions.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        }
    }

    @Test
    public void testExistingPeopleUpdating() throws PeopleNotExistException {
        try (Response response = api.updatePeople(people)) {
            verify(service, times(1)).updatePeople(people);
            Assertions.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        }
    }

    @Test
    public void testExistingPeopleUpdatingWithFrom() throws PeopleNotExistException {
        try (Response response = api.updatePeopleWithForm(people.getId(), people.getFirstname(), people.getLastname(),
                people.getStatus().toString())) {
            verify(service, times(1)).updatePeopleWithForm(people.getId(), people.getFirstname(), people.getLastname(),
                    people.getStatus().toString());
            Assertions.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        }
    }
}
