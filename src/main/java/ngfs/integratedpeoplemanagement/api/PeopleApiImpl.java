package ngfs.integratedpeoplemanagement.api;

import ngfs.integratedpeoplemanagement.exception.InvalidStatusException;
import ngfs.integratedpeoplemanagement.exception.PeopleAlreadyExistException;
import ngfs.integratedpeoplemanagement.exception.PeopleNotExistException;
import ngfs.integratedpeoplemanagement.peopleservice.api.PeopleApi;
import ngfs.integratedpeoplemanagement.peopleservice.model.AddressDto;
import ngfs.integratedpeoplemanagement.peopleservice.model.PeopleDto;
import ngfs.integratedpeoplemanagement.service.IntegratedPeopleMngmtServiceImpl;
import ngfs.integratedpeoplemanagement.service.api.IntegratedPeopleMngmtService;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * People management service
 *
 * <p>This is a sample API of People management service.
 */
@Component
public class PeopleApiImpl implements PeopleApi {
    private final static Logger LOGGER = LoggerFactory.getLogger(PeopleApi.class);

    private IntegratedPeopleMngmtService service;

    private DmnDecision dmnDecision;

    private DmnEngine dmnEngine;

    @Autowired
    public void setService(IntegratedPeopleMngmtServiceImpl service) {
        this.service = service;
    }

    @Autowired
    public void setDmnDecision(DmnDecision dmnDecision) {
        this.dmnDecision = dmnDecision;
    }

    @Autowired
    public void setDmnEngine(DmnEngine dmnEngine) {
        this.dmnEngine = dmnEngine;
    }

    @Override
    public Response addPeople(@Valid PeopleDto body) {
        try {
            service.addPeople(body);
            LOGGER.info("Person {} has been added", body.getFirstname() + " " + body.getLastname());
            return Response.ok().build();
        } catch (PeopleAlreadyExistException e) {
            e.printStackTrace();
            return Response.status(Response.Status.FORBIDDEN).build();
        }

    }

    @Override
    public Response deletePeople(Long peopleId) {
        try {
            service.deletePeople(peopleId);
            LOGGER.info("Person with id = {} has been deleted", peopleId);
            return Response.ok().build();
        } catch (PeopleNotExistException e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Override
    public Response findPeopleByStatus(@NotNull @Size(min = 4) String status) {
        try {
            String[] statuses = status.split(",", 2);
            checkStatusValues(statuses);
            ArrayList<String> states = Arrays.stream(statuses)
                    .map(this::getStatusFromDmnEngine)
                    .collect(Collectors.toCollection(ArrayList::new));
            List<PeopleDto> people = service.findPeopleByStatuses(states);
            LOGGER.info("People with status = {} have been found", status);
            return Response.ok().entity(people).build();
        } catch (IllegalArgumentException e) {
            LOGGER.info("Status is wrong, possible values are: alive, not alive, but entered: {}", status);
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    private void checkStatusValues(String[] statuses) {
        for (String status : statuses) {
            if (PeopleDto.StatusEnum.fromValue(status).equals(PeopleDto.StatusEnum.ALIVE) ||
                    PeopleDto.StatusEnum.fromValue(status).equals(PeopleDto.StatusEnum.NOT_ALIVE)) {
                LOGGER.info("Status posted: " + status);
            } else {
                throw new InvalidStatusException();
            }
        }
    }

    private String getStatusFromDmnEngine(String status) {
        VariableMap variableMap = Variables.putValue("statusInput", status);
        DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(dmnDecision, variableMap);
        return result.getSingleResult().getSingleEntry();
    }

    @Override
    public Response getChildrenAddresses(Long peopleId) {
        List<AddressDto> childrenAddresses;
        try {
            childrenAddresses = service.getChildrenAddresses(peopleId);
            LOGGER.info("Children of the person with id = {} has been found", peopleId);
            return Response.ok(childrenAddresses).build();
        } catch (PeopleNotExistException e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Override
    public Response getPeopleById(Long peopleId) {
        PeopleDto people;
        try {
            people = service.getPeopleById(peopleId);
            LOGGER.info("Person with id = {} has been found", peopleId);
            return Response.ok().entity(people).build();
        } catch (PeopleNotExistException e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Override
    public Response updatePeople(@Valid PeopleDto body) {

        try {
            service.updatePeople(body);
            LOGGER.info("Person with id = {} has been updated", body.getId());
            return Response.ok().build();
        } catch (PeopleNotExistException e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Override
    public Response updatePeopleWithForm(Long peopleId, String firstname, String lastname, String status) {
        try {
            service.updatePeopleWithForm(peopleId, firstname, lastname, status);
            LOGGER.info("Person with id = {} has been updated", peopleId);
            return Response.ok().build();
        } catch (PeopleNotExistException e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}

