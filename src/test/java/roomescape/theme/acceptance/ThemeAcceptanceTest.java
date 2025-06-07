package roomescape.theme.acceptance;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import fixture.MemberFixture;
import fixture.PaymentFixture;
import fixture.ReservationFixture;
import fixture.ReservationTimeFixture;
import fixture.ThemeFixture;
import io.restassured.RestAssured;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.helper.TestHelper;
import roomescape.member.entity.Member;
import roomescape.member.entity.RoleType;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.entity.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.entity.Reservation;
import roomescape.reservation.entity.ReservationTime;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.theme.dto.request.ThemeCreateRequest;
import roomescape.theme.entity.Theme;
import roomescape.theme.repository.ThemeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ThemeAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;

    Member member;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        member = MemberFixture.create(RoleType.ADMIN);
        memberRepository.save(member);
    }

    @Test
    @DisplayName("테마 생성 요청 - 성공")
    void createTheme() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());
        Theme theme = ThemeFixture.createDefault();
        var request = new ThemeCreateRequest(
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnail()
        );

        // when & then
        TestHelper.postWithToken("/admin/themes", request, token)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", equalTo(theme.getName()))
                .body("description", equalTo(theme.getDescription()))
                .body("thumbnail", equalTo(theme.getThumbnail()));
    }

    @Test
    @DisplayName("테마 생성 요청 - 중복 이름으로 실패")
    void createThemeWithDuplicateName() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());
        Theme theme = ThemeFixture.createDefault();
        var request = new ThemeCreateRequest(
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnail()
        );
        var duplicatedThemeNameRequest = new ThemeCreateRequest(
                theme.getName(),
                theme.getDescription() + "diff",
                theme.getThumbnail() + "diff"
        );

        TestHelper.postWithToken("/admin/themes", request, token)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // when & then
        TestHelper.postWithToken("/admin/themes", duplicatedThemeNameRequest, token)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("모든 테마 조회 요청")
    void getAllThemes() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());
        List<Theme> themes = ThemeFixture.createDefaultList(2);
        var request1 = new ThemeCreateRequest(
                themes.get(0).getName(),
                themes.get(0).getDescription(),
                themes.get(0).getThumbnail()
        );
        var request2 = new ThemeCreateRequest(
                themes.get(1).getName(),
                themes.get(1).getDescription(),
                themes.get(1).getThumbnail()
        );

        TestHelper.postWithToken("/admin/themes", request1, token)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        TestHelper.postWithToken("/admin/themes", request2, token)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // when & then
        TestHelper.get("/themes")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(2))
                .body("[0].name", equalTo(themes.get(0).getName()))
                .body("[1].name", equalTo(themes.get(1).getName()));
    }

    @Test
    @DisplayName("인기 테마 조회 요청 - 인기순 정렬 확인")
    void getPopularThemes() {
        // given
        List<Theme> themes = ThemeFixture.createDefaultList(2);
        themeRepository.saveAll(themes);

        ReservationTime reservationTime = ReservationTimeFixture.createDefault();
        reservationTimeRepository.save(reservationTime);

        Payment payment = PaymentFixture.createDefault();
        paymentRepository.save(payment);

        LocalDate yesterday = LocalDate.now().minusDays(1);
        Reservation reservation = ReservationFixture.create(yesterday, reservationTime, themes.get(1), member, payment);
        reservationRepository.save(reservation);

        // when & then
        TestHelper.get("/themes/popular?limit=2")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(2))
                .body("[0].name", equalTo(themes.get(1).getName()));
    }

    @Test
    @DisplayName("인기 테마 조회 요청 - limit 개수 확인")
    void getPopularThemesWhenExistsLimit() {
        // given
        List<Theme> themes = ThemeFixture.createDefaultList(10);
        themeRepository.saveAll(themes);

        // when & then
        TestHelper.get("/themes/popular?limit=2")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(2));
    }

    @Test
    @DisplayName("테마 삭제 요청 - 성공")
    void deleteTheme() {
        // given
        Theme theme = ThemeFixture.createDefault();
        themeRepository.save(theme);
        String token = TestHelper.login(member.getEmail(), member.getPassword());

        // when & then
        TestHelper.deleteWithToken("/admin/themes/1", token)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        TestHelper.get("/themes")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(0));
    }
}
