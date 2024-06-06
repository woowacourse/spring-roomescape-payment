package roomescape.client.fake;

public enum FakeHeaderConstant {
    AUTHORIZATION_HEADER("Authorization", "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6");

    private final String name;
    private final String value;

    FakeHeaderConstant(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
