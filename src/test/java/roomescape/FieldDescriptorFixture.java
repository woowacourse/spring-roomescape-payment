package roomescape;

import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.ParameterDescriptor;

import java.util.List;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

public class FieldDescriptorFixture {

    public static final List<FieldDescriptor> memberFieldDescriptor = List.of(
            fieldWithPath("id").type(JsonFieldType.NUMBER).description("멤버 아이디"),
            fieldWithPath("name").type(JsonFieldType.STRING).description("멤버 이름"),
            fieldWithPath("email").type(JsonFieldType.STRING).description("멤버 이메일"),
            fieldWithPath("role").type(JsonFieldType.STRING).description("멤버 권한")
    );

    public static final List<FieldDescriptor> timeFieldDescriptor = List.of(
            fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 시작 시간 아이디"),
            fieldWithPath("startAt").type(JsonFieldType.STRING).description("예약 시작 시간")
    );

    public static final List<FieldDescriptor> timeListFieldDescriptor = List.of(
            fieldWithPath("[]").type(JsonFieldType.ARRAY).description("예약 시작 시간 리스트"),
            fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("예약 시작 시간 아이디"),
            fieldWithPath("[].startAt").type(JsonFieldType.STRING).description("예약 시작 시간")
    );

    public static final List<FieldDescriptor> themeFieldDescriptor = List.of(
            fieldWithPath("id").type(JsonFieldType.NUMBER).description("테마 아이디"),
            fieldWithPath("name").type(JsonFieldType.STRING).description("테마 이름"),
            fieldWithPath("description").type(JsonFieldType.STRING).description("테마 설명"),
            fieldWithPath("thumbnail").type(JsonFieldType.STRING).description("테마 썸네일")
    );

    public static final List<FieldDescriptor> themeListFieldDescriptor = List.of(
            fieldWithPath("[]").type(JsonFieldType.ARRAY).description("테마 리스트"),
            fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("테마 아이디"),
            fieldWithPath("[].name").type(JsonFieldType.STRING).description("테마 이름"),
            fieldWithPath("[].description").type(JsonFieldType.STRING).description("테마 설명"),
            fieldWithPath("[].thumbnail").type(JsonFieldType.STRING).description("테마 썸네일")
    );

    public static final List<FieldDescriptor> memberReservationSaveFieldDescriptor = List.of(
            fieldWithPath("date").type(JsonFieldType.STRING).description("날짜"),
            fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("예약 시작 시간 아이디"),
            fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("테마 아이디"),
            fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("결제키").optional(),
            fieldWithPath("orderId").type(JsonFieldType.STRING).description("주문 아이디").optional(),
            fieldWithPath("amount").type(JsonFieldType.NUMBER).description("가격").optional()
    );

    public static final List<FieldDescriptor> memberReservationFieldDescriptor = List.of(
            fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 아이디"),
            fieldWithPath("name").type(JsonFieldType.STRING).description("예약자 이름"),
            fieldWithPath("date").type(JsonFieldType.STRING).description("날짜"),
            fieldWithPath("time.id").type(JsonFieldType.NUMBER).description("예약 시작 시간 아이디"),
            fieldWithPath("time.startAt").type(JsonFieldType.STRING).description("예약 시작 시간"),
            fieldWithPath("theme.id").type(JsonFieldType.NUMBER).description("테마 아이디"),
            fieldWithPath("theme.name").type(JsonFieldType.STRING).description("테마 이름"),
            fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("결제키").optional(),
            fieldWithPath("orderId").type(JsonFieldType.STRING).description("주문 아이디").optional(),
            fieldWithPath("amount").type(JsonFieldType.NUMBER).description("가격").optional()
    );

    public static final List<FieldDescriptor> myReservationFieldDescriptor = List.of(
            fieldWithPath("reservationId").type(JsonFieldType.NUMBER).description("예약 아이디"),
            fieldWithPath("theme").type(JsonFieldType.STRING).description("테마 이름"),
            fieldWithPath("date").type(JsonFieldType.STRING).description("날짜"),
            fieldWithPath("time").type(JsonFieldType.STRING).description("예약 시작 시간"),
            fieldWithPath("status").type(JsonFieldType.STRING).description("예약 상태"),
            fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("결제 키").optional(),
            fieldWithPath("amount").type(JsonFieldType.NUMBER).description("가격").optional()
    );

    public static final List<FieldDescriptor> myReservationListFieldDescriptor = List.of(
            fieldWithPath("[]").type(JsonFieldType.ARRAY).description("예약 목록"),
            fieldWithPath("[].reservationId").type(JsonFieldType.NUMBER).description("예약 아이디"),
            fieldWithPath("[].theme").type(JsonFieldType.STRING).description("테마 이름"),
            fieldWithPath("[].date").type(JsonFieldType.STRING).description("날짜"),
            fieldWithPath("[].time").type(JsonFieldType.STRING).description("예약 시작 시간"),
            fieldWithPath("[].status").type(JsonFieldType.STRING).description("예약 상태"),
            fieldWithPath("[].paymentKey").type(JsonFieldType.STRING).description("결제 키").optional(),
            fieldWithPath("[].amount").type(JsonFieldType.NUMBER).description("가격").optional()
    );

    public static final List<FieldDescriptor> adminReservationSaveFieldDescriptor = List.of(
            fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("예약자 아이디"),
            fieldWithPath("date").type(JsonFieldType.STRING).description("날짜"),
            fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("예약 시작 시간 아이디"),
            fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("테마 아이디")
    );

    public static final List<FieldDescriptor> adminReservationFieldDescriptor = List.of(
            fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 아이디"),
            fieldWithPath("name").type(JsonFieldType.STRING).description("예약자 이름"),
            fieldWithPath("date").type(JsonFieldType.STRING).description("날짜"),
            fieldWithPath("time.id").type(JsonFieldType.NUMBER).description("예약 시작 시간 아이디"),
            fieldWithPath("time.startAt").type(JsonFieldType.STRING).description("예약 시작 시간"),
            fieldWithPath("theme.id").type(JsonFieldType.NUMBER).description("테마 아이디"),
            fieldWithPath("theme.name").type(JsonFieldType.STRING).description("테마 이름")
    );

    public static final List<FieldDescriptor> adminReservationListFieldDescriptor = List.of(
            fieldWithPath("[]").type(JsonFieldType.ARRAY).description("예약 리스트"),
            fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("예약 아이디"),
            fieldWithPath("[].name").type(JsonFieldType.STRING).description("예약자 이름"),
            fieldWithPath("[].date").type(JsonFieldType.STRING).description("날짜"),
            fieldWithPath("[].time.id").type(JsonFieldType.NUMBER).description("예약 시작 시간 아이디"),
            fieldWithPath("[].time.startAt").type(JsonFieldType.STRING).description("예약 시작 시간"),
            fieldWithPath("[].theme.id").type(JsonFieldType.NUMBER).description("테마 아이디"),
            fieldWithPath("[].theme.name").type(JsonFieldType.STRING).description("테마 이름")
    );

    public static final List<FieldDescriptor> loginFieldDescriptor = List.of(
            fieldWithPath("email").type(JsonFieldType.STRING).description("로그인 아이디"),
            fieldWithPath("password").type(JsonFieldType.STRING).description("로그인 비밀번호")
    );

    public static final List<FieldDescriptor> payFieldDescriptor = List.of(
            fieldWithPath("orderId").type(JsonFieldType.STRING).description("주문번호"),
            fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("결제 키"),
            fieldWithPath("amount").type(JsonFieldType.NUMBER).description("금액")
    );

    public static final List<FieldDescriptor> errorFieldDescriptor = List.of(
            fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메시지")
    );

    public static final List<FieldDescriptor> availableTimeListFieldDescriptor = List.of(
            fieldWithPath("[].id").description("시간 아이디"),
            fieldWithPath("[].startAt").description("시작 시간"),
            fieldWithPath("[].isReserved").description("예약 여부")
    );

    public static final List<ParameterDescriptor> reservationParameterDescriptor = List.of(
            parameterWithName("themeId").description("테마 아이디"),
            parameterWithName("memberId").description("이용자 아이디"),
            parameterWithName("dateFrom").description("시작 날짜"),
            parameterWithName("dateTo").description("종료 날짜")
    );

    public static final List<ParameterDescriptor> timeParameterDescriptor = List.of(
            parameterWithName("date").description("날짜"),
            parameterWithName("themeId").description("테마 아이디")
    );

    public static final List<CookieDescriptor> tokenCookieDescriptor = List.of(
            cookieWithName("token").description("토큰")
    );

    public static List<FieldDescriptor> timeFieldDescriptorWithoutId() {
        return timeFieldDescriptor.subList(1, timeFieldDescriptor.size());
    }

    public static List<FieldDescriptor> themeFieldDescriptorWithoutId() {
        return themeFieldDescriptor.subList(1, themeFieldDescriptor.size());
    }
}
