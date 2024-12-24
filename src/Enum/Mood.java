package Enum;

public enum Mood {
    HOPES("надеется"),
    ENJOY("радуется"),
    ANNOYED("раздосадован"),
    AFRAID("опасается"),
    NONE("нейтральное");

    private final String description;


    Mood(String description) {
        this.description = description;
    }


    public String getDescription() {
        return description;
    }
}
