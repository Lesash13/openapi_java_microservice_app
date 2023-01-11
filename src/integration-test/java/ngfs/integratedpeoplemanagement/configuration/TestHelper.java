package ngfs.integratedpeoplemanagement.configuration;

import com.github.tomakehurst.wiremock.client.WireMock;
import ngfs.integratedpeoplemanagement.peopleservice.model.AddressDto;
import ngfs.integratedpeoplemanagement.peopleservice.model.PeopleDto;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Random;

public class TestHelper {

    protected final String pathForCreation = "/people";

    protected final String pathForGetAndDelete = "/people/{id}";

    protected final String houseJson = """
            {"id": 0,"city": "St. Petersburh","street": "12-13 Line V.O.","number": 14,"lastUsed": "2020-03-16"}""";

    protected final String houseServicePathForGet = "/house/0";

    protected final String pathForGetChildren = "/people/{id}/getChildrenAdresses";

    protected final String pathForFindByStatus = "/people/findByStatus";

    protected final String houseServicePathForPut = "/house";

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

    @BeforeEach
    public void resetRequests() {
        WireMock.resetAllRequests();
    }
}
