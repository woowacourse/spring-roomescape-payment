package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.BasicAcceptanceTest;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.dto.LoginMember;
import roomescape.dto.request.reservation.AdminReservationRequest;
import roomescape.dto.request.reservation.ReservationCriteriaRequest;
import roomescape.dto.request.reservation.ReservationRequest;
import roomescape.dto.request.reservation.WaitingRequest;
import roomescape.dto.response.reservation.ReservationResponse;
import roomescape.exception.RoomescapeException;

class ReservationServiceTest extends BasicAcceptanceTest {
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationWaitingService reservationWaitingService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void SetUp() {
        jdbcTemplate.update(
                "INSERT INTO member (name, email, password, role) VALUES ('회원', 'member@wooteco.com', 'wootecoCrew6!', 'BASIC')");
        jdbcTemplate.update(
                "INSERT INTO member (name, email, password, role) VALUES ('운영자', 'admin@wooteco.com', 'wootecoCrew6!', 'ADMIN')");
        jdbcTemplate.update("INSERT INTO reservation_time (start_at) VALUES ('10:00')");
        jdbcTemplate.update("INSERT INTO theme (name, description, thumbnail, price) VALUES ('name1', 'description1', 'thumbnail1', 1000)");
    }

    @DisplayName("사용자 요청으로 들어온 에약이 예외 조건에 해당되지 않을 때 해당 예약을 저장한다.")
    @Test
    void saveByClient() {
        LocalDate tomorrow = LocalDate.now().plusDays(1L);
        LoginMember loginMember = new LoginMember(1L, "회원");
        ReservationRequest reservationRequest = new ReservationRequest(tomorrow, 1L, 1L, "paymentKey", "orderId", BigDecimal.valueOf(1000));
        reservationService.saveReservationByClient(loginMember, reservationRequest);

        assertThat(reservationService.findAll()).hasSize(1);
    }

    @DisplayName("관리자 요청으로 들어온 에약이 예외 조건에 해당되지 않을 때 해당 예약을 저장한다.")
    @Test
    void saveByAdmin() {
        LocalDate tomorrow = LocalDate.now().plusDays(1L);
        AdminReservationRequest adminReservationRequest = new AdminReservationRequest(1L, tomorrow, 1L, 1L);
        reservationService.saveReservationByAdmin(adminReservationRequest);

        assertThat(reservationService.findAll()).hasSize(1);
    }

    @DisplayName("해당 id의 상태를 `CANCELED`로 변경한다.")
    @Test
    void cancelById() {
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, time_id, theme_id, status) VALUES (CURRENT_DATE + INTERVAL '1' DAY , 1, 1, 1, 'RESERVATION')");
        reservationService.cancelById(1L);

        assertThat(reservationRepository.findById(1L).get().getStatus()).isSameAs(ReservationStatus.CANCELED);
    }

    @DisplayName("예약 삭제 요청시 예약이 존재하지 않으면 예외를 발생시킨다.")
    @Test
    void invalidNotExistReservation() {
        assertThatThrownBy(() -> reservationService.cancelById(99L))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(String.format("존재하지 않는 예약입니다. 요청 예약 id:%d", 99));
    }

    @DisplayName("조건을 만족하는 예약을 반환한다.")
    @Test
    void findByCriteria() {
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, time_id, theme_id, status) VALUES (CURRENT_DATE + INTERVAL '-1' DAY , 1, 1, 1, 'RESERVATION')");
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, time_id, theme_id, status) VALUES (CURRENT_DATE , 1, 1, 1, 'RESERVATION')");
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, time_id, theme_id, status) VALUES (CURRENT_DATE + INTERVAL '1' DAY , 1, 1, 1, 'RESERVATION')");
        List<ReservationResponse> reservationResponses = reservationService.findByCriteria(
                new ReservationCriteriaRequest(1L, 1L, LocalDate.now(), LocalDate.now().plusDays(1))
        );

        assertThat(reservationResponses).hasSize(2);
    }

    @DisplayName("해당 멤버의 예약 목록을 반환한다.")
    @Test
    void findMyReservations() {
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, time_id, theme_id, status) VALUES (CURRENT_DATE + INTERVAL '-1' DAY , 2, 1, 1, 'RESERVATION')");
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, time_id, theme_id, status) VALUES (CURRENT_DATE , 1, 1, 1, 'RESERVATION')");

        assertThat(reservationService.findMyReservations(1L)).hasSize(1);
    }

    @TestFactory
    @DisplayName("예약을 삭제할 시 1번째 예약 대기를 삭제한다.")
    Stream<DynamicTest> updateWaitingToReservation() {
        AtomicReference<ReservationResponse> reservationResponse = new AtomicReference<>();
        return Stream.of(
                dynamicTest("예약 대기를 조회한다. (총 0개)", () -> assertThat(reservationWaitingService.findAll()).isEmpty()),
                dynamicTest("예약을 추가한다.", () -> reservationResponse.set(reservationService.saveReservationByAdmin(new AdminReservationRequest(1L, LocalDate.now().plusDays(1L), 1L, 1L)))),
                dynamicTest("예약 대기를 추가한다.", () -> reservationWaitingService.saveReservationWaiting(new WaitingRequest(LocalDate.now().plusDays(1), 1L, 1L), (new LoginMember(2L, "운영자")))),
                dynamicTest("예약을 삭제한다.", () -> reservationService.cancelById(reservationResponse.get().id())),
                dynamicTest("예약 대기가 삭제된다.", () -> assertThat(reservationWaitingService.findAll()).isEmpty()));
    }
}
