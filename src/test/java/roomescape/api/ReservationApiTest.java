package roomescape.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import java.awt.image.DataBufferByte;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.auth.Role;
import roomescape.domain.*;
import roomescape.domain.repository.MemberRepository;
import roomescape.domain.repository.ReservationRepository;
import roomescape.domain.repository.ReservationTimeRepository;
import roomescape.domain.repository.ThemeRepository;
import roomescape.domain.repository.WaitingRepository;
import roomescape.infrastructure.JwtTokenProvider;
import roomescape.presentation.PaymentClientController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReservationApiTest {

    @LocalServerPort
    int port;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationTimeRepository timeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private WaitingRepository waitingRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @MockitoBean
    private PaymentClientController paymentClientController;

    @BeforeEach
    void setUpRestAssuredPort() {
        RestAssured.port = port;
    }

    @Test
    void 사용자가_예약을_추가한다() {
        Member savedMember = memberRepository.save(
                new Member(null, "name1", "email1@domain.com", "password1", Role.MEMBER)
        );
        String token = tokenProvider.createToken(savedMember.getId().toString(), savedMember.getRole());
        ReservationTime time = timeRepository.save(ReservationTime.createWithoutId(LocalTime.of(9, 0)));
        Theme theme = themeRepository.save(Theme.createWithoutId("theme1", "desc", "thumb1"));
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", "2026-08-05");
        reservation.put("timeId", time.getId());
        reservation.put("themeId", theme.getId());
        reservation.put("paymentKey", "1");
        reservation.put("orderId", "1");
        reservation.put("amount", 1000);

        PaymentInfo paymentInfo = new PaymentInfo("1", 1000);
        BDDMockito.given(paymentClientController.postPaymentInfo(any())).willReturn(paymentInfo);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(reservation)
                .cookie("token", token)
                .when().post("/api/reservations")
                .then().log().all()
                .statusCode(201)
                .body("date", equalTo("2026-08-05"))
                .body("memberName", equalTo("name1"));
    }

    @Test
    void 예약을_삭제한다() {
        // given
        Member member = memberRepository.save(
                new Member(null, "name1", "email1@domain.com", "password1", Role.MEMBER)
        );
        ReservationTime time = timeRepository.save(ReservationTime.createWithoutId(LocalTime.of(9, 0)));
        Theme theme = themeRepository.save(Theme.createWithoutId("theme1", "desc", "thumb1"));
        Reservation reservation = reservationRepository.save(
                Reservation.createWithoutId(member, LocalDate.of(2025, 1, 1), time, theme));
        // when & then
        RestAssured.given().log().all()
                .when().delete("/api/reservations/{reservationId}", reservation.getId())
                .then().log().all()
                .statusCode(204);

        assertThat(reservationRepository.findById(reservation.getId())).isEmpty();
    }

    @Test
    void 예약을_삭제했을_때_1번_예약대기가_예약이_된다() {
        // given
        Member member = memberRepository.save(
                new Member(null, "name1", "email1@domain.com", "password1", Role.MEMBER)
        );
        Member ohterMember = memberRepository.save(
                new Member(null, "name2", "email2@domain.com", "password2", Role.MEMBER)
        );

        ReservationTime time = timeRepository.save(ReservationTime.createWithoutId(LocalTime.of(9, 0)));
        Theme theme = themeRepository.save(Theme.createWithoutId("theme1", "desc", "thumb1"));

        Reservation reservation = reservationRepository.save(
                Reservation.createWithoutId(member, LocalDate.of(2025, 1, 1), time, theme));
        waitingRepository.save(
                Waiting.createWithoutId(ohterMember, LocalDate.of(2025, 1, 1), time, theme));

        // when & then
        RestAssured.given().log().all()
                .when().delete("/api/reservations/{reservationId}", reservation.getId())
                .then().log().all()
                .statusCode(204);

        assertThat(reservationRepository.findAll()).hasSize(1);
    }
}
