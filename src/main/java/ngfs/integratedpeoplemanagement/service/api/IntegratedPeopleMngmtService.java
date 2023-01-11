package ngfs.integratedpeoplemanagement.service.api;

import ngfs.integratedpeoplemanagement.entity.People;
import ngfs.integratedpeoplemanagement.exception.PeopleAlreadyExistException;
import ngfs.integratedpeoplemanagement.exception.PeopleNotExistException;
import ngfs.integratedpeoplemanagement.peopleservice.model.AddressDto;
import ngfs.integratedpeoplemanagement.peopleservice.model.PeopleDto;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public interface IntegratedPeopleMngmtService {

    PeopleDto addPeople(PeopleDto person) throws PeopleAlreadyExistException;

    @Transactional
    PeopleDto updatePeople(PeopleDto person) throws PeopleNotExistException;

    @Transactional
    void deletePeople(Long personId) throws PeopleNotExistException;

    List<People> getPeopleBirthdayLessThan(LocalDate birthday);

    List<People> getPeopleBirthdayMoreThan(LocalDate birthday);

    List<People> getPeopleBirthdayStatusOrdered(LocalDate birthday);

    PeopleDto getPeopleById(Long peopleId) throws PeopleNotExistException;

    List<PeopleDto> findPeopleByStatuses(ArrayList<String> statuses);

    List<AddressDto> getChildrenAddresses(Long personId) throws PeopleNotExistException;

    PeopleDto updatePeopleWithForm(Long peopleId, String firstname, String lastname, String status)
            throws PeopleNotExistException;
}
