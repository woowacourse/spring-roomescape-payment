package roomescape.reservation.acceptance;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import fixture.MemberFixture;
import fixture.PaymentFixture;
import fixture.ReservationTimeFixture;
import fixture.ThemeFixture;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.helper.TestHelper;
import roomescape.member.entity.Member;
import roomescape.member.entity.RoleType;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.entity.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.dto.request.ReservationAdminCreateRequest;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.request.ReservationCreateRequest.PaymentDetail;
import roomescape.reservation.entity.ReservationTime;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.theme.entity.Theme;
import roomescape.theme.repository.ThemeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(RestDocumentationExtension.class) // JUnit 5용 REST Docs 확장 추가
class ReservationAcceptanceTest {

    @LocalServerPort
    private int port;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @MockitoBean
    private PaymentService paymentService;

    private final LocalDate tomorrow = LocalDate.now().plusDays(1);

    private ReservationTime reservationTime;

    private Member member;

    private Theme theme;

    private Payment payment;

    private RequestSpecification documentationSpec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;

        this.documentationSpec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation))
                .build();

        payment = PaymentFixture.createDefault();
        paymentRepository.save(payment);
        given(paymentService.confirmPayment(any(), any(), any()))
                .willReturn(payment);

        member = MemberFixture.create(RoleType.ADMIN);
        memberRepository.save(member);

        theme = ThemeFixture.createDefault();
        themeRepository.save(theme);

        reservationTime = ReservationTimeFixture.createDefault();
        reservationTimeRepository.save(reservationTime);
    }

    @Test
    @DisplayName("사용자 예약 생성 - 성공")
    void createReservation() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());
        var reservationRequest = new ReservationCreateRequest(
                tomorrow,
                reservationTime.getId(),
                theme.getId(),
                new PaymentDetail(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                )
        );

        // when & then
        RestAssured
                .given(this.documentationSpec)
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(reservationRequest)
                .filter(document("reservations-post-success",
                        requestFields( // 요청 필드 문서화
                                fieldWithPath("date").description("예약 날짜 (YYYY-MM-DD 형식)"),
                                fieldWithPath("timeId").description("예약 시간의 ID"),
                                fieldWithPath("themeId").description("테마의 ID"),
                                fieldWithPath("payment.paymentKey").description("결제 고유 키 (Toss Payments)"),
                                fieldWithPath("payment.orderId").description("주문 ID (Toss Payments)"),
                                fieldWithPath("payment.amount").description("결제 금액 (Toss Payments)"),
                                fieldWithPath("payment.paymentType").description("결제 타입 (Toss Payments, 예: NORMAL)")
                        ),
                        responseFields( // 응답 필드 문서화
                                fieldWithPath("id").description("생성된 예약의 ID"),
                                fieldWithPath("date").description("예약된 날짜"),
                                fieldWithPath("startAt").description("예약 시작 시간 (HH:mm 형식)"),
                                fieldWithPath("memberName").description("예약한 회원의 이름"),
                                fieldWithPath("themeName").description("예약된 테마의 이름")
                        )
                ))
                .when()
                .post("/reservations")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("date", equalTo(tomorrow.toString()))
                .body("startAt", equalTo(reservationTime.getStartAt().toString()))
                .body("themeName", equalTo(theme.getName()));
    }

    @Test
    @DisplayName("사용자 예약 생성 - 실패 - 로그인 정보 없음")
    void createReservationWithoutLogin() {
        // given
        var reservationRequest = new ReservationCreateRequest(
                tomorrow,
                reservationTime.getId(),
                theme.getId(),
                new PaymentDetail(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                )
        );

        // when & then
        RestAssured
                .given(this.documentationSpec)
                .contentType(ContentType.JSON)
                .body(reservationRequest)
                .filter(document("reservations-post-fail-without-login",
                        requestFields( // 요청 필드 문서화
                                fieldWithPath("date").description("예약 날짜 (YYYY-MM-DD 형식)"),
                                fieldWithPath("timeId").description("예약 시간의 ID"),
                                fieldWithPath("themeId").description("테마의 ID"),
                                fieldWithPath("payment.paymentKey").description("결제 고유 키 (Toss Payments)"),
                                fieldWithPath("payment.orderId").description("주문 ID (Toss Payments)"),
                                fieldWithPath("payment.amount").description("결제 금액 (Toss Payments)"),
                                fieldWithPath("payment.paymentType").description("결제 타입 (Toss Payments, 예: NORMAL)")
                        ),
                        responseFields( // 응답 필드 문서화
                                fieldWithPath("message").description("에러 메시지")
                        )
                ))
                .when()
                .post("/reservations")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("사용자 예약 생성 - 실패 - 중복 예약 생성 요청")
    void createDuplicateReservation() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());
        var reservationRequest = new ReservationCreateRequest(
                tomorrow,
                reservationTime.getId(),
                theme.getId(),
                new PaymentDetail(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                )
        );
        TestHelper.postWithToken("/reservations", reservationRequest, token);

        // when & then
        RestAssured
                .given(this.documentationSpec)
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(reservationRequest)
                .filter(document("reservations-post-fail-duplicate",
                        requestFields( // 요청 필드 문서화
                                fieldWithPath("date").description("예약 날짜 (YYYY-MM-DD 형식)"),
                                fieldWithPath("timeId").description("예약 시간의 ID"),
                                fieldWithPath("themeId").description("테마의 ID"),
                                fieldWithPath("payment.paymentKey").description("결제 고유 키 (Toss Payments)"),
                                fieldWithPath("payment.orderId").description("주문 ID (Toss Payments)"),
                                fieldWithPath("payment.amount").description("결제 금액 (Toss Payments)"),
                                fieldWithPath("payment.paymentType").description("결제 타입 (Toss Payments, 예: NORMAL)")
                        ),
                        responseFields( // 응답 필드 문서화
                                fieldWithPath("message").description("에러 메시지")
                        )
                ))
                .when()
                .post("/reservations")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("사용자 예약 조회 - 성공")
    void getReservationsByMember() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());
        var reservationRequest = new ReservationCreateRequest(
                tomorrow,
                reservationTime.getId(),
                theme.getId(),
                new PaymentDetail(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                )
        );
        TestHelper.postWithToken("/reservations", reservationRequest, token);

        // when & then
        RestAssured
                .given(this.documentationSpec)
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .filter(document("reservations-mine-get-success",
                        responseFields( // 응답 필드 문서화
                                fieldWithPath("[].id").description("예약 ID"),
                                fieldWithPath("[].theme").description("테마 이름"),
                                fieldWithPath("[].date").description("예약 날짜"),
                                fieldWithPath("[].time").description("예약 시작 시간 (HH:mm 형식)"),
                                fieldWithPath("[].status").description("예약 상태 (예: 예약, 2번째 예약대기 등)"),
                                fieldWithPath("[].payment.paymentKey").description("결제 고유 키 (Toss Payments)"),
                                fieldWithPath("[].payment.amount").description("결제 금액 (Toss Payments)")
                        )
                ))
                .when()
                .get("/reservations/mine")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(1))
                .body("[0].theme", equalTo(theme.getName()))
                .body("[0].date", equalTo(tomorrow.toString()))
                .body("[0].time", equalTo(reservationTime.getStartAt().toString()))
                .body("[0].status", equalTo("예약"));
    }

    @Test
    @DisplayName("관리자 모든 예약 조회 - 성공")
    void getAllReservations() {
        // given
        String adminToken = TestHelper.login(member.getEmail(), member.getPassword());
        var reservationRequest = new ReservationCreateRequest(
                tomorrow,
                reservationTime.getId(),
                theme.getId(),
                new PaymentDetail(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                )
        );
        TestHelper.postWithToken("/reservations", reservationRequest, adminToken);

        // when & then
        RestAssured
                .given(this.documentationSpec)
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .filter(document("admin-reservations-get-success",
                        responseFields( // 응답 필드 문서화
                                fieldWithPath("[].id").description("생성된 예약의 ID"),
                                fieldWithPath("[].date").description("예약된 날짜"),
                                fieldWithPath("[].startAt").description("예약 시작 시간 (HH:mm 형식)"),
                                fieldWithPath("[].memberName").description("예약한 회원의 이름"),
                                fieldWithPath("[].themeName").description("예약된 테마의 이름")
                        )
                ))
                .when()
                .get("/admin/reservations")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(1))
                .body("[0].date", equalTo(tomorrow.toString()))
                .body("[0].startAt", equalTo(reservationTime.getStartAt().toString()))
                .body("[0].memberName", equalTo(member.getName()))
                .body("[0].themeName", equalTo(theme.getName()));
    }

    @Test
    @DisplayName("관리자 예약 삭제 - 성공")
    void deleteReservation() {
        // given
        String adminToken = TestHelper.login(member.getEmail(), member.getPassword());
        var reservationRequest = new ReservationCreateRequest(
                tomorrow,
                reservationTime.getId(),
                theme.getId(),
                new PaymentDetail(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                )
        );
        TestHelper.postWithToken("/reservations", reservationRequest, adminToken);

        // when & then
        RestAssured
                .given(this.documentationSpec)
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .filter(document("admin-reservations-delete-success",
                        pathParameters(
                                parameterWithName("id").description("삭제할 예약 ID")
                        )
                ))
                .when()
                .delete("/admin/reservations/{id}", 1L)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // 예약 삭제 후 조회 검사
        TestHelper.getWithToken("/admin/reservations", adminToken)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(0));
    }

    @Test
    @DisplayName("관리자 예약 조회 - 성공 - 필터링 조회")
    void getFilteredReservations() {
        // given
        String adminToken = TestHelper.login(member.getEmail(), member.getPassword());
        var reservationRequest = new ReservationCreateRequest(
                tomorrow,
                reservationTime.getId(),
                theme.getId(),
                new PaymentDetail(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                )
        );
        TestHelper.postWithToken("/reservations", reservationRequest, adminToken);

        // when & then
        RestAssured
                .given(this.documentationSpec)
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .filter(document("admin-reservations-filtered-get-success",
                        queryParameters(
                                parameterWithName("themeId").description("테마 ID"),
                                parameterWithName("memberId").description("회원 ID"),
                                parameterWithName("dateFrom").description("조회 시작 날짜 (YYYY-MM-DD 형식)"),
                                parameterWithName("dateTo").description("조회 종료 날짜 (YYYY-MM-DD 형식)")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("예약 ID"),
                                fieldWithPath("[].date").description("예약 날짜"),
                                fieldWithPath("[].startAt").description("예약 시작 시간 (HH:mm 형식)"),
                                fieldWithPath("[].memberName").description("회원 이름"),
                                fieldWithPath("[].themeName").description("테마 이름")
                        )
                ))
                .when()
                .param("themeId", 1L)
                .param("memberId", 1L)
                .param("dateFrom", LocalDate.now().toString())
                .param("dateTo", LocalDate.now().plusWeeks(1).toString())
                .get("/admin/reservations/filtered")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(1))
                .body("[0].date", equalTo(tomorrow.toString()))
                .body("[0].startAt", equalTo(reservationTime.getStartAt().toString()))
                .body("[0].memberName", equalTo(member.getName()))
                .body("[0].themeName", equalTo(theme.getName()));
    }

    @Test
    @DisplayName("관리자 예약 조회 - 실패 - 필터링 조회 - 일반 유저 권한 부족")
    void getFilteredReservationsWithNonAdmin() {
        // given
        Member userMember = MemberFixture.create(RoleType.USER);
        memberRepository.save(userMember);
        String userToken = TestHelper.login(userMember.getEmail(), userMember.getPassword());

        // when & then
        RestAssured
                .given(this.documentationSpec)
                .contentType(ContentType.JSON)
                .cookie("token", userToken)
                .filter(document("admin-reservations-filtered-get-fail-no-permission",
                        queryParameters(
                                parameterWithName("themeId").description("테마 ID"),
                                parameterWithName("memberId").description("회원 ID"),
                                parameterWithName("dateFrom").description("조회 시작 날짜 (YYYY-MM-DD 형식)"),
                                parameterWithName("dateTo").description("조회 종료 날짜 (YYYY-MM-DD 형식)")
                        ),
                        responseFields(
                                fieldWithPath("message").description("에러 메시지")
                        )
                ))
                .when()
                .param("themeId", 1L)
                .param("memberId", 1L)
                .param("dateFrom", LocalDate.now().toString())
                .param("dateTo", LocalDate.now().plusWeeks(1).toString())
                .get("/admin/reservations/filtered")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("관리자 예약 생성 - 성공")
    void createReservationByAdmin() {
        // given
        String adminToken = TestHelper.login(member.getEmail(), member.getPassword());
        Member userMember = MemberFixture.create(RoleType.USER);
        memberRepository.save(userMember);

        var adminCreateRequest = new ReservationAdminCreateRequest(
                tomorrow,
                theme.getId(),
                reservationTime.getId(),
                userMember.getId()
        );

        // when & then
        RestAssured
                .given(this.documentationSpec)
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .body(adminCreateRequest)
                .filter(document("admin-reservations-post-success",
                        requestFields( // 요청 필드 문서화
                                fieldWithPath("date").description("예약 날짜 (YYYY-MM-DD 형식)"),
                                fieldWithPath("themeId").description("테마의 ID"),
                                fieldWithPath("timeId").description("예약 시간의 ID"),
                                fieldWithPath("memberId").description("예약할 회원의 ID")
                        ),
                        responseFields( // 응답 필드 문서화
                                fieldWithPath("id").description("생성된 예약의 ID"),
                                fieldWithPath("date").description("예약된 날짜"),
                                fieldWithPath("startAt").description("예약 시작 시간 (HH:mm 형식)"),
                                fieldWithPath("memberName").description("예약한 회원의 이름"),
                                fieldWithPath("themeName").description("예약된 테마의 이름")
                        )
                ))
                .when()
                .post("/admin/reservations")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("date", equalTo(tomorrow.toString()))
                .body("startAt", equalTo(reservationTime.getStartAt().toString()))
                .body("themeName", equalTo(theme.getName()));
    }

    @Test
    @DisplayName("관리자 예약 생성 - 실패 - 권한 부족")
    void createReservationByNonAdmin() {
        // given
        Member userMember = MemberFixture.create(RoleType.USER);
        memberRepository.save(userMember);
        String userToken = TestHelper.login(userMember.getEmail(), userMember.getPassword());

        var adminCreateRequest = new ReservationAdminCreateRequest(
                tomorrow,
                theme.getId(),
                reservationTime.getId(),
                userMember.getId()
        );

        // when & then
        RestAssured
                .given(this.documentationSpec)
                .contentType(ContentType.JSON)
                .cookie("token", userToken)
                .body(adminCreateRequest)
                .filter(document("admin-reservations-post-fail-no-permission",
                        requestFields( // 요청 필드 문서화
                                fieldWithPath("date").description("예약 날짜 (YYYY-MM-DD 형식)"),
                                fieldWithPath("themeId").description("테마의 ID"),
                                fieldWithPath("timeId").description("예약 시간의 ID"),
                                fieldWithPath("memberId").description("예약할 회원의 ID")
                        ),
                        responseFields( // 응답 필드 문서화
                                fieldWithPath("message").description("에러 메시지")
                        )
                ))
                .when()
                .post("/admin/reservations")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
}
