package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import roomescape.controller.request.AdminReservationRequest;
import roomescape.controller.request.ReservationRequest;
import roomescape.exception.BadRequestException;
import roomescape.exception.DuplicatedException;
import roomescape.exception.NotFoundException;
import roomescape.model.Member;
import roomescape.model.Reservation;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationRepository;

import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.*;
import static roomescape.model.Role.MEMBER;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql(scripts = {"/initialize_table.sql", "/test_data.sql"})
class ReservationServiceTest {

    private ReservationRepository reservationRepository;
    private MemberRepository memberRepository;
    private ReservationService reservationService;

    @Autowired
    public ReservationServiceTest(ReservationRepository reservationRepository, MemberRepository memberRepository,
                                  ReservationService reservationService) {
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.reservationService = reservationService;
    }

    @DisplayName("모든 예약 시간을 반환한다")
    @Test
    void should_return_all_reservation_times() {
        List<Reservation> reservations = reservationService.findAllReservations();

        assertThat(reservations).hasSize(2);
    }

    @DisplayName("검색 조건에 맞는 예약을 반환한다.")
    @Test
    void should_return_filtered_reservation() {
        List<Reservation> reservations = reservationService
                .filterReservation(1L, 1L, now().minusDays(1), now().plusDays(3));

        assertThat(reservations).hasSize(1);
    }

    @DisplayName("사용자가 예약 시간을 추가한다")
    @Test
    void should_add_reservation_times_when_give_member_request() {
        Member member = memberRepository.findById(1L).get();

        ReservationRequest request = new ReservationRequest(now().plusDays(2), 1L, 1L, "orderId", "paymentKey", 1234L);

        reservationService.addReservation(request, member);

        List<Reservation> allReservations = reservationRepository.findAll();
        assertThat(allReservations).hasSize(3);
    }

    @DisplayName("관리자가 예약 시간을 추가한다")
    @Test
    void should_add_reservation_times_when_give_admin_request() {
        AdminReservationRequest request =
                new AdminReservationRequest(now().plusDays(2), 1L, 1L, 1L);
        reservationService.addReservation(request);

        List<Reservation> allReservations = reservationRepository.findAll();
        assertThat(allReservations).hasSize(3);
    }

    @DisplayName("관리자가 예약 시간을 추가할 때 사용자가 없으면 예외가 발생한다.")
    @Test
    void should_throw_exception_when_not_exist_member() {
        AdminReservationRequest request =
                new AdminReservationRequest(now().plusDays(2), 1L, 1L, 99L);

        assertThatThrownBy(() -> reservationService.addReservation(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("[ERROR] 아이디가 99인 사용자가 존재하지 않습니다.");
    }

    @DisplayName("예약 시간을 삭제한다")
    @Test
    void should_remove_reservation_times() {
        reservationService.deleteReservation(1L);

        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).hasSize(1);
    }

    @DisplayName("존재하지 않는 예약을 삭제하면 예외가 발생한다.")
    @Test
    void should_throw_exception_when_not_exist_reservation() {
        assertThatThrownBy(() -> reservationService.deleteReservation(1000000))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("[ERROR] 해당 id:[1000000] 값으로 예약된 내역이 존재하지 않습니다.");
    }

    @DisplayName("존재하는 예약을 삭제하면 예외가 발생하지 않는다.")
    @Test
    void should_not_throw_exception_when_exist_reservation() {
        assertThatCode(() -> reservationService.deleteReservation(1))
                .doesNotThrowAnyException();
    }

    @DisplayName("현재 이전으로 예약하면 예외가 발생한다.")
    @Test
    void should_throw_exception_when_previous_date() {
        ReservationRequest request =
                new ReservationRequest(LocalDate.now().minusDays(1), 1L, 1L, "orderId", "paymentKey", 1234L);
        Member member = new Member("수달", MEMBER, "otter@email.com", "1111");

        assertThatThrownBy(() -> reservationService.addReservation(request, member))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("[ERROR] 현재(", ") 이전 시간으로 예약할 수 없습니다.");
    }

    @DisplayName("현재 이후로 예약하면 예외가 발생하지 않는다.")
    @Test
    void should_not_throw_exception_when_later_date() {
        Member member = memberRepository.findById(1L).get();

        ReservationRequest request =
                new ReservationRequest(LocalDate.now().plusDays(2), 1L, 1L, "orderId", "paymentKey", 1234L);

        assertThatCode(() -> reservationService.addReservation(request, member))
                .doesNotThrowAnyException();
    }

    @DisplayName("날짜, 시간, 테마가 일치하는 예약을 추가하려 할 때 예외가 발생한다.")
    @Test
    void should_throw_exception_when_add_exist_reservation() {
        LocalDate date = now().plusDays(1);
        Member member = memberRepository.findById(1L).get();

        ReservationRequest request = new ReservationRequest(date, 1L, 1L, "orderId", "paymentKey", 1234L);
        assertThatThrownBy(() -> reservationService.addReservation(request, member))
                .isInstanceOf(DuplicatedException.class)
                .hasMessage("[ERROR] 이미 해당 시간에 예약이 존재합니다.");
    }

    @DisplayName("사용자가 예약한 예약을 반환한다.")
    @Test
    void should_return_member_reservations() {
        Member member = memberRepository.findById(1L).get();

        List<Reservation> reservations = reservationService
                .findMemberReservations(member.getId());

        assertThat(reservations).hasSize(2);
    }

    @DisplayName("주어진 아이디에 맞는 예약을 반환한다.")
    @Test
    void should_return_reservation_when_given_id() {
        Reservation reservation = reservationService.findById(1L);

        assertThat(reservation.getId()).isEqualTo(1);
    }
}
