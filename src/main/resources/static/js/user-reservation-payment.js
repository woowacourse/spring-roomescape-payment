document.addEventListener('DOMContentLoaded', () => {

    // ------  결제위젯 초기화 ------
    // @docs https://docs.tosspayments.com/reference/widget-sdk#sdk-설치-및-초기화
    // @docs https://docs.tosspayments.com/reference/widget-sdk#renderpaymentmethods선택자-결제-금액-옵션
    const paymentAmount = 1000;
    const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";

    const paymentWidget = PaymentWidget(widgetClientKey, PaymentWidget.ANONYMOUS);
    paymentWidget.renderPaymentMethods(
        "#payment-method",
        {value: paymentAmount},
        {variantKey: "DEFAULT"}
    );

    document.getElementById('reserve-button')
        .addEventListener('click', onReservationButtonClickWithPaymentWidget);

    function onReservationButtonClickWithPaymentWidget(event) {
        onReservationButtonClick(event, paymentWidget);
    }
});

function onReservationButtonClick(event, paymentWidget) {
    const params = new URLSearchParams(window.location.search);
    const date = params.get('date');
    const themeId = params.get('themeId');
    const timeId = params.get('timeId');

    const reservationData = {
        date: date,
        themeId: themeId,
        timeId: timeId
    };

    const generateRandomString = () =>
        window.btoa(Math.random()).slice(0, 20);

    // 결제창 띄우기
    // TOSS 결제 위젯 Javascript SDK 연동 방식 중 'Promise로 처리하기'를 적용함
    // https://docs.tosspayments.com/reference/widget-sdk#promise%EB%A1%9C-%EC%B2%98%EB%A6%AC%ED%95%98%EA%B8%B0
    const orderIdPrefix = "EVER";
    paymentWidget.requestPayment({
        orderId: orderIdPrefix + generateRandomString(),
        orderName: "테스트 방탈출 예약 결제 1건",
        amount: 1000,
    }).then(function (data) {
        console.debug(data);
        fetchReservationPayment(data, reservationData);
    }).catch(function (error) {
        // TOSS 에러 처리: 에러 목록을 확인하세요
        // https://docs.tosspayments.com/reference/error-codes#failurl 로-전달되는-에러
        alert(error.code + " :" + error.message + "/ orderId : " + err.orderId);
    });
}

async function fetchReservationPayment(paymentData, reservationData) {
    /*
    TODO: [1단계]
        - 자신의 예약 API request에 맞게 reservationPaymentRequest 필드명 수정
        - 내 서버 URL에 맞게 reservationURL 변경
        - 예약 결제 실패 시, 사용자가 실패 사유를 알 수 있도록 alert 에서 에러 메시지 수정
    */
    const reservationPaymentRequest = {
        date: reservationData.date,
        themeId: reservationData.themeId,
        timeId: reservationData.timeId,
        paymentKey: paymentData.paymentKey,
        orderId: paymentData.orderId,
        amount: paymentData.amount,
        paymentType: paymentData.paymentType,
    }

    // 결제 요청 및 예약 저장
    const reservationURL = "/reservations";
    fetch(reservationURL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(reservationPaymentRequest),
    }).then(response => {
        if (!response.ok) {
            return response.json().then(errorBody => {
                console.error("예약 결제 실패 : " + JSON.stringify(errorBody));
                window.alert(errorBody.detail);
            });
        } else {
            response.json().then(successBody => {
                console.log("예약 결제 성공 : " + JSON.stringify(successBody));
                window.location.href = "/reservation";
            });
        }
    }).catch(error => {
        console.error(error.message);
    });
}
