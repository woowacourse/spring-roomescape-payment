package roomescape.support.docs;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.time.LocalDate;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

public class DescriptorUtil {

    public static FieldDescriptor[] WAITING_DESCRIPTOR = new FieldDescriptor[]{
            fieldWithPath("id").type(JsonFieldType.NUMBER).description("테마 아이디입니다."),
            fieldWithPath("date").type(LocalDate.class).description("테마 이름입니다."),
            fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("테마 설명입니다."),
            fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("테마 이미지입니다.")};

    public static FieldDescriptor[] THEME_DESCRIPTOR = new FieldDescriptor[]{
            fieldWithPath("id").type(JsonFieldType.NUMBER).description("테마 아이디입니다."),
            fieldWithPath("name").type(JsonFieldType.STRING).description("테마 이름입니다."),
            fieldWithPath("description").type(JsonFieldType.STRING).description("테마 설명입니다."),
            fieldWithPath("thumbnail").type(JsonFieldType.STRING).description("테마 이미지입니다.")};

    public static FieldDescriptor[] RESERVATION_TIME_DESCRIPTOR = new FieldDescriptor[]{
            fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 시간 아이디입니다."),
            fieldWithPath("startAt").type(JsonFieldType.STRING).description("예약 시간입니다.")};

    public static FieldDescriptor[] ERROR_MESSAGE_DESCRIPTOR = new FieldDescriptor[]{
            fieldWithPath("message").description("예외 메시지입니다.")};

    public static FieldDescriptor[] LOGIN_DESCRIPTOR = new FieldDescriptor[]{
            fieldWithPath("email").type(JsonFieldType.STRING).description("입력할 사용자 이메일입니다."),
            fieldWithPath("password").type(JsonFieldType.STRING).description("사용자 이메일입니다."),
            fieldWithPath("name").type(JsonFieldType.STRING).description("사용자 이름입니다.")};

    public static FieldDescriptor[] MEMBER_DESCRIPTOR = new FieldDescriptor[]{
            fieldWithPath("id").type(JsonFieldType.NUMBER).description("사용자 아이디입니다."),
            fieldWithPath("email").type(JsonFieldType.STRING).description("사용자 이메일입니다."),
            fieldWithPath("name").type(JsonFieldType.STRING).description("사용자 이름입니다."),
            fieldWithPath("role").type(JsonFieldType.STRING).description("사용자 권한입니다."),};

    public static FieldDescriptor[] RESERVATION_REQUEST_DESCRIPTOR = new FieldDescriptor[]{
            fieldWithPath("date").type(LocalDate.class).description("예약할 날짜입니다."),
            fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("예약할 예약 시간 아이디입니다."),
            fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("예약할 테마 아이디입니다."),
            fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("토스에서 제공되는 payment key입니다."),
            fieldWithPath("orderId").type(JsonFieldType.STRING).description("결제 아이디입니다."),
            fieldWithPath("amount").type(JsonFieldType.NUMBER).description("예약할 방탈출의 결제 금액입니다."),
            fieldWithPath("paymentType").type(JsonFieldType.STRING).description("결제 타입을 나타냅니다.")};

}
