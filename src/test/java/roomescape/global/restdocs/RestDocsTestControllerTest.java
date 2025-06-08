package roomescape.global.restdocs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import roomescape.global.AuthInterceptor;
import roomescape.global.ControllerTest;
import roomescape.member.service.MemberService;

@WebMvcTest(RestDocsTestController.class)
@AutoConfigureRestDocs
class RestDocsTestControllerTest extends ControllerTest {

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockitoBean
    private AuthInterceptor authInterceptor;

    @DisplayName("200 상태, Hello를 응답으로 반환 후 restDocs를 생성한다")
    @Test
    void restDocsTest() throws Exception {

        // when & then
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello"))
                .andDo(restDocs.document());
        // 스니펫 생성 확인
        String snippetsDir = "build/generated-snippets/rest-docs-test-controller-test/rest-docs-test/";

        File requestSnippet = new File(snippetsDir + "http-request.adoc");
        File responseSnippet = new File(snippetsDir + "http-response.adoc");

        //then
        assertThat(requestSnippet).exists();
        assertThat(responseSnippet).exists();
    }

}
