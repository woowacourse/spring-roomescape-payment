package roomescape.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.FieldDescriptor;

class MemberIntegrationTest extends IntegrationTest {
    @Nested
    @DisplayName("사용자 목록 조회 API")
    class FindAllMember {
        List<FieldDescriptor> memberFindAllResponseDescriptors = List.of(
                fieldWithPath("members.[].id").description("사용자 id"),
                fieldWithPath("members.[].name").description("이름"),
                fieldWithPath("members.[].email").description("이메일")
        );

        @Test
        void 사용자_목록을_조회할_수_있다() {
            memberFixture.createUserMember();
            memberFixture.createAdminMember();

            RestAssured.given(spec).log().all()
                    .filter(document(
                            "member-find-all-success",
                            responseFields(memberFindAllResponseDescriptors)
                    ))
                    .cookies(cookieProvider.createAdminCookies())
                    .when().get("/members")
                    .then().log().all()
                    .statusCode(200)
                    .body("members.size()", is(2));
        }
    }
}
