package ngfs.integratedpeoplemanagement.entity;

import javax.persistence.Embeddable;

@Embeddable
public class Addresses {

    private Long homeId;

    public Long getHomeId() {
        return homeId;
    }

    public void setHomeId(Long homeId) {
        this.homeId = homeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Addresses)) {
            return false;
        }

        Addresses addresses = (Addresses) o;

        return getHomeId() != null ? getHomeId().equals(addresses.getHomeId()) : addresses.getHomeId() == null;
    }

    @Override
    public int hashCode() {
        return getHomeId() != null ? getHomeId().hashCode() : 0;
    }
}
