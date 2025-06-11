package roomescape.domain.waiting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.test.fixture.DateFixture.NEXT_DAY;
import static roomescape.test.fixture.DateFixture.YESTERDAY;

import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import roomescape.domain.member.domain.Member;
import roomescape.domain.member.domain.Role;
import roomescape.domain.member.service.MemberQueryService;
import roomescape.domain.payment.domain.Payment;
import roomescape.domain.payment.dto.PaymentCreationContent;
import roomescape.domain.payment.repository.PaymentRepository;
import roomescape.domain.payment.service.PaymentQueryService;
import roomescape.domain.reservation.domain.Reservation;
import roomescape.domain.reservation.service.ReservationQueryService;
import roomescape.domain.theme.domain.Theme;
import roomescape.domain.theme.service.ThemeQueryService;
import roomescape.domain.time.domain.ReservationTime;
import roomescape.domain.time.service.ReservationTimeQueryService;
import roomescape.domain.waiting.domain.Waiting;
import roomescape.domain.waiting.dto.WaitingCreationContent;
import roomescape.domain.waiting.repository.WaitingRepository;
import roomescape.exception.BadRequestException;
import roomescape.exception.NotFoundException;

@DataJpaTest
@Import(value = {
        PaymentQueryService.class, MemberQueryService.class, ThemeQueryService.class,
        ReservationTimeQueryService.class, ReservationQueryService.class, WaitingQueryService.class,
        WaitingService.class
})
class WaitingServiceTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private WaitingRepository waitingRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private WaitingService waitingService;

    private ReservationTime time;
    private Theme theme;
    private Member member;
    private Payment payment;

    @BeforeEach
    void setup() {
        time = entityManager.persist(
                ReservationTime.createWithoutId(LocalTime.of(10, 0)));
        theme = entityManager.persist(
                Theme.createWithoutId("테마", "테마 설명", "thumbnail.jpg"));
        member = entityManager.persist(
                Member.createWithoutId(Role.GENERAL, "회원", "member@test.com", "password123!"));
        payment = entityManager.persist(
                Payment.createWithoutId("order_id", "payment_id", 1000L));

        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("대기 데이터를 추가할 수 있다.")
    public class addWaiting {

        @DisplayName("정상적으로 대기 데이터를 추가할 수 있다.")
        @Test
        void addWaitingTest() {
            // given
            entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(NEXT_DAY, time, theme, member));

            WaitingCreationContent creationContent =
                    new WaitingCreationContent(NEXT_DAY, theme.getId(), time.getId(), member.getId());

            entityManager.flush();
            entityManager.clear();

            // when
            waitingService.addWaiting(creationContent, payment.getId());

            // then
            assertAll(
                    () -> assertThat(waitingRepository.findAll()).hasSize(1),
                    () -> assertThat(paymentRepository.findAll()).hasSize(1)
            );
        }

        @DisplayName("테마가 유효하지 않은 경우 대기 데이터를 추가할 수 없다.")
        @Test
        void cannotAddByInvalidTheme() {
            // given
            WaitingCreationContent creationContent =
                    new WaitingCreationContent(NEXT_DAY, theme.getId() + 100, time.getId(), member.getId());

            // when & then
            assertThatThrownBy(() -> waitingService.addWaiting(creationContent, payment.getId()))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("ID에 해당하는 테마는 존재하지 않습니다.");
        }

        @DisplayName("예약시간이 유효하지 않은 경우 대기 데이터를 추가할 수 없다.")
        @Test
        void cannotAddByInvalidTime() {
            // given
            WaitingCreationContent creationContent =
                    new WaitingCreationContent(NEXT_DAY, theme.getId(), time.getId() + 100, member.getId());
            PaymentCreationContent paymentCreationContent =
                    new PaymentCreationContent("312313", "12312re", 1000L);

            // when & then
            assertThatThrownBy(() -> waitingService.addWaiting(creationContent, payment.getId()))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("ID에 해당하는 예약시간이 존재하지 않습니다.");
        }

        @DisplayName("회원이 유효하지 않은 경우 대기 데이터를 추가할 수 없다.")
        @Test
        void cannotAddByInvalidMember() {
            // given
            WaitingCreationContent creationContent =
                    new WaitingCreationContent(NEXT_DAY, theme.getId(), time.getId(), member.getId() + 100);
            PaymentCreationContent paymentCreationContent =
                    new PaymentCreationContent("312313", "12312re", 1000L);

            // when & then
            assertThatThrownBy(() -> waitingService.addWaiting(creationContent, payment.getId()))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("ID에 해당하는 회원을 찾을 수 없습니다.");
        }

        @DisplayName("과거의 대기 데이터를 저장하는 것은 허용하지 않는다.")
        @Test
        void cannotAddByPastWaiting() {
            // given
            entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(YESTERDAY, time, theme, member));

            entityManager.flush();
            entityManager.clear();

            WaitingCreationContent creationContent =
                    new WaitingCreationContent(YESTERDAY, theme.getId(), time.getId(), member.getId());
            PaymentCreationContent paymentCreationContent =
                    new PaymentCreationContent("312313", "12312re", 1000L);

            // when & then
            assertThatThrownBy(() -> waitingService.addWaiting(creationContent, payment.getId()))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("과거 날짜와 시간으로 예약 대기를 생성할 수 없습니다.");
        }

        @DisplayName("대기 데이터의 중복 저장은 허용하지 않늗다.")
        @Test
        void cannotAddByDuplicatedWaiting() {
            // given
            entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(NEXT_DAY, time, theme, member));

            entityManager.persist(Waiting.createWithoutIdWithoutPayment(NEXT_DAY, theme, time, member));

            entityManager.flush();
            entityManager.clear();

            WaitingCreationContent creationContent =
                    new WaitingCreationContent(NEXT_DAY, theme.getId(), time.getId(), member.getId());
            PaymentCreationContent paymentCreationContent =
                    new PaymentCreationContent("312313", "12312re", 1000L);

            // when & then
            assertThatThrownBy(() -> waitingService.addWaiting(creationContent, payment.getId()))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("중복된 예약 대기는 허용하지 않습니다.");
        }

        @DisplayName("예약이 존재하지 않는 경우 대기 데이터를 저장할 수 없다.")
        @Test
        void cannotAddByEmptyReservation() {
            // given
            WaitingCreationContent creationContent =
                    new WaitingCreationContent(NEXT_DAY.plusDays(100), theme.getId(), time.getId(), member.getId());
            PaymentCreationContent paymentCreationContent =
                    new PaymentCreationContent("312313", "12312re", 1000L);

            // when & then
            assertThatThrownBy(() -> waitingService.addWaiting(creationContent, payment.getId()))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("예약이 존재하지 않는 예약 대기는 허용하지 않습니다.");
        }
    }

    @Nested
    @DisplayName("대기 데이터를 삭제할 수 있다.")
    public class deleteWaitingById {

        @DisplayName("대기 데이터를 삭제할 수 있다.")
        @Test
        void canDeleteWaiting() {
            // given
            Waiting waiting = entityManager.persist(
                    Waiting.createWithoutIdWithoutPayment(NEXT_DAY, theme, time, member));

            entityManager.flush();
            entityManager.clear();

            // when
            waitingService.deleteWaitingById(waiting.getId());

            // then
            assertThat(entityManager.find(Waiting.class, waiting.getId())).isNull();
        }

        @DisplayName("존재하지 않는 대기 데이터를 제거할 경우 예외를 발생시킨다.")
        @Test
        void cannotDeleteWaiting() {
            // when & then
            assertThatThrownBy(() -> waitingService.deleteWaitingById(100L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("ID에 해당하는 대기가 존재하지 않습니다.");
        }
    }
}
