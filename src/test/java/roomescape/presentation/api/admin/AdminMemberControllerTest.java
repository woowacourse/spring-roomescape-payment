package roomescape.presentation.api.admin;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import static roomescape.support.docs.DescriptorUtil.MEMBER_DESCRIPTOR;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.fixture.Fixture;
import roomescape.presentation.BaseControllerTest;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class AdminMemberControllerTest extends BaseControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    private RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("모든 회원을 조회할 경우 성공하면 200을 반환한다.")
    void getAllMembers() {
        Member admin = memberRepository.save(Fixture.MEMBER_ADMIN);
        String token = tokenProvider.createToken(admin.getId().toString());

        RestAssured.given(spec).log().all()
                .accept("application/json")
                .filter(document("member/all-members",
                        requestCookies(cookieWithName("token").description("로그인시 응답받은 쿠키값입니다.")),
                        responseFields(
                                fieldWithPath("[]").description("멤버 배열입니다."))
                                .andWithPrefix("[].", MEMBER_DESCRIPTOR)))
                .cookie("token", token)
                .when().get("/admin/members")
                .then().log().all()
                .assertThat()
                .body("size()", equalTo(1))
                .body("id", hasItems(1))
                .body("name", hasItems("어드민"));
    }
}
