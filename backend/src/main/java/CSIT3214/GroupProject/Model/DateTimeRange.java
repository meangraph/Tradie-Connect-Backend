package CSIT3214.GroupProject.Model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeRange {
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;


    public LocalDate getStartDate() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(startDate, dateFormatter);
    }

    public LocalTime getStartTime() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mma");
        return LocalTime.parse(startTime, timeFormatter);
    }

    public LocalDate getEndDate() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(endDate, dateFormatter);
    }

    public LocalTime getEndTime() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mma");
        return LocalTime.parse(endTime, timeFormatter);
    }
}
