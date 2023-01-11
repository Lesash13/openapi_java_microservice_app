package ngfs.integratedpeoplemanagement.service;

import ngfs.integratedpeoplemanagement.entity.People;
import ngfs.integratedpeoplemanagement.exception.PeopleAlreadyExistException;
import ngfs.integratedpeoplemanagement.exception.PeopleNotExistException;
import ngfs.integratedpeoplemanagement.houseservice.api.HouseApi;
import ngfs.integratedpeoplemanagement.houseservice.model.HouseDto;
import ngfs.integratedpeoplemanagement.peopleservice.model.AddressDto;
import ngfs.integratedpeoplemanagement.peopleservice.model.PeopleDto;
import ngfs.integratedpeoplemanagement.repository.PeopleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static ngfs.integratedpeoplemanagement.mappers.PeopleFieldsMapper.MAPPER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {IntegratedPeopleMngmtServiceImpl.class})
public class IntegratedPeopleMngmtServiceTest {

    @Autowired
    private IntegratedPeopleMngmtServiceImpl service;

    @Mock
    private HouseApi client;

    @MockBean
    private PeopleRepository repository;

    private PeopleDto person;

    private static AddressDto addressDto(Long id) {
        AddressDto addressPrs = new AddressDto();
        addressPrs.setRegistrationId(id);
        addressPrs.setHomeIds(Collections.singletonList(1L));
        return addressPrs;
    }

    @BeforeEach
    public void setUp() {
        Long id = 1L;
        person = new PeopleDto();
        person.setId(id);
        person.setStatus(PeopleDto.StatusEnum.ALIVE);
        person.setFirstname("Firstname" + id);
        person.setLastname("Lastname" + id);
        person.setBirthday(LocalDate.now());
        person.setAddressData(addressDto(id));
        service.setClient(client);
    }

    @Test
    public void testPeopleCreation() throws PeopleAlreadyExistException {
        People personEntity = MAPPER.dtoToEntity(person);
        when(repository.findPeopleByFirstnameAndLastnameAndBirthdayAndStatus(personEntity.getFirstname(),
                personEntity.getLastname(), personEntity.getBirthday(), personEntity.getStatus())).thenReturn(
                Collections.emptyList());
        when(repository.save(personEntity)).thenReturn(personEntity);
        PeopleDto result = service.addPeople(person);
        verify(repository, times(1)).save(personEntity);
        assertEquals(person, result);
    }

    @Test
    public void testPeopleCreationAlreadyExists() {
        People personEntity = MAPPER.dtoToEntity(person);
        when(repository.findPeopleByFirstnameAndLastnameAndBirthdayAndStatus(personEntity.getFirstname(),
                personEntity.getLastname(), personEntity.getBirthday(), personEntity.getStatus())).thenReturn(
                Collections.singletonList(personEntity));
        when(repository.save(personEntity)).thenReturn(personEntity);
        assertThrows(PeopleAlreadyExistException.class, () -> service.addPeople(person));
        verify(repository, times(0)).save(personEntity);
    }

    @Test
    public void testPeopleUpdating() throws PeopleNotExistException {
        MAPPER.dtoToEntity(person);
        person.setLastname("Lastname2");
        People personEntity = MAPPER.dtoToEntity(person);
        personEntity.setVersion(1);
        when(repository.findById(personEntity.getId())).thenReturn(Optional.of(personEntity));
        when(repository.save(personEntity)).thenReturn(personEntity);
        PeopleDto result = service.updatePeople(person);
        verify(repository, times(1)).save(personEntity);
        assertEquals(person, result);
    }

    @Test
    public void testPeopleUpdatingNoPerson() {
        People personEntity = MAPPER.dtoToEntity(person);
        when(repository.findPeopleByFirstnameAndLastnameAndBirthdayAndStatus(personEntity.getFirstname(),
                personEntity.getLastname(), personEntity.getBirthday(), personEntity.getStatus())).thenReturn(
                Collections.singletonList(personEntity));
        when(repository.save(personEntity)).thenReturn(personEntity);
        person.setLastname("Lastname2");
        assertThrows(PeopleNotExistException.class, () -> service.updatePeople(person));
        verify(repository, times(0)).save(personEntity);
    }

    @Test
    public void testPeopleDeletion() throws PeopleNotExistException {
        People personEntity = MAPPER.dtoToEntity(person);
        when(repository.findById(person.getId())).thenReturn(Optional.of(personEntity));
        when(repository.save(personEntity)).thenReturn(personEntity);
        service.deletePeople(personEntity.getId());
        Mockito.verify(repository, Mockito.times(1)).delete(personEntity);
    }

    @Test
    public void testPeopleDeletionNoPerson() {
        People personEntity = MAPPER.dtoToEntity(person);
        when(repository.findById(person.getId())).thenReturn(Optional.empty());
        when(repository.save(personEntity)).thenReturn(personEntity);
        assertThrows(PeopleNotExistException.class, () -> service.updatePeople(person));
        Mockito.verify(repository, Mockito.times(0)).delete(personEntity);
    }

    @Test
    public void testPeopleFindByBirthdayLessThan() {

    }

    @Test
    public void testPeopleFindByBirthdayGreaterThan() {
        People personEntity = MAPPER.dtoToEntity(person);
        when(repository.findByBirthdayGreaterThan(LocalDate.now())).thenReturn(new ArrayList<>(List.of(personEntity)));
        List<People> result = service.getPeopleBirthdayMoreThan(LocalDate.now());
        verify(repository, times(1)).findByBirthdayGreaterThan(LocalDate.now());
        assertEquals(List.of(personEntity), result);
    }

    @Test
    public void testPeopleFindByBirthdayGreaterThanOrdered() {
        People personEntity = MAPPER.dtoToEntity(person);
        when(repository.findByBirthdayGreaterThanOrderByStatusDesc(LocalDate.now())).thenReturn(
                new ArrayList<>(List.of(personEntity)));
        List<People> result = service.getPeopleBirthdayStatusOrdered(LocalDate.now());
        verify(repository, times(1)).findByBirthdayGreaterThanOrderByStatusDesc(LocalDate.now());
        assertEquals(List.of(personEntity), result);
    }

    @Test
    public void testPeopleSearchLessThanNonexistentPeople() {
        when(repository.findByBirthdayLessThan(LocalDate.now())).thenReturn(new ArrayList<>());
        verify(repository, times(0)).findByBirthdayLessThan(LocalDate.now());
    }

    @Test
    public void testPeopleSearchMoreThanNonexistentPeople() {
        when(repository.findByBirthdayGreaterThan(LocalDate.now())).thenReturn(new ArrayList<>());
        verify(repository, times(0)).findByBirthdayGreaterThan(LocalDate.now());
    }

    @Test
    public void testPeopleSearchMoreThanOrderedNonexistentPeople() {
        when(repository.findByBirthdayGreaterThanOrderByStatusDesc(LocalDate.now())).thenReturn(new ArrayList<>());
        verify(repository, times(0)).findByBirthdayGreaterThanOrderByStatusDesc(LocalDate.now());
    }

    @Test
    public void testPeopleUpdatingWithForm() throws PeopleNotExistException {
        People personEntity = MAPPER.dtoToEntity(person);
        personEntity.setVersion(1);
        when(repository.findById(personEntity.getId())).thenReturn(Optional.of(personEntity));
        when(repository.save(personEntity)).thenReturn(personEntity);
        PeopleDto result = service.updatePeopleWithForm(personEntity.getId(), personEntity.getFirstname(),
                personEntity.getLastname(), personEntity.getStatus().getValue());
        verify(repository, times(1)).save(personEntity);
        assertEquals(person, result);
    }

    @Test
    public void testPeopleUpdatingWithFormNonexistentPeople() {
        People personEntity = MAPPER.dtoToEntity(person);
        when(repository.findById(personEntity.getId())).thenReturn(Optional.empty());
        assertThrows(PeopleNotExistException.class,
                () -> service.updatePeopleWithForm(personEntity.getId(), personEntity.getFirstname(),
                        personEntity.getLastname(), personEntity.getStatus().toString()));
        verify(repository, times(0)).save(personEntity);
    }

    @Test
    public void testGetPeopleById() throws PeopleNotExistException {
        service.setClient(client);
        People personEntity = MAPPER.dtoToEntity(person);
        when(repository.findById(personEntity.getId())).thenReturn(Optional.of(personEntity));
        PeopleDto result = service.getPeopleById(personEntity.getId());
        verify(repository, times(2)).findById(personEntity.getId());
        assertEquals(result, person);
    }

    @Test
    public void testFindPeopleByStatus() {
        People personEntity = MAPPER.dtoToEntity(person);
        when(repository.findByStatus(People.StatusEnum.fromValue(personEntity.getStatus().getValue()))).thenReturn(
                Collections.singletonList(personEntity));
        when(client.getHouseById(personEntity.getRegistrationAddress().getHomeId())).thenReturn(new HouseDto());
        List<PeopleDto> result = service.findPeopleByStatuses(
                new ArrayList<>(Collections.singletonList(personEntity.getStatus().getValue())));
        verify(repository, times(2)).findByStatus(People.StatusEnum.fromValue(personEntity.getStatus().getValue()));
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), person);
    }

    @Test
    public void testFindChildrenAddress() throws PeopleNotExistException {
        People personEntity = MAPPER.dtoToEntity(person);
        PeopleDto child1 = person;
        child1.setId(2L);
        PeopleDto child2 = person;
        child2.setId(3L);
        person.setChildren(List.of(child1, child2));

        when(repository.findById(personEntity.getId())).thenReturn(Optional.of(personEntity));
        List<AddressDto> addresses = service.getChildrenAddresses(personEntity.getId());
        verify(repository, times(2)).findById(personEntity.getId());
        IntStream.iterate(0, i -> i < addresses.size(), i -> i += 1).forEach(i -> assertEquals(addresses.get(i), person.getChildren().get(i).getAddressData()));
    }

    @Test
    public void testFindEmptyChildrenAddress() throws PeopleNotExistException {
        People personEntity = MAPPER.dtoToEntity(person);
        when(repository.findById(personEntity.getId())).thenReturn(Optional.of(personEntity));
        List<AddressDto> result = service.getChildrenAddresses(personEntity.getId());
        verify(repository, times(2)).findById(personEntity.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindChildrenAddressOfNotExist() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(PeopleNotExistException.class, () -> service.getChildrenAddresses(0L));
    }
}