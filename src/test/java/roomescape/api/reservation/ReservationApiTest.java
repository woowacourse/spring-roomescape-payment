package roomescape.api.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.TestClientConfig;
import roomescape.api.fixture.DocumentationFixture;
import roomescape.auth.dto.LoginRequest;
import roomescape.client.dto.PaymentsConfirmResponse;
import roomescape.reservation.dto.AdminReservationCreateRequest;
import roomescape.reservation.dto.AdminReservationResponse;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.dto.UserReservationCreateRequest;
import roomescape.reservation.dto.UserReservationResponse;
import roomescape.reservation.repository.ReservationRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Sql({"/test-time-data.sql", "/test-theme-data.sql", "/test-member-data.sql", "/test-reservation-data.sql"})
@Import(TestClientConfig.class)
@ExtendWith(RestDocumentationExtension.class)
public class ReservationApiTest {

    private static final String AUTH_COOKIE_NAME = "token";
    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ReservationRepository reservationRepository;

    private RequestSpecification documentationSpecification;

    @BeforeEach
    void setUp(final RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        this.documentationSpecification = DocumentationFixture
                .createDefaultDocumentationSpecification(restDocumentation);
    }

    @DisplayName("POST /reservations : 예약 추가 API 테스트")
    @Test
    void create() throws JsonProcessingException {
        // given
        String memberToken = getMemberToken();
        UserReservationCreateRequest request = new UserReservationCreateRequest(
                TOMORROW, 1L, 1L,
                "payment_key", "order_id", 1000L
        );
        setMockServer();
        // when
        UserReservationResponse actualResponse = RestAssured.given(documentationSpecification).log().all()
                .contentType(ContentType.JSON)
                .cookie(AUTH_COOKIE_NAME, memberToken)
                .body(request)
                .filter(DocumentationFixture.createDocumentWithDefaultPath())
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .extract().as(UserReservationResponse.class);
        // then
        assertAll(
                () -> assertThat(actualResponse.id()).isEqualTo(4L),
                () -> assertThat(actualResponse.member().id()).isEqualTo(1L),
                () -> assertThat(actualResponse.date()).isEqualTo(TOMORROW),
                () -> assertThat(actualResponse.time().id()).isEqualTo(1L),
                () -> assertThat(actualResponse.theme().id()).isEqualTo(1L),
                () -> assertThat(actualResponse.payment().id()).isEqualTo(1L)
        );
    }

    @DisplayName("POST /admin/reservations : 어드민 예약 생성 API 테스트")
    @Test
    void createByAdmin() {
        // given
        String adminToken = getAdminToken();
        AdminReservationCreateRequest request = new AdminReservationCreateRequest(TOMORROW, 1L, 1L, 1L);
        // when
        AdminReservationResponse actualResponse = RestAssured.given(documentationSpecification).log().all()
                .contentType(ContentType.JSON)
                .cookie(AUTH_COOKIE_NAME, adminToken)
                .body(request)
                .filter(DocumentationFixture.createDocumentWithDefaultPath())
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(201)
                .extract().as(AdminReservationResponse.class);
        // then
        assertAll(
                () -> assertThat(actualResponse.id()).isEqualTo(4L),
                () -> assertThat(actualResponse.member().id()).isEqualTo(1L),
                () -> assertThat(actualResponse.date()).isEqualTo(TOMORROW),
                () -> assertThat(actualResponse.time().id()).isEqualTo(1L),
                () -> assertThat(actualResponse.theme().id()).isEqualTo(1L)
        );
    }

    @DisplayName("GET /me/reservations : 내 예약 조회 API 테스트")
    @Test
    void findAllMyReservations() {
        // given
        String loginToken = getMemberToken();
        // when
        MyReservationResponse[] actualResponse = RestAssured.given(documentationSpecification).log().all()
                .cookie(AUTH_COOKIE_NAME, loginToken)
                .filter(DocumentationFixture.createDocumentWithDefaultPath())
                .when().get("/me/reservations")
                .then().log().all()
                .statusCode(200)
                .extract().as(MyReservationResponse[].class);
        // then
        assertThat(actualResponse).hasSize(3);
    }

    @DisplayName("GET /reservations : 어드민 예약 전체 조회 API 테스트")
    @Test
    void findAll() {
        // given
        String adminToken = getAdminToken();
        // when
        AdminReservationResponse[] actualResponse = RestAssured.given(documentationSpecification).log().all()
                .cookie(AUTH_COOKIE_NAME, adminToken)
                .filter(DocumentationFixture.createDocumentWithDefaultPath())
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .extract().as(AdminReservationResponse[].class);
        // then
        assertThat(actualResponse).hasSize(3);
    }

    @DisplayName("GET /admin/reservations? : 어드민 필터 조회 API 테스트")
    @Test
    void findFiltered() {
        // given
        String adminToken = getAdminToken();
        Map<String, Object> filterQueryParams = Map.of(
                "themeId", 1L,
                "memberId", 1L,
                "dateFrom", LocalDate.of(2025, 5, 1).format(DateTimeFormatter.ISO_DATE),
                "dateTo", LocalDate.of(2025, 5, 2).format(DateTimeFormatter.ISO_DATE)
        );
        // when
        AdminReservationResponse[] actualResponse = RestAssured.given(documentationSpecification)
                .cookie(AUTH_COOKIE_NAME, adminToken)
                .queryParams(filterQueryParams)
                .filter(DocumentationFixture.createDocumentWithDefaultPath())
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200)
                .extract().as(AdminReservationResponse[].class);
        // then
        assertThat(actualResponse).hasSize(2);
    }

    @DisplayName("DELETE /admin/reservations/{id} : 예약 취소 API 테스트")
    @Test
    void delete() {
        // given
        int reservationIdOfMember1 = 3;
        String adminToken = getAdminToken();
        // when
        RestAssured.given(documentationSpecification).log().all()
                .cookie(AUTH_COOKIE_NAME, adminToken)
                .filter(DocumentationFixture.createDocumentWithDefaultPath())
                .when().delete("/admin/reservations/{id}", reservationIdOfMember1)
                .then().log().all()
                .statusCode(204);
        // then
        assertThat(reservationRepository.count()).isEqualTo(2);
    }

    private String getMemberToken() {
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new LoginRequest("aaa@gmail.com", "1234"))
                .when().post("/login")
                .then().log().all()
                .extract().cookie(AUTH_COOKIE_NAME);
    }

    private String getAdminToken() {
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new LoginRequest("admin@gmail.com", "1234"))
                .when().post("/login")
                .then().log().all()
                .extract().cookie(AUTH_COOKIE_NAME);
    }

    private void setMockServer() throws JsonProcessingException {
        PaymentsConfirmResponse expectedResponse = new PaymentsConfirmResponse("payment_key",
                "order_id", 1000L);
        server.reset();
        server.expect(requestTo("https://api.tosspayments.com/v1/payments" + "/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(mapper.writeValueAsString(expectedResponse))
                        .contentType(MediaType.APPLICATION_JSON));
    }
}
