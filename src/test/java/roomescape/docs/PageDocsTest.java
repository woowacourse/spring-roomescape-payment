package roomescape.docs;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

public class PageDocsTest extends RestDocsTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "/admin/time",
            "/admin/theme",
            "/admin/reservation",
            "/admin/waiting",
    })
    @DisplayName("관리자가 어드민 페이지에 접속한다.")
    void getAdminPageWhenAdmin(String url) {
        restDocs
                .cookie(COOKIE_NAME, getAdminToken(1L, "admin"))
                .when().get(url)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .apply(document("/page/admin-page" + url.replace("/admin", "") + "/by-admin"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/admin/time",
            "/admin/theme",
            "/admin/reservation",
            "/admin/waiting",
    })
    @DisplayName("회원이 어드민 페이지에 접속한다.")
    void getAdminPageWhenMember(String url) {
        restDocs
                .cookie(COOKIE_NAME, getMemberToken(1L, "wiib"))
                .when().get(url)
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .apply(document("/page/admin-page" + url.replace("/admin", "") + "/by-member"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/reservation",
            "/reservation-mine",
            "/login",
            "/signup",
    })
    @DisplayName("회원이 클라이언트 페이지에 접속한다.")
    void getClientPage(String url) {
        restDocs
                .cookie(COOKIE_NAME, getMemberToken(1L, "wiib"))
                .when().get(url)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .apply(document("/page/client-page" + url));
    }
}
