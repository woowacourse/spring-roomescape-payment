package roomescape.reservation.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createTimeAt_10;

import java.time.LocalDate;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.member.domain.Password;

class ReservationTest {

    private Member defaultMember = Member.builder()
            .name("김철수")
            .email("kim@example.com")
            .password(Password.createForMember("pass123"))
            .role(MemberRole.MEMBER)
            .build();

    @Test
    void 새_예약의_id_필드는_null이다() {
        // given
        RegistrationSlot registrationSlot = RegistrationSlot.builder()
                .theme(createDefaultTheme())
                .date(DEFAULT_DATE)
                .time(createTimeAt_10())
                .build();

        Reservation reservation = Reservation.builder()
                .member(defaultMember)
                .registrationSlot(registrationSlot)
                .build();

        // when & then
        assertThat(reservation.getId()).isNull();
    }

    @Test
    void id_필드를_제외한_필드가_null이면_예외처리() {
        // given
        RegistrationSlot registrationSlot = RegistrationSlot.builder()
                .theme(createDefaultTheme())
                .date(DEFAULT_DATE)
                .time(createTimeAt_10())
                .build();

        // when
        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThatThrownBy(() -> {
                Reservation.builder()
                        .member(null)
                        .registrationSlot(registrationSlot)
                        .build();
            }).isInstanceOf(NullPointerException.class);

            softly.assertThatThrownBy(() -> {
                Reservation.builder()
                        .member(createDefaultMember_1())
                        .registrationSlot(null)
                        .build();
            }).isInstanceOf(NullPointerException.class);
        });
    }

    @Test
    void 예약_시간이_현재_이후면_객체가_정상_생성된다() {
        // given
        Member member = Member.builder()
                .name("김철수")
                .email("kim@example.com")
                .password(Password.createForMember("pass123"))
                .role(MemberRole.MEMBER)
                .build();

        RegistrationSlot registrationSlot = RegistrationSlot.builder()
                .theme(createDefaultTheme())
                .date(LocalDate.now().plusDays(1))
                .time(createTimeAt_10())
                .build();

        // when & then
        assertDoesNotThrow(() -> Reservation.builder()
                .member(member)
                .registrationSlot(registrationSlot)
                .build());
    }
}
