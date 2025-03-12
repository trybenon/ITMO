package Enum;

public enum Christen {
    CHRISTEN("крещеный"),
    NOTCHRISTEN("некрещеный");

    private final String description;

    Christen(String description){
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}

