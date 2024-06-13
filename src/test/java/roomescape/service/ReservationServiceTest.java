package roomescape.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static roomescape.fixture.MemberFixture.DEFAULT_ADMIN;
import static roomescape.fixture.MemberFixture.DEFAULT_MEMBER;
import static roomescape.fixture.ReservationTimeFixture.DEFAULT_TIME;
import static roomescape.fixture.ThemeFixture.DEFAULT_THEME;

import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.domain.Reservation;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.exception.ExceptionType;
import roomescape.exception.RoomescapeException;
import roomescape.fixture.PaymentFixture;
import roomescape.fixture.ReservationFixture;
import roomescape.fixture.ReservationWaitingFixture;
import roomescape.repository.CollectionMemberRepository;
import roomescape.repository.CollectionPaymentRepository;
import roomescape.repository.CollectionReservationRepository;
import roomescape.repository.CollectionReservationTimeRepository;
import roomescape.repository.CollectionReservationWaitingRepository;
import roomescape.repository.CollectionThemeRepository;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationWaitingRepository;
import roomescape.service.finder.ReservationFinder;

class ReservationServiceTest {
    private ReservationService reservationService;
    private ReservationRepository reservationRepository;
    private CollectionReservationTimeRepository reservationTimeRepository;
    private CollectionThemeRepository themeRepository;
    private CollectionMemberRepository memberRepository;
    private ReservationWaitingRepository waitingRepository;
    private PaymentRepository paymentRepository;

    @BeforeEach
    void initService() {
        reservationTimeRepository = new CollectionReservationTimeRepository();
        themeRepository = new CollectionThemeRepository();
        memberRepository = new CollectionMemberRepository();
        waitingRepository = new CollectionReservationWaitingRepository();
        reservationRepository = new CollectionReservationRepository();
        paymentRepository = new CollectionPaymentRepository();
        ReservationFinder reservationFinder = new ReservationFinder(reservationRepository, reservationTimeRepository,
                memberRepository, themeRepository);
        reservationService = new ReservationService(reservationRepository, waitingRepository, reservationFinder,
                memberRepository, paymentRepository);
    }

    @Test
    @DisplayName("없는 시간에 예약 시도시 실패하는지 확인")
    void saveFailWhenTimeNotFound() {
        Assertions.assertThatThrownBy(() -> reservationService.save(ReservationFixture.DEFAULT_REQUEST))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(ExceptionType.NOT_FOUND_RESERVATION_TIME.getMessage());
    }

    @Test
    @DisplayName("없는 테마에 예약 시도시 실패하는지 확인")
    void saveFailWhenThemeNotFound() {
        reservationTimeRepository.save(DEFAULT_TIME);

        Assertions.assertThatThrownBy(() -> reservationService.save(ReservationFixture.DEFAULT_REQUEST))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(ExceptionType.NOT_FOUND_THEME.getMessage());
    }

    @Test
    @DisplayName("없는 회원 예약 시도시 실패하는지 확인")
    void saveFailWhenMemberNotFound() {
        reservationTimeRepository.save(DEFAULT_TIME);
        themeRepository.save(DEFAULT_THEME);

        Assertions.assertThatThrownBy(() -> reservationService.save(ReservationFixture.DEFAULT_REQUEST))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(ExceptionType.NOT_FOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("중복된 예약 시도시 실패하는지 확인")
    void saveFailWhenDuplicateReservation() {
        initServiceWithMember();
        reservationTimeRepository.save(DEFAULT_TIME);
        themeRepository.save(DEFAULT_THEME);

        reservationService.save(ReservationFixture.DEFAULT_REQUEST);

        Assertions.assertThatThrownBy(() -> reservationService.save(ReservationFixture.DEFAULT_REQUEST))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(ExceptionType.DUPLICATE_RESERVATION.getMessage());
    }

    private void initServiceWithMember() {
        memberRepository = new CollectionMemberRepository(List.of(DEFAULT_MEMBER, DEFAULT_ADMIN));
        waitingRepository = new CollectionReservationWaitingRepository();
        reservationRepository = new CollectionReservationRepository();
        ReservationFinder reservationFinder = new ReservationFinder(reservationRepository, reservationTimeRepository,
                memberRepository, themeRepository);
        reservationService = new ReservationService(reservationRepository, waitingRepository, reservationFinder,
                memberRepository, paymentRepository);
    }

    @Test
    @DisplayName("이미 지나간 시간에 예약 시도시 실패하는지 확인")
    void saveFailWhenPastTime() {
        initServiceWithMember();
        reservationTimeRepository.save(DEFAULT_TIME);
        themeRepository.save(DEFAULT_THEME);
        ReservationRequest reservationRequestWithPastDate = new ReservationRequest(LocalDate.now().minusDays(1),
                DEFAULT_MEMBER.getId(), DEFAULT_TIME.getId(), DEFAULT_THEME.getId(), null);

        Assertions.assertThatThrownBy(() -> reservationService.save(reservationRequestWithPastDate))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(ExceptionType.PAST_TIME_RESERVATION.getMessage());
    }

    @Test
    @DisplayName("자신의 예약이 아니고 관리자가 아닌 경우 지울 수 없는지 확인")
    void cancelFailWhenPermission() {
        initServiceWithMember();
        reservationTimeRepository.save(DEFAULT_TIME);
        themeRepository.save(DEFAULT_THEME);

        ReservationRequest reservationRequestFromAdmin = new ReservationRequest(
                ReservationFixture.DEFAULT_REQUEST.date(),
                DEFAULT_ADMIN.getId(), DEFAULT_TIME.getId(), DEFAULT_THEME.getId(), null);
        ReservationResponse saved = reservationService.save(reservationRequestFromAdmin);

        Assertions.assertThatThrownBy(() -> reservationService.cancel(DEFAULT_MEMBER.getId(), saved.id()))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(ExceptionType.PERMISSION_DENIED.getMessage());
    }

    @Test
    @DisplayName("관리자는 다른 사람의 예약도 삭제할 수 있는지 확인")
    void cancelSuccessWhenAdmin() {
        initServiceWithMember();
        reservationTimeRepository.save(DEFAULT_TIME);
        themeRepository.save(DEFAULT_THEME);

        ReservationResponse saved = reservationService.save(ReservationFixture.DEFAULT_REQUEST);

        assertDoesNotThrow(() -> reservationService.cancel(DEFAULT_ADMIN.getId(), saved.id()));
    }

    @Test
    @DisplayName("예약이 취소된 경우 우선순위가 가장 높은 예약 대기가 예약이 되는지 확인")
    void changeReservationWhenDeletedReservationHasWaiting() {
        initServiceWithMember();
        reservationTimeRepository.save(DEFAULT_TIME);
        themeRepository.save(DEFAULT_THEME);

        ReservationResponse saved = reservationService.save(ReservationFixture.DEFAULT_REQUEST);
        waitingRepository.save(ReservationWaitingFixture.DEFAULT_WAITING);

        reservationService.cancel(DEFAULT_ADMIN.getId(), saved.id());

        boolean reservationMemberIsAdmin = reservationRepository.findById(saved.id())
                .orElseThrow()
                .getReservationMember()
                .equals(DEFAULT_ADMIN);
        Assertions.assertThat(reservationMemberIsAdmin)
                .isTrue();
    }

    @Test
    @DisplayName("MemberId로 예약을 찾을 때 결제 정보도 잘 가져오는지 확인")
    void findByMemberIdWithPayment() {
        initServiceWithMember();
        reservationTimeRepository.save(DEFAULT_TIME);
        themeRepository.save(DEFAULT_THEME);

        Reservation saved = reservationRepository.save(ReservationFixture.DEFAULT_RESERVATION);
        paymentRepository.save(PaymentFixture.DEFAULT_PAYMENT_WITHOUT_ID);

        String savedPaymentKey = reservationService.findByMemberId(saved.getId())
                .get(0).paymentKey();
        Assertions.assertThat(savedPaymentKey)
                .isEqualTo(PaymentFixture.DEFAULT_PAYMENT_WITHOUT_ID.getPaymentKey());
    }

    @Test
    @DisplayName("결제 정보가 있는 예약을 저장하면 결제 정보의 예약 필드를 업데이트하는지 확인")
    void saveWithUpdatingPayment() {
        initServiceWithMember();
        reservationTimeRepository.save(DEFAULT_TIME);
        themeRepository.save(DEFAULT_THEME);
        var payment = paymentRepository.save(PaymentFixture.DEFAULT_PAYMENT_WITHOUT_RESERVATION);
        var reservation = reservationService.save(ReservationFixture.DEFAULT_REQUEST);

        Assertions.assertThat(paymentRepository.findByReservationId(reservation.id()))
                .contains(payment);
    }
}
