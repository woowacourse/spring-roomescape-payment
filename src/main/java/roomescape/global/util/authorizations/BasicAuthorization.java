package roomescape.global.util.authorizations;

import java.util.StringJoiner;
import org.springframework.stereotype.Component;
import roomescape.global.util.Authorization;

@Component
public class BasicAuthorization implements Authorization {

    private static final String PREFIX = "Basic";
    private static final String DELIMITER = " ";

    @Override
    public String getHeader(String value) {
        StringJoiner joiner = new StringJoiner(DELIMITER);
        joiner.add(PREFIX).add(value);
        return joiner.toString();
    }
}
