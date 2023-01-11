package ngfs.integratedpeoplemanagement.service;

import ngfs.integratedpeoplemanagement.entity.People;
import ngfs.integratedpeoplemanagement.exception.InvalidIdException;
import ngfs.integratedpeoplemanagement.exception.PeopleAlreadyExistException;
import ngfs.integratedpeoplemanagement.exception.PeopleNotExistException;
import ngfs.integratedpeoplemanagement.houseservice.api.HouseApi;
import ngfs.integratedpeoplemanagement.houseservice.model.HouseDto;
import ngfs.integratedpeoplemanagement.peopleservice.model.AddressDto;
import ngfs.integratedpeoplemanagement.peopleservice.model.PeopleDto;
import ngfs.integratedpeoplemanagement.repository.PeopleRepository;
import ngfs.integratedpeoplemanagement.service.api.IntegratedPeopleMngmtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ngfs.integratedpeoplemanagement.mappers.PeopleFieldsMapper.MAPPER;

@Component
public class IntegratedPeopleMngmtServiceImpl implements IntegratedPeopleMngmtService {
    private final static Logger LOGGER = LoggerFactory.getLogger(IntegratedPeopleMngmtServiceImpl.class);

    private static final int INIT_VERSION = 1;

    private static final String INVALID_YEAR_MESSAGE = "Years before 1980 are not supported";

    private PeopleRepository repository;

    private HouseApi client;

    public void setClient(HouseApi client) {
        this.client = client;
    }

    @Override
    @Transactional
    public PeopleDto addPeople(PeopleDto person) throws PeopleAlreadyExistException {
        LOGGER.info("Will create Person in db");
        List<People> people = repository.findPeopleByFirstnameAndLastnameAndBirthdayAndStatus(person.getFirstname(),
                person.getLastname(), person.getBirthday(), People.StatusEnum.fromValue(person.getStatus().toString()));
        if (person.getBirthday().getYear() < 1980) {
            throw new ValidationException(INVALID_YEAR_MESSAGE);
        }
        if (!people.isEmpty()) {
            throw new PeopleAlreadyExistException(person.getFirstname(), person.getLastname());
        } else {
            People personEntity = MAPPER.dtoToEntity(person);
            List<People> relatives = personEntity.getRelatives()
                    .stream()
                    .map(p -> repository.findPeopleByFirstnameAndLastnameAndBirthdayAndStatus(p.getFirstname(),
                            p.getLastname(), p.getBirthday(), p.getStatus()).get(0))
                    .collect(Collectors.toList());
            List<People> children = personEntity.getChildren()
                    .stream()
                    .map(p -> repository.findPeopleByFirstnameAndLastnameAndBirthdayAndStatus(p.getFirstname(),
                            p.getLastname(), p.getBirthday(), p.getStatus()).get(0))
                    .collect(Collectors.toList());
            personEntity.setRelatives(relatives);
            personEntity.setChildren(children);
            personEntity.setVersion(INIT_VERSION);
            personEntity.setCreatedTimestamp(OffsetDateTime.now());
            personEntity.setLastModifiedTimestamp(OffsetDateTime.now());
            personEntity = repository.save(personEntity);
            person = MAPPER.entityToDto(personEntity);
            LOGGER.info("Person " + person.getFirstname() + " " + person.getLastname() + " was created");
        }
        return person;
    }

    @Override
    @Transactional
    public PeopleDto updatePeople(PeopleDto person) throws PeopleNotExistException {
        LOGGER.info("Will update Person in db");
        if (repository.findById(person.getId()).isEmpty()) {
            throw new PeopleNotExistException();
        }
        if (person.getBirthday().getYear() < 1980) {
            throw new ValidationException(INVALID_YEAR_MESSAGE);
        }
        People personEntity = MAPPER.dtoToEntity(person);
        List<People> relatives = personEntity.getRelatives()
                .stream()
                .map(p -> repository.findPeopleByFirstnameAndLastnameAndBirthdayAndStatus(p.getFirstname(),
                        p.getLastname(), p.getBirthday(), p.getStatus()).get(0))
                .collect(Collectors.toList());
        List<People> children = personEntity.getChildren()
                .stream()
                .map(p -> repository.findPeopleByFirstnameAndLastnameAndBirthdayAndStatus(p.getFirstname(),
                        p.getLastname(), p.getBirthday(), p.getStatus()).get(0))
                .collect(Collectors.toList());
        personEntity.setRelatives(relatives);
        personEntity.setChildren(children);
        personEntity.setVersion(repository.findById(person.getId()).get().getVersion() + 1);
        personEntity.setLastModifiedTimestamp(OffsetDateTime.now());
        personEntity = repository.save(personEntity);
        person = MAPPER.entityToDto(personEntity);
        LOGGER.info("Person " + person.getFirstname() + " " + person.getLastname() + " was updated");

        return person;
    }

    @Override
    @Transactional
    public void deletePeople(Long personId) throws PeopleNotExistException {
        LOGGER.info("Will delete Person from db");
        if (repository.findById(personId).isEmpty()) {
            throw new PeopleNotExistException();
        } else {
            People person = repository.findById(personId).get();
            List<People> relatives = repository.findByRelativesContains(person);
            relatives.forEach(p -> {
                LOGGER.info("Found " + p.getFirstname() + " " + p.getLastname());
                p.getRelatives().remove(person);
                repository.save(p);
            });
            repository.delete(person);
            LOGGER.info("Person " + person.getFirstname() + " " + person.getLastname() + " was deleted");
        }
    }

    @Override
    public List<People> getPeopleBirthdayLessThan(LocalDate birthday) {
        List<People> persons = repository.findByBirthdayLessThan(birthday);

        if (!persons.isEmpty()) {
            LOGGER.info("Next people with birthday less then " + birthday + " were found: ");
            for (People person : persons) {
                LOGGER.info(
                        person.getFirstname() + " " + person.getLastname() + " with birthday " + person.getBirthday());
            }
        } else {
            LOGGER.info("No people were found by set requirements");
        }
        return persons;
    }

    @Override
    public List<People> getPeopleBirthdayMoreThan(LocalDate birthday) {
        List<People> persons = repository.findByBirthdayGreaterThan(birthday);
        if (!persons.isEmpty()) {
            LOGGER.info("Next people with birthday more then " + birthday + " were found: ");
            for (People person : persons) {
                LOGGER.info(
                        person.getFirstname() + " " + person.getLastname() + " with birthday " + person.getBirthday());
            }
        } else {
            LOGGER.info("No people were found by set requirements");
        }
        return persons;
    }

    @Override
    public List<People> getPeopleBirthdayStatusOrdered(LocalDate birthday) {
        List<People> persons = repository.findByBirthdayGreaterThanOrderByStatusDesc(birthday);
        if (!persons.isEmpty()) {
            LOGGER.info("Next people with birthday more then " + birthday + " were found and ordered: ");
            for (People person : persons) {
                LOGGER.info(
                        person.getFirstname() + " " + person.getLastname() + " with birthday " + person.getBirthday());
            }
        } else {
            LOGGER.info("No people were found by set requirements");
        }
        return persons;
    }

    @Override
    @Transactional
    public PeopleDto getPeopleById(Long peopleId) throws PeopleNotExistException {
        People personEntity;
        if (peopleId <= 0) {
            throw new InvalidIdException();
        }
        if (repository.findById(peopleId).isEmpty()) {
            throw new PeopleNotExistException();
        } else {
            personEntity = repository.findById(peopleId).get();
        }
        if (personEntity.getRegistrationAddress() != null) {
            LOGGER.info("Registration address found, house service will be called");
            HouseDto houseDto = client.getHouseById(personEntity.getRegistrationAddress().getHomeId());
            LOGGER.info("House with house={} has been found", houseDto);
        }
        return MAPPER.entityToDto(personEntity);
    }

    @Override
    @Transactional
    public List<PeopleDto> findPeopleByStatuses(ArrayList<String> statuses) {
        List<People> people = new ArrayList<>();
        for (String status : statuses) {
            if (repository.findByStatus(People.StatusEnum.fromValue(status)).size() != 0) {
                people.addAll(repository.findByStatus(People.StatusEnum.fromValue(status)));
            } else {
                LOGGER.info("No people found with status: " + status);
            }
        }

        List<HouseDto> houseDtos = people.stream()
                .filter(p -> p.getRegistrationAddress() != null)
                .map(p -> p.getRegistrationAddress().getHomeId())
                .map(homeId -> client.getHouseById(homeId))
                .collect(Collectors.toList());
        houseDtos.forEach(houseDto1 -> houseDto1.setLastUsed(LocalDate.now()));
        houseDtos.forEach(houseDto1 -> client.updateHouse(houseDto1));

        List<PeopleDto> peopleDto = new ArrayList<>();
        for (

                People person : people) {
            PeopleDto personDto = MAPPER.entityToDto(person);
            peopleDto.add(personDto);
        }
        return peopleDto;
    }

    @Override
    @Transactional
    public List<AddressDto> getChildrenAddresses(Long personId) throws PeopleNotExistException {
        List<AddressDto> childrenAdresses = new ArrayList<>();
        if (repository.findById(personId).isEmpty()) {
            throw new PeopleNotExistException();
        } else {
            People personEntity = repository.findById(personId).get();
            PeopleDto person = MAPPER.entityToDto(personEntity);
            for (int i = 0; i < person.getChildren().size(); i++) {
                if (personEntity.getRegistrationAddress() != null) {
                    LOGGER.info("Registration address found, house service will be called");
                    HouseDto houseDto = client.getHouseById(personEntity.getRegistrationAddress().getHomeId());
                    LOGGER.info("House with house={} has been found", houseDto);
                }
                childrenAdresses.add(person.getChildren().get(i).getAddressData());
            }
        }
        return childrenAdresses;
    }

    @Override
    @Transactional
    public PeopleDto updatePeopleWithForm(Long peopleId, String firstname, String lastname, String status)
            throws PeopleNotExistException {
        LOGGER.info("Will update Person in db");
        PeopleDto person;
        if (repository.findById(peopleId).isEmpty()) {
            throw new PeopleNotExistException();
        } else {
            People personEntity = repository.findById(peopleId).get();
            List<People> relatives = personEntity.getRelatives()
                    .stream()
                    .map(p -> repository.findPeopleByFirstnameAndLastnameAndBirthdayAndStatus(p.getFirstname(),
                            p.getLastname(), p.getBirthday(), p.getStatus()).get(0))
                    .collect(Collectors.toList());
            List<People> children = personEntity.getChildren()
                    .stream()
                    .map(p -> repository.findPeopleByFirstnameAndLastnameAndBirthdayAndStatus(p.getFirstname(),
                            p.getLastname(), p.getBirthday(), p.getStatus()).get(0))
                    .collect(Collectors.toList());
            personEntity.setRelatives(relatives);
            personEntity.setChildren(children);
            personEntity.setVersion(personEntity.getVersion() + 1);
            personEntity.setLastModifiedTimestamp(OffsetDateTime.now());
            personEntity.setFirstname(firstname);
            personEntity.setLastname(lastname);
            personEntity.setStatus(People.StatusEnum.fromValue(status));
            personEntity = repository.save(personEntity);
            person = MAPPER.entityToDto(personEntity);
            LOGGER.info("Person " + person.getFirstname() + " " + person.getLastname() + " was updated");
        }
        return person;
    }

    @Autowired
    public void setRepository(PeopleRepository repository) {
        this.repository = repository;
    }
}
