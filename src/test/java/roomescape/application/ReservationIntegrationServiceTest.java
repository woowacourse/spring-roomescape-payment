package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import roomescape.domain.reservation.reserved.Reserved;
import roomescape.domain.reservation.reserved.ReservedRepository;
import roomescape.domain.reservation.reserved.ReservedSearchFilter;
import roomescape.exception.AlreadyExistedException;
import roomescape.exception.BusinessRuleViolationException;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationIntegrationServiceTest {

    @Autowired
    private ReservedService service;

    @Autowired
    private ReservedRepository reservedRepository;

    @Test
    @DisplayName("예약을 추가할 수 있다.")
    void saveReservation() {
        // given
        var tomorrow = LocalDate.now().plusDays(1);

        // when
        Reserved reserved = service.saveReservedWithoutPurchase(2L, tomorrow, 2L, 2L);

        // then
        var reservations = reservedRepository.findAll();
        assertThat(reservations).contains(reserved);
    }

    @Test
    @DisplayName("미래의 날짜와 시간에 대한 예약 생성은 가능하다.")
    void saveReservation_WhenReserveFuture() {
        // given
        var tomorrow = LocalDate.now().plusDays(1);

        // when & then
        assertThatCode(() -> service.saveReservedWithoutPurchase(2L, tomorrow, 2L, 2L)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("과거의 날짜와 시간에 대한 예약 생성 시 예외를 던진다.")
    void saveReservation_WhenReservePast() {
        // given
        var yesterday = LocalDate.now().minusDays(1);

        // when & then
        assertThatThrownBy(() -> service.saveReservedWithoutPurchase(2L, yesterday, 2L, 2L)).isInstanceOf(
                BusinessRuleViolationException.class).hasMessage("이전 날짜로 예약할 수 없습니다.");
    }

    @Test
    @DisplayName("이미 예약된 날짜와 시간에 대한 예약 생성 시 예외를 던진다.")
    void saveReservation_WhenReservationAlreadyExists() {
        // given
        var reservedUserId = 2L;
        var reservedDate = LocalDate.now().plusDays(2);
        var reservedThemeId = 1L;
        var reservedTimeSlotId = 1L;

        // when & then
        assertThatThrownBy(() -> service.saveReservedWithoutPurchase(reservedUserId, reservedDate, reservedTimeSlotId,
                reservedThemeId)).isInstanceOf(AlreadyExistedException.class)
                .hasMessage("이미 해당 날짜, 시간, 테마에 대한 예약이 존재합니다.");
    }

    @Test
    @DisplayName("검색 필터로 예약을 조회할 수 있다.")
    void findReservationsByFilter() {
        // given
        var filter = new ReservedSearchFilter(1L, 2L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));

        // when & then
        assertThat(service.findReservedByFilter(filter)).hasSize(2);
    }

    @Test
    @DisplayName("예약을 삭제할 수 있다.")
    void removeById() {
        // when
        service.removeById(1L);

        // then
        var reservations = reservedRepository.findAll();
        assertThat(reservations.getFirst().getId()).isEqualTo(2L);
    }
}
