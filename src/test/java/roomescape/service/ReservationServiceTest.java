package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.Fixture.VALID_MEMBER;
import static roomescape.Fixture.VALID_RESERVATION_TIME;
import static roomescape.Fixture.VALID_THEME;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import roomescape.controller.payment.TestPaymentConfiguration;
import roomescape.domain.Member;
import roomescape.domain.ReservationDate;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.domain.repository.MemberRepository;
import roomescape.domain.repository.ReservationTimeRepository;
import roomescape.domain.repository.ThemeRepository;
import roomescape.exception.RoomescapeErrorCode;
import roomescape.exception.RoomescapeException;
import roomescape.service.request.ReservationSaveAppRequest;
import roomescape.service.response.PaymentAppResponse;
import roomescape.service.response.ReservationAppResponse;
import roomescape.service.response.ReservationTimeAppResponse;
import roomescape.service.response.ThemeAppResponse;
import roomescape.web.controller.request.MemberReservationRequest;

@Sql(scripts = "/truncate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(classes = TestPaymentConfiguration.class)
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @DisplayName("예약과 결제 정보를 저장하고 응답을 반환한다.")
    @Test
    void save() {
        LocalDate reservationDate = LocalDate.now().plusDays(1);
        ReservationTime reservationTime = reservationTimeRepository.save(VALID_RESERVATION_TIME);
        Theme theme = themeRepository.save(VALID_THEME);
        Member member = memberRepository.save(VALID_MEMBER);

        ReservationAppResponse reservationAppResponse = reservationService.save(
                ReservationSaveAppRequest.of(
                        new MemberReservationRequest(
                                LocalDate.now().plusDays(1).toString(),
                                reservationTime.getId(),
                                theme.getId(),
                                "paymentKey",
                                "orderId",
                                BigDecimal.valueOf(1000)
                        ), member.getId()
                )
        );

        ReservationDate date = reservationAppResponse.date();
        ReservationTimeAppResponse reservationTimeAppResponse = reservationAppResponse.reservationTimeAppResponse();
        ThemeAppResponse themeAppResponse = reservationAppResponse.themeAppResponse();
        PaymentAppResponse paymentAppResponse = reservationAppResponse.paymentAppResponse();

        assertAll(
                () -> assertThat(reservationAppResponse.id()).isEqualTo(1L),
                () -> assertThat(date.getDate()).isEqualTo(reservationDate),
                () -> assertThat(reservationAppResponse.name()).isEqualTo(member.getName()),
                () -> assertThat(reservationTimeAppResponse.id()).isEqualTo(reservationTime.getId()),
                () -> assertThat(themeAppResponse.id()).isEqualTo(theme.getId()),
                () -> assertThat(paymentAppResponse.paymentKey()).isEqualTo("paymentKey"),
                () -> assertThat(paymentAppResponse.orderId()).isEqualTo("orderId"),
                () -> assertThat(paymentAppResponse.amount()).isEqualTo(BigDecimal.valueOf(1000))
        );
    }

    @DisplayName("실패: 존재하지 않는 시간,테마,사용자 ID 입력 시 예외가 발생한다.")
    @Test
    void save_TimeIdDoesntExist() {
        assertThatCode(() -> reservationService.save(ReservationSaveAppRequest.of(
                new MemberReservationRequest(LocalDate.now().plusDays(1).toString(),
                        1L,
                        1L,
                        "paymentKey",
                        "orderId",
                        BigDecimal.valueOf(1000)), 1L)))
                .isInstanceOf(RoomescapeException.class);
    }

    @DisplayName("실패: 중복 예약을 생성하면 예외가 발생한다.")
    @Test
    void save_Duplication() {
        LocalDate reservationDate = LocalDate.now().plusDays(1);
        ReservationTime reservationTime = reservationTimeRepository.save(VALID_RESERVATION_TIME);
        Theme theme = themeRepository.save(VALID_THEME);
        Member member = memberRepository.save(VALID_MEMBER);

        ReservationAppResponse reservationAppResponse = reservationService.save(
                ReservationSaveAppRequest.of(
                        new MemberReservationRequest(
                                reservationDate.toString(),
                                reservationTime.getId(),
                                theme.getId(),
                                "paymentKey",
                                "orderId",
                                BigDecimal.valueOf(1000)
                        ), member.getId()
                )
        );

        assertThatCode(() -> reservationService.save(ReservationSaveAppRequest.of(
                new MemberReservationRequest(
                        LocalDate.now().plusDays(1).toString(),
                        reservationTime.getId(),
                        theme.getId(),
                        "paymentKey",
                        "orderId",
                        BigDecimal.valueOf(1000)), member.getId()
        )))
                .isInstanceOf(RoomescapeException.class)
                .extracting("errorCode")
                .isEqualTo(RoomescapeErrorCode.DUPLICATED_RESERVATION);
    }

    @DisplayName("실패: 어제 날짜에 대한 예약을 생성하면 예외가 발생한다.")
    @Test
    void save_PastDateReservation() {
        LocalDate reservationDate = LocalDate.now().minusDays(1);
        ReservationTime reservationTime = reservationTimeRepository.save(VALID_RESERVATION_TIME);
        Theme theme = themeRepository.save(VALID_THEME);
        Member member = memberRepository.save(VALID_MEMBER);

        assertThatCode(() -> reservationService.save(ReservationSaveAppRequest.of(
                new MemberReservationRequest(
                        reservationDate.toString(),
                        reservationTime.getId(),
                        theme.getId(),
                        "paymentKey",
                        "orderId",
                        BigDecimal.valueOf(1000)), member.getId()
        )))
                .isInstanceOf(RoomescapeException.class)
                .extracting("errorCode")
                .isEqualTo(RoomescapeErrorCode.PAST_REQUEST);
    }

    @DisplayName("실패: 같은 날짜에 대한 과거 시간 예약을 생성하면 예외가 발생한다.")
    @Test
    void save_TodayPastTimeReservation() {
        LocalDate reservationDate = LocalDate.now();
        ReservationTime reservationTime = reservationTimeRepository.save(VALID_RESERVATION_TIME);
        Theme theme = themeRepository.save(VALID_THEME);
        Member member = memberRepository.save(VALID_MEMBER);

        assertThatCode(() -> reservationService.save(ReservationSaveAppRequest.of(
                new MemberReservationRequest(
                        reservationDate.toString(),
                        reservationTime.getId(),
                        theme.getId(),
                        "paymentKey",
                        "orderId",
                        BigDecimal.valueOf(1000)), member.getId()
        )))
                .isInstanceOf(RoomescapeException.class)
                .extracting("errorCode")
                .isEqualTo(RoomescapeErrorCode.PAST_REQUEST);
    }
}
