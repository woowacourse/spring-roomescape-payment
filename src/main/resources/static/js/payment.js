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

    document.getElementById('reserve-button').addEventListener('click', onReservationButtonClickWithPaymentWidget);

    function onReservationButtonClickWithPaymentWidget(event) {
        onReservationButtonClick(event, paymentWidget);
    }
});

function onReservationButtonClick(event, paymentWidget) {
    const generateRandomString = () =>
        window.btoa(Math.random()).slice(0, 20);
    // TOSS 결제 위젯 Javascript SDK 연동 방식 중 'Promise로 처리하기'를 적용함
    // https://docs.tosspayments.com/reference/widget-sdk#promise%EB%A1%9C-%EC%B2%98%EB%A6%AC%ED%95%98%EA%B8%B0
    const orderIdPrefix = "WTEST";
    paymentWidget.requestPayment({
        orderId: orderIdPrefix + generateRandomString(),
        orderName: "테스트 방탈출 예약 결제 1건",
        amount: 1000,
    }).then(function (data) {
        let reservationId = new URLSearchParams(window.location.search).get('reservationId');
        data.reservationId = reservationId;
        console.debug(data);
        fetchReservationPayment(data);
    }).catch(function (error) {
        // TOSS 에러 처리: 에러 목록을 확인하세요
        // https://docs.tosspayments.com/reference/error-codes#failurl 로-전달되는-에러
        alert(error.code + " :" + error.message + "/ orderId : " + error.orderId);
    });
}

async function fetchReservationPayment(paymentData) {
    const paymentRequest = {
        reservationId: paymentData.reservationId,
        paymentKey: paymentData.paymentKey,
        orderId: paymentData.orderId,
        amount: paymentData.amount,
        paymentType: paymentData.paymentType,
    }

    const paymentURL = "/payments";
    fetch(paymentURL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(paymentRequest),
    }).then(response => {
        if (!response.ok) {
            return response.json().then(errorBody => {
                console.error("결제 실패 : " + JSON.stringify(errorBody));
                window.alert(errorBody.message);
            });
        } else {
            response.json().then(successBody => {
                console.log("결제 성공 : " + JSON.stringify(successBody));
                window.location.href = "/reservation-mine";
            });
        }
    }).catch(error => {
        console.error(error.message);
    });
}
