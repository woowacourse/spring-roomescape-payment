package roomescape.presentation.api.admin;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import roomescape.application.dto.request.ThemeRequest;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.member.Role;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.detail.ReservationDetail;
import roomescape.domain.reservation.detail.ReservationTime;
import roomescape.domain.reservation.detail.ReservationTimeRepository;
import roomescape.domain.reservation.detail.Theme;
import roomescape.domain.reservation.detail.ThemeRepository;
import roomescape.fixture.Fixture;
import roomescape.presentation.BaseControllerTest;

class AdminThemeControllerTest extends BaseControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Nested
    @DisplayName("테마를 추가하는 경우")
    class AddTheme extends BaseControllerTest {

        @Test
        @DisplayName("성공하면 201을 반환한다.")
        void success() {
            Member admin = memberRepository.save(Fixture.MEMBER_ADMIN);
            String token = tokenProvider.createToken(admin.getId().toString());

            ThemeRequest request = new ThemeRequest("테마 이름", "테마 설명", "https://example.com/image.jpg");

            RestAssured.given().log().all()
                    .cookie("token", token)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/admin/themes")
                    .then().log().all()
                    .statusCode(HttpStatus.CREATED.value())
                    .header("Location", response -> equalTo("/themes/" + response.path("id")))
                    .and()
                    .body("id", equalTo(1))
                    .body("name", equalTo("테마 이름"))
                    .body("description", equalTo("테마 설명"))
                    .body("thumbnail", equalTo("https://example.com/image.jpg"));
        }

        @Test
        @DisplayName("이미 존재하는 테마 이름이면 400을 반환한다.")
        void addThemeFailWhenNameAlreadyExists() {
            Member admin = memberRepository.save(Fixture.MEMBER_ADMIN);
            String token = tokenProvider.createToken(admin.getId().toString());

            themeRepository.save(new Theme("테마 이름", "테마 설명", "https://example.com/image.jpg"));

            ThemeRequest request = new ThemeRequest("테마 이름", "테마 설명-2", "https://example.com/image-2.jpg");

            RestAssured.given().log().all()
                    .cookie("token", token)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/admin/themes")
                    .then().log().all()
                    .assertThat()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", containsString("해당 이름의 테마는 이미 존재합니다."))
                    .extract();
        }
    }

    @Nested
    @DisplayName("테마를 삭제하는 경우")
    class DeleteTheme extends BaseControllerTest {

        @Test
        @DisplayName("성공하면 204를 반환한다.")
        void success() {
            Member admin = memberRepository.save(Fixture.MEMBER_ADMIN);
            String token = tokenProvider.createToken(admin.getId().toString());

            Theme theme = themeRepository.save(new Theme("테마 이름", "테마 설명", "https://example.com"));

            RestAssured.given().log().all()
                    .cookie("token", token)
                    .when().delete("/admin/themes/" + theme.getId())
                    .then().log().all()
                    .assertThat()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }


        @Test
        @DisplayName("존재하지 않는 테마를 삭제하면 404를 반환한다.")
        void deleteThemeByIdFailWhenNotFoundId() {
            Member admin = memberRepository.save(Fixture.MEMBER_ADMIN);
            String token = tokenProvider.createToken(admin.getId().toString());

            RestAssured.given().log().all()
                    .cookie("token", token)
                    .when().delete("/admin/themes/1")
                    .then().log().all()
                    .assertThat()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("message", containsString("해당 id의 테마가 존재하지 않습니다."));
        }

        @Test
        @DisplayName("이미 사용 중인 테마을 삭제하면 400을 반환한다.")
        void deleteThemeByIdFailWhenUsedTheme() {
            Member admin = memberRepository.save(Fixture.MEMBER_ADMIN);
            String token = tokenProvider.createToken(admin.getId().toString());

            Member member = memberRepository.save(new Member("member@gmail.com", "password", "member", Role.USER));
            ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(10, 30)));
            Theme theme = themeRepository.save(new Theme("테마 이름", "테마 설명", "https://example.com"));

            ReservationDetail detail = new ReservationDetail(LocalDate.of(2024, 6, 22), time, theme);
            reservationRepository.save(new Reservation(detail, member));

            RestAssured.given().log().all()
                    .cookie("token", token)
                    .pathParam("id", theme.getId())
                    .when().delete("/admin/themes/{id}")
                    .then().log().all()
                    .assertThat()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", containsString("해당 테마를 사용하는 예약이 존재합니다."));
        }
    }
}
