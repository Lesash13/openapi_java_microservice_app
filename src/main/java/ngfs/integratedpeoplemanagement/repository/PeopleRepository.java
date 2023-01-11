package ngfs.integratedpeoplemanagement.repository;

import ngfs.integratedpeoplemanagement.entity.People;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PeopleRepository extends CrudRepository<People, Long> {

    List<People> findByBirthdayLessThan(LocalDate date);

    List<People> findByBirthdayGreaterThan(LocalDate date);

    List<People> findByBirthdayGreaterThanOrderByStatusDesc(LocalDate date);

    List<People> findPeopleByFirstnameAndLastnameAndBirthdayAndStatus(@Size(min = 1, max = 20) String firstname,
                                                                      @Size(min = 1, max = 20) String lastname, @NotNull @PastOrPresent LocalDate birthday,
                                                                      People.StatusEnum status);

    List<People> findByRelativesContains(People people);

    List<People> findByStatus(People.StatusEnum status);
}