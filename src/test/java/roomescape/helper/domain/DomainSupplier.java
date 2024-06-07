package roomescape.helper.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class DomainSupplier {
    @Autowired
    protected MemberFixture memberFixture;

    @Autowired
    protected ReservationFixture reservationFixture;

    @Autowired
    protected ReservationTimeFixture timeFixture;

    @Autowired
    protected ThemeFixture themeFixture;

    @Autowired
    protected WaitingFixture waitingFixture;
}
