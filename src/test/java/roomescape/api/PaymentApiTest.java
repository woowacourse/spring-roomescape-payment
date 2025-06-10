package roomescape.api;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.auth.AuthToken;
import roomescape.auth.jwt.JwtUtil;
import roomescape.business.model.entity.Member;
import roomescape.business.model.entity.Payment;
import roomescape.business.model.entity.Reservation;
import roomescape.business.model.entity.Theme;
import roomescape.business.model.entity.TimeSlot;
import roomescape.business.model.vo.PaymentStatus;
import roomescape.business.model.vo.ReservationStatus;
import roomescape.infrastructure.MemberRepository;
import roomescape.infrastructure.PaymentRepository;
import roomescape.infrastructure.ReservationRepository;
import roomescape.infrastructure.ReservationTimeRepository;
import roomescape.infrastructure.ThemeRepository;
import roomescape.infrastructure.payment.PaymentClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PaymentApiTest {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReservationTimeRepository reservationTimeRepository;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @MockitoBean
    PaymentClient paymentClient;

    @Test
    void 결제를_생성한다() {
        // given
        TimeSlot timeSlot = reservationTimeRepository.save(TimeSlot.create(LocalTime.of(11, 0)));
        Theme theme = themeRepository.save(Theme.create("theme1", "description1", "thumbnail1"));
        paymentRepository.save(Payment.restore("id", "paymentKey", 1000L, PaymentStatus.IN_PROGRESS, null));
        Member member = Member.create("name", "email1@domain.com", "password1");
        memberRepository.save(member);
        LocalDate date = LocalDate.now().plusDays(1);
        AuthToken token = jwtUtil.createToken(member);
        Map<String, Object> body = Map.of(
                "date", date.toString(),
                "timeId", timeSlot.getId().id(),
                "themeId", theme.getId().id()
        );
        // when
        ValidatableResponse response = RestAssured.given().log().all()
                .cookie("authToken", token.value())
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/payments")
                .then().log().all();
        // then
        response.statusCode(201);
    }

    @Test
    void 중복_예약이면_400에러가_발생한다() {
        // given
        TimeSlot timeSlot = reservationTimeRepository.save(TimeSlot.create(LocalTime.of(11, 0)));
        Theme theme = themeRepository.save(Theme.create("theme1", "description1", "thumbnail1"));
        paymentRepository.save(Payment.restore("id", "paymentKey", 1000L, PaymentStatus.IN_PROGRESS, null));
        Member member = Member.create("name", "email1@domain.com", "password1");
        memberRepository.save(member);
        LocalDate date = LocalDate.now().plusDays(1);
        reservationRepository.save(Reservation.create(member, date, timeSlot, theme));
        AuthToken token = jwtUtil.createToken(member);
        Map<String, Object> body = Map.of(
                "date", date.toString(),
                "timeId", timeSlot.getId().id(),
                "themeId", theme.getId().id()
        );
        // when
        ValidatableResponse response = RestAssured.given().log().all()
                .cookie("authToken", token.value())
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/payments")
                .then().log().all();
        // then
        response.statusCode(400);
    }

    @Test
    void PG사에_결제_승인을_요청하고_예약을_확정한다() {
        // given
        TimeSlot time = reservationTimeRepository.save(TimeSlot.create(LocalTime.of(11, 0)));
        Theme theme = themeRepository.save(Theme.create("theme1", "description1", "thumbnail1"));
        Member member = memberRepository.save(Member.create("name", "email1@domain.com", "password1"));
        Reservation reservation = reservationRepository.save(
                Reservation.create(member, LocalDate.now().plusDays(1), time, theme));
        paymentRepository.save(Payment.restore("id", "paymentKey", 1000L, PaymentStatus.IN_PROGRESS, reservation));
        AuthToken token = jwtUtil.createToken(member);

        Map<String, Object> body = Map.of(
                "paymentKey", "paymentKey1",
                "amount", 1000
        );
        // when
        ValidatableResponse response = RestAssured.given().log().all()
                .cookie("authToken", token.value())
                .contentType(ContentType.JSON)
                .body(body)
                .when().patch("/payments/id")
                .then().log().all();
        // then
        response.statusCode(204);
        Reservation findReservation = reservationRepository.findById(reservation.getId()).get();
        assertThat(findReservation.getStatus()).isEqualTo(ReservationStatus.DONE);
    }
}
