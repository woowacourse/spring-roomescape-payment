package roomescape.api.member;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static roomescape.api.fixture.DocumentationFixture.createDocumentWithDefaultPath;

import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

public class MemberDocumentationFixture {

    // descriptors
    public static final FieldDescriptor MEMBER_NAME_FIELD_DESCRIPTOR = fieldWithPath("name").description("회원 이름");
    public static final FieldDescriptor MEMBER_EMAIL_FIELD_DESCRIPTOR = fieldWithPath("email").description("회원 이메일");
    public static final FieldDescriptor MEMBER_PASSWORD_FIELD_DESCRIPTOR = fieldWithPath("password")
            .description("회원 비밀번호");
    public static final FieldDescriptor MEMBER_ID_FIELD_DESCRIPTOR = fieldWithPath("id")
            .description("회원 ID");
    public static final FieldDescriptor MEMBER_ARRAY = fieldWithPath("[]").description("회원 배열");

    public static final List<FieldDescriptor> MEMBER_RESPONSE_DESCRIPTORS = List.of(
            MEMBER_ID_FIELD_DESCRIPTOR,
            MEMBER_NAME_FIELD_DESCRIPTOR,
            MEMBER_EMAIL_FIELD_DESCRIPTOR
    );

    // documents
    public static final RestDocumentationFilter SIGN_UP_DOCUMENT = createDocumentWithDefaultPath(
            requestFields(
                    MEMBER_NAME_FIELD_DESCRIPTOR,
                    MEMBER_EMAIL_FIELD_DESCRIPTOR,
                    MEMBER_PASSWORD_FIELD_DESCRIPTOR
            ),
            responseFields(MEMBER_RESPONSE_DESCRIPTORS)
    );
    public static final RestDocumentationFilter GET_MEMBERS_DOCUMENT = createDocumentWithDefaultPath(
            responseFields(MEMBER_ARRAY).andWithPrefix("[].", MEMBER_RESPONSE_DESCRIPTORS)
    );
}
