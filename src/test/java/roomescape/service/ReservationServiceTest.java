package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import roomescape.member.dto.MemberRegisterRequest;
import roomescape.member.service.MemberService;
import roomescape.payment.PaymentClient;
import roomescape.reservation.dto.ReservationPaymentRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationService;
import roomescape.theme.dto.ReservationThemeRequest;
import roomescape.theme.service.ReservationThemeService;
import roomescape.time.dto.ReservationTimeRequest;
import roomescape.time.service.ReservationTimeService;

@SpringBootTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@ActiveProfiles("test")
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ReservationTimeService reservationTimeService;

    @Autowired
    private ReservationThemeService reservationThemeService;

    @Autowired
    private PaymentClient paymentClient;

    @Test
    @DisplayName("사용자의 id를 이용해 예약을 생성한다")
    void createReservationTest() {
        // given
        Long memberId = memberService.addMember(new MemberRegisterRequest("", "", "")).id();
        Long timeId = reservationTimeService.addReservationTime(new ReservationTimeRequest(LocalTime.now())).id();
        Long themeId = reservationThemeService.addReservationTheme(new ReservationThemeRequest("", "", "")).id();

        ReservationPaymentRequest reservationRequest = new ReservationPaymentRequest(
                LocalDate.now().plusDays(1),
                themeId,
                timeId,
                "test",
                "test",
                1000,
                "NORMAL"
        );

        // when
        ReservationResponse reservationResponse = reservationService.addReservation(memberId, reservationRequest);

        // then
        assertAll(
                () -> assertThat(reservationResponse.id()),
                () -> assertThat(reservationResponse.date()),
                () -> assertThat(reservationResponse.name())
        );
    }

    @Test
    @DisplayName("사용자가 없는 theme id를 이용해 예약을 생성한다")
    void createReservationTest2() {
        // given
        Long memberId = memberService.addMember(new MemberRegisterRequest("", "", "")).id();
        Long timeId = reservationTimeService.addReservationTime(new ReservationTimeRequest(LocalTime.now())).id();
        Long themeId = reservationThemeService.addReservationTheme(new ReservationThemeRequest("", "", "")).id();
        Long nonExistTimeId = 2L;
        Long nonExistThemeId = 2L;

        ReservationPaymentRequest reservationRequest1 = new ReservationPaymentRequest(
                LocalDate.now().plusDays(1),
                nonExistThemeId,
                timeId,
                "test",
                "test",
                1000,
                "NORMAL"
        );

        ReservationPaymentRequest reservationRequest2 = new ReservationPaymentRequest(
                LocalDate.now().plusDays(1),
                themeId,
                nonExistTimeId,
                "test",
                "test",
                1000,
                "NORMAL"
        );

        // when, then
        assertAll(
                () -> assertThatThrownBy(
                        () -> reservationService.addReservation(memberId, reservationRequest1)
                ).isInstanceOf(NoSuchElementException.class),
                () -> assertThatThrownBy(
                        () -> reservationService.addReservation(memberId, reservationRequest2)
                ).isInstanceOf(NoSuchElementException.class)
        );
    }

    @Test
    @DisplayName("미래가 아닌 날짜로 예약 시도 시 예외 발생")
    void createReservationTest3() {
        // given
        Long memberId = memberService.addMember(new MemberRegisterRequest("", "", "")).id();
        Long timeId = reservationTimeService.addReservationTime(new ReservationTimeRequest(LocalTime.now())).id();
        Long themeId = reservationThemeService.addReservationTheme(new ReservationThemeRequest("", "", "")).id();

        ReservationPaymentRequest reservationRequest1 = new ReservationPaymentRequest(
                LocalDate.now(),
                themeId,
                timeId,
                "test",
                "test",
                1000,
                "NORMAL"
        );

        ReservationPaymentRequest reservationRequest2 = new ReservationPaymentRequest(
                LocalDate.now().minusDays(1),
                themeId,
                timeId,
                "test",
                "test",
                1000,
                "NORMAL"
        );

        // when, then
        assertAll(
                () -> assertThatThrownBy(
                        () -> reservationService.addReservation(memberId,reservationRequest1)
                ).isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(
                        () -> reservationService.addReservation(memberId,reservationRequest2)
                ).isInstanceOf(IllegalArgumentException.class)
        );
    }


    @DisplayName("예약이 중복되어 예외가 발생 한다.")
    @Test
    void duplicateTest() {
        // given
        Long memberId = memberService.addMember(new MemberRegisterRequest("", "", "")).id();
        Long timeId = reservationTimeService.addReservationTime(new ReservationTimeRequest(LocalTime.now())).id();
        Long themeId = reservationThemeService.addReservationTheme(new ReservationThemeRequest("", "", "")).id();

        ReservationPaymentRequest reservationRequest = new ReservationPaymentRequest(
                LocalDate.now().plusDays(1),
                themeId,
                timeId,
                "test",
                "test",
                1000,
                "NORMAL"
        );

        reservationService.addReservation(memberId, reservationRequest);

        // when & then
        assertThatThrownBy(() -> reservationService.addReservation(memberId, reservationRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("모든 예약 정보를 가져온다.")
    void getAllReservationsTest() {
        // given
        Long memberId = memberService.addMember(new MemberRegisterRequest("", "", "")).id();
        Long timeId = reservationTimeService.addReservationTime(new ReservationTimeRequest(LocalTime.now())).id();
        Long themeId = reservationThemeService.addReservationTheme(new ReservationThemeRequest("", "", "")).id();

        ReservationPaymentRequest reservationRequest = new ReservationPaymentRequest(
                LocalDate.now().plusDays(1),
                themeId,
                timeId,
                "test",
                "test",
                1000,
                "NORMAL"
        );
        reservationService.addReservation(memberId, reservationRequest);
        //when
        final List<ReservationResponse> expected = reservationService.getAllReservations();

        //then
        assertThat(expected).hasSize(1);
    }
}
