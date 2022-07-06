package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobOffer {
    private String title;
    private String salary;
    private String description;
    private LocalDate startDate;
    private List<String> technologies;
    private String mainProgrammingLanguage;

    public JobOffer(String title) {
        this.title = title;
    }

    public JobOffer(String title, String salary, String description, LocalDate startDate, List<String> technologies) {
        this.title = title;
        this.salary = salary;
        this.description = description;
        this.startDate = startDate;
        this.technologies = technologies;
    }
}
