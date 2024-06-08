package roomescape.client.fake;

public enum FakeHeaderConstant {
    AUTHORIZATION_HEADER("Authorization", "Basic dGVzdF9nc2tfZG9jc19PYVB6OEw1S2RtUVhrelJ6M3k0N0JNdzY6");

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
