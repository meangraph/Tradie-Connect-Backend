package CSIT3214.GroupProject.Model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeRange {
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    @Enumerated(EnumType.STRING)
    private Skill serviceType;


    public LocalDate getStartDate() {
        if (startDate != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(startDate, dateFormatter);
        }
        return null;
    }

    public LocalTime getStartTime() {
        if (startTime != null) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mma");
            return LocalTime.parse(startTime, timeFormatter);
        }
        return null;
    }

    public LocalDate getEndDate() {
        if (endDate != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(endDate, dateFormatter);
        }
        return null;
    }

    public LocalTime getEndTime() {
        if (endTime != null) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mma");
            return LocalTime.parse(endTime, timeFormatter);
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
