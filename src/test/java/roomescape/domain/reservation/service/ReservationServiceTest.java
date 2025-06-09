package roomescape.domain.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.test.fixture.DateFixture.NEXT_DAY;
import static roomescape.test.fixture.DateFixture.YESTERDAY;

import java.time.LocalTime;
import java.util.List;
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
import roomescape.domain.payment.service.PaymentQueryService;
import roomescape.domain.reservation.domain.Reservation;
import roomescape.domain.reservation.dto.ReservationCreationContent;
import roomescape.domain.reservation.repository.ReservationRepository;
import roomescape.domain.reservation.response.AddReservationByAdmin;
import roomescape.domain.theme.domain.Theme;
import roomescape.domain.theme.service.ThemeQueryService;
import roomescape.domain.time.domain.ReservationTime;
import roomescape.domain.time.service.ReservationTimeQueryService;
import roomescape.domain.waiting.domain.Waiting;
import roomescape.domain.waiting.service.WaitingQueryService;
import roomescape.exception.BadRequestException;
import roomescape.exception.NotFoundException;

@DataJpaTest
@Import(value = {
        PaymentQueryService.class, MemberQueryService.class, ThemeQueryService.class,
        ReservationTimeQueryService.class, ReservationQueryService.class, WaitingQueryService.class,
        ReservationService.class, PaymentQueryService.class
})
class ReservationServiceTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationService reservationService;

    private ReservationTime reservationTime;
    private Theme theme;
    private Member member;
    private Payment payment;

    @BeforeEach
    void setup() {
        reservationTime = entityManager.persist(
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
    @DisplayName("결제 정보 없이 예약을 추가할 수 있다.")
    class addReservation {

        @DisplayName("예약을 성공적으로 추가할 수 있다.")
        @Test
        void canAddReservation() {
            // given
            ReservationCreationContent creationContent =
                    new ReservationCreationContent(theme.getId(), NEXT_DAY, reservationTime.getId());

            // when
            AddReservationByAdmin reservationResponse =
                    reservationService.addReservationWithoutPayment(member.getId(), creationContent);

            // then
            assertThat(reservationRepository.findAll()).hasSize(1);
        }

        @DisplayName("유저가 존재하지 않을 경우 예약을 추가할 수 없다.")
        @Test
        void cannotAddReservationByInvalidMember() {
            // given
            long wrongMemberId = member.getId() + 100;
            ReservationCreationContent creationContent =
                    new ReservationCreationContent(theme.getId(), NEXT_DAY, reservationTime.getId());

            // when & then
            assertThatThrownBy(() -> reservationService.addReservationWithoutPayment(wrongMemberId, creationContent))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("ID에 해당하는 회원을 찾을 수 없습니다.");
        }

        @DisplayName("테마가 존재하지 않을 경우 예약을 추가할 수 없다.")
        @Test
        void cannotAddReservationByInvalidTheme() {
            // given
            long wrongThemeId = theme.getId() + 100;
            ReservationCreationContent creationContent =
                    new ReservationCreationContent(wrongThemeId, NEXT_DAY, reservationTime.getId());

            // when & then
            assertThatThrownBy(() -> reservationService.addReservationWithoutPayment(member.getId(), creationContent))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("ID에 해당하는 테마는 존재하지 않습니다.");
        }

        @DisplayName("예약시간이 존재하지 않을 경우 예약을 추가할 수 없다.")
        @Test
        void cannotAddReservationByInvalidTime() {
            // given
            long wrongTimeId = reservationTime.getId() + 100;
            ReservationCreationContent creationContent =
                    new ReservationCreationContent(theme.getId(), NEXT_DAY, wrongTimeId);

            // when & then
            assertThatThrownBy(() -> reservationService.addReservationWithoutPayment(member.getId(), creationContent))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("ID에 해당하는 예약시간은 존재하지 않습니다.");
        }

        @DisplayName("예약이 중복일 경우 예약을 추가할 수 없다.")
        @Test
        void cannotAddReservationByDuplicationReservation() {
            // given
            Reservation alreadySavedReservation = entityManager.persist(
                    Reservation.createWithoutIdAndPaymentHistory(NEXT_DAY, reservationTime, theme, member));

            ReservationCreationContent duplicatedCreationContent = new ReservationCreationContent(
                    alreadySavedReservation.getTheme().getId(),
                    alreadySavedReservation.getDate(),
                    alreadySavedReservation.getReservationTime().getId());

            entityManager.flush();
            entityManager.clear();

            // when & then
            assertThatThrownBy(
                    () -> reservationService.addReservationWithoutPayment(member.getId(), duplicatedCreationContent))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("중복된 예약 입니다.");
        }

        @DisplayName("과거의 시간으로 예약을 할 수 없다.")
        @Test
        void cannotAddReservationByPastDateTime() {
            // given
            ReservationCreationContent creationContentWithPast =
                    new ReservationCreationContent(theme.getId(), YESTERDAY, reservationTime.getId());

            // when & then
            assertThatThrownBy(
                    () -> reservationService.addReservationWithoutPayment(member.getId(), creationContentWithPast))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("과거 예약은 생성할 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("결제정보가 포함된 예약을 추가할 수 있다")
    public class addReservationWithPayment {

        @DisplayName("정상적으로 결제 정보가 포함된 예약을 추가할 수 있다.")
        @Test
        void canReserveWhenPaymentSuccess() {
            // given
            ReservationCreationContent reservationCreationContent =
                    new ReservationCreationContent(theme.getId(), NEXT_DAY, reservationTime.getId());

            // when
            reservationService.addReservationWithPayment(
                    member.getId(), payment.getId(), reservationCreationContent);

            // then
            assertThat(reservationRepository.findAll()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("대기가 없는 예약을 삭제할 수 있다.")
    class deleteReservationWithoutWaiting {

        @DisplayName("예약을 성공적으로 삭제할 수 있다.")
        @Test
        void canDelete() {
            // given
            Reservation reservation = entityManager.persist(
                    Reservation.createWithoutIdAndPaymentHistory(NEXT_DAY, reservationTime, theme, member));

            entityManager.flush();
            entityManager.clear();

            // when
            reservationService.deleteReservationById(reservation.getId());

            // then
            List<Reservation> allReservations = reservationRepository.findAll();
            assertThat(allReservations).isEmpty();
        }

        @DisplayName("존재하지 않는 예약을 삭제할 수 없다.")
        @Test
        void cannotDeleteByInvalidReservationId() {
            // given
            long invalidReservationId = 10;

            // when & then
            assertThatThrownBy(() -> reservationService.deleteReservationById(invalidReservationId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("ID에 해당하는 예약을 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("대기가 존재하는 예약을 삭제할 수 있다.")
    class deleteReservationWithWaiting {

        private Reservation reservation;
        private Member firsMember;
        private Member sercondMember;


        @BeforeEach
        void setup() {
            reservation = entityManager.persist(
                    Reservation.createWithoutIdAndPaymentHistory(NEXT_DAY, reservationTime, theme, member));

            firsMember = entityManager.persist(
                    Member.createWithoutId(Role.GENERAL, "회원1", "waiting1@email.com", "qwer1234!"));
            sercondMember = entityManager.persist(
                    Member.createWithoutId(Role.GENERAL, "회원2", "waiting2@email.com", "qwer1234!"));

            entityManager.flush();
            entityManager.clear();
        }

        @DisplayName("예약이 삭제될 경우 첫번째 예약 대기를 예약으로 등록한다.")
        @Test
        void canAddNewReservationWithWaiting() {
            // given
            Waiting firstWaiting = entityManager.persist(
                    Waiting.createWithoutIdWithoutPayment(
                            reservation.getDate(), reservation.getTheme(),
                            reservation.getReservationTime(), firsMember));
            Waiting secondWaiting = entityManager.persist(
                    Waiting.createWithoutIdWithoutPayment(
                            reservation.getDate(), reservation.getTheme(),
                            reservation.getReservationTime(), sercondMember));

            entityManager.flush();
            entityManager.clear();

            // when
            reservationService.deleteReservationById(reservation.getId());

            // then
            List<Reservation> newReservation = reservationRepository.findByMember(firsMember);
            assertAll(
                    () -> assertThat(newReservation).hasSize(1),
                    () -> assertThat(newReservation.getFirst().getDate()).isEqualTo(firstWaiting.getDate()),
                    () -> assertThat(newReservation.getFirst().getTheme()).isEqualTo(firstWaiting.getTheme()),
                    () -> assertThat(newReservation.getFirst().getReservationTime()).isEqualTo(firstWaiting.getTime()),
                    () -> assertThat(newReservation.getFirst().getMember()).isEqualTo(firstWaiting.getMember())
            );
        }

        @DisplayName("첫번째 예약 대기가 예약으로 등록된 경우 첫번째 예약 대기는 삭제된다.")
        @Test
        void canDeleteFirstWaiting() {
            // given
            Waiting firstWaiting = entityManager.persist(
                    Waiting.createWithoutIdWithoutPayment(
                            reservation.getDate(), reservation.getTheme(),
                            reservation.getReservationTime(), firsMember));
            Waiting secondWaiting = entityManager.persist(
                    Waiting.createWithoutIdWithoutPayment(
                            reservation.getDate(), reservation.getTheme(),
                            reservation.getReservationTime(), sercondMember));

            entityManager.flush();
            entityManager.clear();

            // when
            reservationService.deleteReservationById(reservation.getId());

            // then
            Waiting deletedFirstWaiting = entityManager.find(Waiting.class, firstWaiting.getId());
            assertThat(deletedFirstWaiting).isNull();
        }

        @DisplayName("결제데이터가 존재하는 예약 대기를 예약으로 등록할 수 있다.")
        @Test
        void canConvertWaitingWithPaymentToReservation() {
            // given
            Payment payment = entityManager.persist(
                    Payment.createWithoutId("order_id", "payment_key", 1000L));

            Waiting firstWaiting = entityManager.persist(
                    Waiting.createWithoutId(
                            reservation.getDate(), reservation.getTheme(),
                            reservation.getReservationTime(), firsMember, payment));
            Waiting secondWaiting = entityManager.persist(
                    Waiting.createWithoutIdWithoutPayment(
                            reservation.getDate(), reservation.getTheme(),
                            reservation.getReservationTime(), sercondMember));

            entityManager.flush();
            entityManager.clear();

            // when
            reservationService.deleteReservationById(reservation.getId());

            // then
            List<Reservation> newReservation = reservationRepository.findByMember(firsMember);

            assertAll(
                    () -> assertThat(newReservation).hasSize(1),
                    () -> assertThat(newReservation.getFirst().getDate()).isEqualTo(firstWaiting.getDate()),
                    () -> assertThat(newReservation.getFirst().getTheme()).isEqualTo(firstWaiting.getTheme()),
                    () -> assertThat(newReservation.getFirst().getReservationTime()).isEqualTo(firstWaiting.getTime()),
                    () -> assertThat(newReservation.getFirst().getMember()).isEqualTo(firstWaiting.getMember()),
                    () -> assertThat(newReservation.getFirst().getPayment()).isEqualTo(firstWaiting.getPayment())
            );
        }
    }
}
