package roomescape.payment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Schedule;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

class PaymentTest {
    private final BigDecimal amount = BigDecimal.valueOf(1000L);
    private final Member member = new Member(1L, "커찬", "kuchn@abc.com");
    private final LocalDate date = LocalDate.of(2050, 10, 10);
    private final ReservationTime time = new ReservationTime(LocalTime.of(9, 0));
    private final Theme theme = new Theme(
            "오리와 호랑이",
            "오리들과 호랑이들 사이에서 살아남기",
            "https://image.jpg");
    private final Schedule schedule = new Schedule(date, time, theme);

    @DisplayName("결제 키가 비어있을 때 예외를 던진다.")
    @Test
    void validatePaymentTest_whenPaymentKeyIsNull() {
        assertThatThrownBy(() ->
                new Payment(null, amount, member, schedule))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("결제 금액이 비어있을 때 예외를 던진다.")
    @Test
    void validatePaymentTest_whenAmountIsNull() {
        assertThatThrownBy(() ->
                new Payment("pay_12345", null, member, schedule))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("결제자가 비어있을 때 예외를 던진다.")
    @Test
    void validatePaymentTest_whenMemberIsNull() {
        assertThatThrownBy(() ->
                new Payment("pay_12345", amount, null, schedule))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("스케줄이 비어있을 때 예외를 던진다.")
    @Test
    void validatePaymentTest_whenScheduleIsNull() {
        assertThatThrownBy(() ->
                new Payment("pay_12345", amount, member, null))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("결제가 완료된 상태임을 확인한다.")
    @Test
    void isPaidTest_whenPaymentIsCompleted() {
        Payment payment = new Payment("pay_12345", amount, member, schedule);

        assertThat(payment.isPaid()).isTrue();
    }
}
