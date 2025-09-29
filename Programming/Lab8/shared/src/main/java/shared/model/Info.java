package shared.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Info implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    int numberOfPerson;
    String type;
    LocalDateTime dateOfInit;
    long yourPerson;

    public Info(int numberOfPerson, String type, LocalDateTime dateOfInit, long yourPerson)  {
        this.numberOfPerson = numberOfPerson;
        this.type = type;
        this.dateOfInit = dateOfInit;
        this.yourPerson = yourPerson;
    }

    public int getNumberOfPersons() {
        return numberOfPerson;
    }

    public void setNumberOfPersons(int numberOfPerson) {
        this.numberOfPerson = numberOfPerson;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getDateOfInit() {
        return dateOfInit;
    }

    public void setDateOfInit(LocalDateTime dateOfInit) {
        this.dateOfInit = dateOfInit;
    }

    public long getYourPersons() {
        return yourPerson;
    }

    public void setYourPersons(int yourPerson) {
        this.yourPerson = yourPerson;
    }
}

