package roomescape.payment.domain;

import roomescape.global.exception.NotFoundException;

import java.util.Arrays;

public enum PGCompany {
    TOSS("TOSS");

    private final String identifier;

    PGCompany(String identifier) {
        this.identifier = identifier;
    }

    public static PGCompany from(String identifier) {
        return Arrays.stream(PGCompany.values())
                .filter(status -> status.identifier.equals(identifier))
                .findAny()
                .orElseThrow(() -> new NotFoundException(identifier + "가 식별자인 PG사가 없습니다."));
    }

    public String getIdentifier() {
        return identifier;
    }
}
