//This holds the values of cost and startdate/time in one object so we can use @requestbody and get all the values.

package CSIT3214.GroupProject.DataAccessLayer;

import CSIT3214.GroupProject.Model.DateTimeRange;
import CSIT3214.GroupProject.Model.Skill;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Getter
@Setter
public class CreateServiceRequestDTO {
    private Long serviceRequestId;
    private Double cost;
    private DateTimeRange dateTimeRange;
    private String description;
    @Enumerated(EnumType.STRING)
    private Skill serviceType;

    public LocalDate getStartDate() {
        if (dateTimeRange != null) {
            return dateTimeRange.getStartDate();
        }
        return null;
    }

    public LocalTime getStartTime() {
        if (dateTimeRange != null) {
            return dateTimeRange.getStartTime();
        }
        return null;
    }

    public LocalDate getEndDate() {
        if (dateTimeRange != null) {
            return dateTimeRange.getEndDate();
        }
        return null;
    }

    public LocalTime getEndTime() {
        if (dateTimeRange != null) {
            return dateTimeRange.getEndTime();
        }
        return null;
    }

    public Skill getServiceType() {
        return serviceType;
    }

    public void setServiceType(Skill serviceType) {
        this.serviceType = serviceType;
    }
}
