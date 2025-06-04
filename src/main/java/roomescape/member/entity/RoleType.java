package roomescape.member.entity;

public enum RoleType {
    GUEST("GUEST"),
    USER("USER"),
    ADMIN("ADMIN");

    private final String value;

    RoleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
