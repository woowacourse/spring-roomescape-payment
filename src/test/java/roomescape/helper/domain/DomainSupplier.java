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
    protected ReservationTimeFixture reservationTimeFixture;

    @Autowired
    protected ThemeFixture themeFixture;

    @Autowired
    protected WaitingFixture waitingFixture;

    @Autowired
    protected ReservationPaymentFixture reservationPaymentFixture;
}
