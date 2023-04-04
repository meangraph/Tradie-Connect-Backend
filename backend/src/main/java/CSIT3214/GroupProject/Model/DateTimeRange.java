package CSIT3214.GroupProject.Model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeRange {
    private String date;
    private String startTime;

    public LocalDate getDate() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, dateFormatter);
    }

    public LocalTime getStartTime() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mma");
        return LocalTime.parse(startTime, timeFormatter);
    }
}
