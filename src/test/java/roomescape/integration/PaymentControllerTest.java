package roomescape.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static roomescape.domain.FakePayment.CORRECT_REQ;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import roomescape.Fixture;
import roomescape.TestPaymentConfig;
import roomescape.domain.Email;
import roomescape.domain.Member;
import roomescape.domain.Name;
import roomescape.domain.Password;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Role;
import roomescape.domain.Theme;
import roomescape.repository.MemberRepository;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.service.JwtGenerator;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(value = "/clear.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "/clear.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
@Import(TestPaymentConfig.class)
public class PaymentControllerTest {

    @Autowired
    private JwtGenerator JWT_GENERATOR;
    @LocalServerPort
    int port;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    private Theme defaultTheme1 = new Theme("theme1", "description", "thumbnail");
    private Theme defaultTheme2 = new Theme("theme2", "description", "thumbnail");

    private ReservationTime defaultTime = new ReservationTime(LocalTime.of(11, 30));
    private Member defaultMember = Fixture.defaultMember;
    private Member otherMember = new Member(
            new Name("otherName"),
            Role.USER,
            new Email("other@email.com"),
            new Password("otherPassword"));
    private String token;
    private String othersToken;
    private Payment notPayed;

    @BeforeEach
    void init() {
        RestAssured.port = port;
        defaultTheme1 = themeRepository.save(defaultTheme1);
        defaultTheme2 = themeRepository.save(defaultTheme2);
        defaultTime = reservationTimeRepository.save(defaultTime);
        defaultMember = memberRepository.save(defaultMember);
        otherMember = memberRepository.save(otherMember);
        token = generateTokenWith(defaultMember);
        othersToken = generateTokenWith(otherMember);
    }

    private String generateTokenWith(Member member) {
        return JWT_GENERATOR.generateWith(
                Map.of(
                        "id", member.getId(),
                        "name", member.getName().getValue(),
                        "role", member.getRole().getTokenValue()
                )
        );
    }

    @DisplayName("처음 예약을 생성하면 결제 대기 상태가 된다.")
    @Test
    void notPayedTest() {
        Map<String, Object> reservationParam = Map.of(
                "date", LocalDate.now().plusDays(1).toString(),
                "timeId", defaultTime.getId(),
                "themeId", defaultTheme1.getId());

        int savedId = RestAssured.given().log().all()
                .when()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(reservationParam)
                .post("/reservations")
                .then().log().all()
                .extract().jsonPath().get("id");

        Reservation savedReservation = reservationRepository.findById((long) savedId).get();
        assertThat(savedReservation.getPayment()).isEqualTo(Optional.empty());
    }

    @DisplayName("결제 대기 상태의 예약을 결제하면 결제 상태로 변경된다.")
    @Test
    void payTest() {
        Map<String, Object> reservationParam = Map.of(
                "date", LocalDate.now().plusDays(1).toString(),
                "timeId", defaultTime.getId(),
                "themeId", defaultTheme1.getId());

        int savedId = RestAssured.given().log().all()
                .when()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(reservationParam)
                .post("/reservations")
                .then().log().all()
                .extract().jsonPath().get("id");

        Map<String, Object> paymentParam = Map.of(
                "paymentKey", CORRECT_REQ.paymentKey(),
                "amount", CORRECT_REQ.amount(),
                "orderId", CORRECT_REQ.orderId()
        );

        RestAssured.given().log().all()
                .when()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(paymentParam)
                .post("/payment/" + savedId)
                .then().log().all();

        Reservation savedReservation = reservationRepository.findById((long) savedId).get();
        assertThat(savedReservation.getPayment().isPresent()).isTrue();
    }
}
