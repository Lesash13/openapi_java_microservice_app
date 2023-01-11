package ngfs.integratedpeoplemanagement.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "peo_las_fir_bir_uq", columnNames = {"lastname", "firstname",
        "birthday"}), indexes = {@Index(name = "peo_par_id_idx", columnList = "parent_id"),
        @Index(name = "peo_fir_las_idx", columnList = "firstname, lastname")})
public class People extends BaseEntity {

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(foreignKey = @ForeignKey(name = "peo_own_hou_peo_id_fk"))
    private List<Addresses> ownedHouses = new ArrayList<>();

    @NotNull
    private LocalDate birthday;

    @Size(min = 1, max = 20)
    private String firstname;

    @Size(min = 1, max = 20)
    private String lastname;

    @Embedded
    private Addresses registrationAddress;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.REFRESH})
    @JoinTable(foreignKey = @ForeignKey(name = "peo_peo_id_fk"), inverseForeignKey = @ForeignKey(name =
            "peo_rel_id_fk"))
    private List<People> relatives = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.REFRESH})
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "peo_par_id_fk"))
    private List<People> children = new ArrayList<>();

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public List<People> getRelatives() {
        return relatives;
    }

    public void setRelatives(List<People> relatives) {
        this.relatives = relatives;
    }

    public List<People> getChildren() {
        return children;
    }

    public void setChildren(List<People> children) {
        this.children = children;
    }

    public Addresses getRegistrationAddress() {
        return registrationAddress;
    }

    public void setRegistrationAddress(Addresses registrationAddress) {
        this.registrationAddress = registrationAddress;
    }

    public List<Addresses> getOwnedHouses() {
        return ownedHouses;
    }

    public void setOwnedHouses(List<Addresses> ownedHouses) {
        this.ownedHouses = ownedHouses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof People)) {
            return false;
        }
        People people = (People) o;
        return Objects.equals(getOwnedHouses(), people.getOwnedHouses()) &&
                getBirthday().equals(people.getBirthday()) && getFirstname().equals(people.getFirstname()) &&
                getLastname().equals(people.getLastname()) &&
                Objects.equals(getRegistrationAddress(), people.getRegistrationAddress()) &&
                getStatus() == people.getStatus() && Objects.equals(getRelatives(), people.getRelatives()) &&
                Objects.equals(getChildren(), people.getChildren());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOwnedHouses(), getBirthday(), getFirstname(), getLastname(), getRegistrationAddress(),
                getStatus(), getRelatives(), getChildren());
    }

    public enum StatusEnum {
        ALIVE("alive"), NOT_ALIVE("not alive");

        private final String value;

        StatusEnum(String value) {
            this.value = value;
        }

        public static StatusEnum fromValue(String value) {
            for (StatusEnum status : StatusEnum.values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }

        public String getValue() {
            return value;
        }
    }
}
