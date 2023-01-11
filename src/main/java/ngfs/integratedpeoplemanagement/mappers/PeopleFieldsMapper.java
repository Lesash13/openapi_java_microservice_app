package ngfs.integratedpeoplemanagement.mappers;

import ngfs.integratedpeoplemanagement.entity.Addresses;
import ngfs.integratedpeoplemanagement.entity.People;
import ngfs.integratedpeoplemanagement.peopleservice.model.PeopleDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PeopleFieldsMapper {

    PeopleFieldsMapper MAPPER = Mappers.getMapper(PeopleFieldsMapper.class);

    @Mapping(source = "firstname", target = "firstname")
    @Mapping(source = "lastname", target = "lastname")
    @Mapping(source = "birthday", target = "birthday")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "relatives", target = "relatives")
    @Mapping(source = "children", target = "children")
    @Mapping(source = "addressData.homeIds", target = "ownedHouses")
    @Mapping(source = "addressData.registrationId", target = "registrationAddress.homeId")

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdTimestamp", ignore = true)
    @Mapping(target = "lastModifiedTimestamp", ignore = true)
    @Mapping(target = "registrationAddress", ignore = true)
    People dtoToEntity(PeopleDto peopleDto);

    @Mapping(target = "homeId", expression = "java(id)")
    Addresses longToAddress(Long id);

    default Long addressToLong(Addresses addresses) {
        return addresses.getHomeId();
    }

    @InheritInverseConfiguration
    PeopleDto entityToDto(People people);

}