package roomescape.util;

public class BasicAuthGenerator {

    private BasicAuthGenerator() {
    }

    public static String generate(String auth) {
        return "Basic " + auth;
    }
}
