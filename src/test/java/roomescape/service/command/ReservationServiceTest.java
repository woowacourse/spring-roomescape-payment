package roomescape.service.command;

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
import roomescape.exception.BadRequestException;
import roomescape.exception.NotFoundException;
import roomescape.exception.PaymentException;
import roomescape.mvc.member.domain.Member;
import roomescape.mvc.member.domain.Role;
import roomescape.mvc.member.service.MemberQueryService;
import roomescape.mvc.payment.dto.PaymentCreationContent;
import roomescape.mvc.payment.dto.PaymentResult;
import roomescape.mvc.payment.repository.PaymentRepository;
import roomescape.mvc.payment.service.PaymentService;
import roomescape.mvc.reservation.domain.Reservation;
import roomescape.mvc.reservation.dto.ReservationCreationContent;
import roomescape.mvc.reservation.repository.ReservationRepository;
import roomescape.mvc.reservation.response.AddReservationByAdmin;
import roomescape.mvc.reservation.service.ReservationQueryService;
import roomescape.mvc.reservation.service.ReservationService;
import roomescape.mvc.theme.domain.Theme;
import roomescape.mvc.theme.service.ThemeQueryService;
import roomescape.mvc.time.domain.ReservationTime;
import roomescape.mvc.time.service.ReservationTimeQueryService;
import roomescape.mvc.waiting.domain.Waiting;
import roomescape.mvc.waiting.service.WaitingQueryService;
import roomescape.test.stub.PaymentClientStub;

@DataJpaTest
@Import(value = {
        PaymentService.class, MemberQueryService.class, ThemeQueryService.class,
        ReservationTimeQueryService.class, ReservationQueryService.class, WaitingQueryService.class,
        ReservationService.class, PaymentClientStub.class
})
class ReservationServiceTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private PaymentClientStub paymentClientStub;

    private ReservationTime reservationTime;
    private Theme theme;
    private Member member;

    @BeforeEach
    void setup() {
        reservationTime = entityManager.persist(
                ReservationTime.createWithoutId(LocalTime.of(10, 0)));
        theme = entityManager.persist(
                Theme.createWithoutId("테마", "테마 설명", "thumbnail.jpg"));
        member = entityManager.persist(
                Member.createWithoutId(Role.GENERAL, "회원", "member@test.com", "password123!"));

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
                    reservationService.addReservationByAdmin(member.getId(), creationContent);

            // then
            assertAll(
                    () -> assertThat(reservationRepository.findAll()).hasSize(1),
                    () -> assertThat(paymentRepository.findAll()).hasSize(0)
            );
        }

        @DisplayName("유저가 존재하지 않을 경우 예약을 추가할 수 없다.")
        @Test
        void cannotAddReservationByInvalidMember() {
            // given
            long wrongMemberId = member.getId() + 100;
            ReservationCreationContent creationContent =
                    new ReservationCreationContent(theme.getId(), NEXT_DAY, reservationTime.getId());

            // when & then
            assertThatThrownBy(() -> reservationService.addReservationByAdmin(wrongMemberId, creationContent))
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
            assertThatThrownBy(() -> reservationService.addReservationByAdmin(member.getId(), creationContent))
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
            assertThatThrownBy(() -> reservationService.addReservationByAdmin(member.getId(), creationContent))
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
                    () -> reservationService.addReservationByAdmin(member.getId(), duplicatedCreationContent))
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
            assertThatThrownBy(() -> reservationService.addReservationByAdmin(member.getId(), creationContentWithPast))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("과거 예약은 생성할 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("결제정보를 포함하여 예약을 추가할 수 있다")
    public class addReservationWithPayment {

        @DisplayName("결제 성공시 예약이 성공한다.")
        @Test
        void canReserveWhenPaymentSuccess() {
            // given
            PaymentResult paymentResult = new PaymentResult("order_id", "payment_key", 1000L);
            paymentClientStub.setAuthorizePayment(paymentResult);

            ReservationCreationContent reservationCreationContent =
                    new ReservationCreationContent(theme.getId(), NEXT_DAY, reservationTime.getId());
            PaymentCreationContent paymentCreationContent =
                    new PaymentCreationContent("order_id", "payment_key", 1000L);

            // when
            reservationService.addReservationByMember(
                    member.getId(), reservationCreationContent, paymentCreationContent);

            // then
            assertAll(
                    () -> assertThat(reservationRepository.findAll()).hasSize(1),
                    () -> assertThat(paymentRepository.findAll()).hasSize(1)
            );
        }

        @DisplayName("결제 실패시 예약이 실패한다.")
        @Test
        void cannotReserveWhenPaymentFail() {
            // given
            PaymentException paymentException = new PaymentException("결제 승인 실패");
            paymentClientStub.setAuthorizePayment(paymentException);

            ReservationCreationContent reservationCreationContent =
                    new ReservationCreationContent(theme.getId(), NEXT_DAY, reservationTime.getId());
            PaymentCreationContent paymentCreationContent =
                    new PaymentCreationContent("order_id", "payment_key", 1000L);

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> reservationService.addReservationByMember(
                            member.getId(), reservationCreationContent, paymentCreationContent))
                            .isInstanceOf(PaymentException.class),
                    () -> assertThat(reservationRepository.findAll()).hasSize(0)
            );
        }
    }

    @Nested
    @DisplayName("예약을 삭제할 수 있다.")
    class deleteReservationById {

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

        @DisplayName("예약 대기가 존재할 경우 첫번째 예약 대기를 예약으로 등록한다.")
        @Test
        void canAddNewReservationWithWaiting() {
            // given
            Reservation reservation = entityManager.persist(
                    Reservation.createWithoutIdAndPaymentHistory(NEXT_DAY, reservationTime, theme, member));

            Member firstWaitingMember = entityManager.persist(
                    Member.createWithoutId(Role.GENERAL, "회원1", "waiting1@email.com", "qwer1234!"));
            Member secondWaitingMember = entityManager.persist(
                    Member.createWithoutId(Role.GENERAL, "회원2", "waiting2@email.com", "qwer1234!"));

            Waiting firstWaiting = entityManager.persist(
                    Waiting.createWithoutIdWithoutPayment(
                            reservation.getDate(), reservation.getTheme(),
                            reservation.getReservationTime(), firstWaitingMember));

            entityManager.persist(
                    Waiting.createWithoutIdWithoutPayment(
                            reservation.getDate(), reservation.getTheme(),
                            reservation.getReservationTime(), secondWaitingMember));

            entityManager.flush();
            entityManager.clear();

            // when
            reservationService.deleteReservationById(reservation.getId());

            // then
            List<Reservation> newReservation = reservationRepository.findByMember(firstWaitingMember);
            assertAll(
                    () -> assertThat(newReservation).hasSize(1),
                    () -> assertThat(newReservation.getFirst().getDate()).isEqualTo(firstWaiting.getDate()),
                    () -> assertThat(newReservation.getFirst().getTheme()).isEqualTo(firstWaiting.getTheme()),
                    () -> assertThat(newReservation.getFirst().getReservationTime()).isEqualTo(firstWaiting.getTime()),
                    () -> assertThat(newReservation.getFirst().getMember()).isEqualTo(firstWaiting.getMember())
            );
        }

        @DisplayName("예약 대기가 존재해서 첫번째 예약 대기가 예약으로 등록된 경우 첫번째 예약 대기는 삭제된다.")
        @Test
        void canDeleteFirstWaiting() {
            // given
            Reservation reservation = entityManager.persist(
                    Reservation.createWithoutIdAndPaymentHistory(NEXT_DAY, reservationTime, theme, member));

            Member firstWaitingMember = entityManager.persist(
                    Member.createWithoutId(Role.GENERAL, "회원1", "waiting1@email.com", "qwer1234!"));
            Member secondWaitingMember = entityManager.persist(
                    Member.createWithoutId(Role.GENERAL, "회원2", "waiting2@email.com", "qwer1234!"));

            Waiting firstWaiting = entityManager.persist(
                    Waiting.createWithoutIdWithoutPayment(
                            reservation.getDate(), reservation.getTheme(),
                            reservation.getReservationTime(), firstWaitingMember));
            Waiting secondWaiting = entityManager.persist(
                    Waiting.createWithoutIdWithoutPayment(
                            reservation.getDate(), reservation.getTheme(),
                            reservation.getReservationTime(), secondWaitingMember));

            entityManager.flush();
            entityManager.clear();

            // when
            reservationService.deleteReservationById(reservation.getId());

            // then
            Waiting deletedFirstWaiting = entityManager.find(Waiting.class, firstWaiting.getId());
            assertThat(deletedFirstWaiting).isNull();
        }
    }
}
