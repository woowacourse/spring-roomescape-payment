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

  document.getElementById('reserve-button').addEventListener('click',
    onReservationButtonClickWithPaymentWidget);

  function onReservationButtonClickWithPaymentWidget(event) {
    onReservationButtonClick(event, paymentWidget);
  }
});

function onReservationButtonClick(event, paymentWidget) {
  const generateRandomString = () =>
    window.btoa(Math.random()).slice(0, 20);
  const orderIdPrefix = "ORDER";
  const url = new URL(window.location.href);
  const urlParams = url.searchParams;
  const reservationId = urlParams.get('id');

  paymentWidget.requestPayment({
    orderId: orderIdPrefix + generateRandomString(),
    orderName: "테스트 방탈출 예약 결제 1건",
    amount: 1000,
  }).then(function (data) {
    console.debug(data);
    fetchReservationPayment(data, reservationId);
  }).catch(function (error) {
    alert(error.message);
  });
}

async function fetchReservationPayment(paymentData, reservationId) {
  const reservationPaymentRequest = {
    reservationId: reservationId,
    paymentKey: paymentData.paymentKey,
    orderId: paymentData.orderId,
    amount: paymentData.amount,
    paymentType: paymentData.paymentType,
  }

  const reservationURL = "/reservations/pay";
  fetch(reservationURL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(reservationPaymentRequest),
  }).then(response => {
    if (!response.ok) {
      return response.json().then(errorBody => {
        console.error("결제 실패: " + JSON.stringify(errorBody));
        window.alert(errorBody.message);
      });
    } else {
      response.json().then(successBody => {
        console.log("결제 성공: " + JSON.stringify(successBody));
        window.location.reload();
      });
    }
  }).catch(error => {
    console.error(error.message);
  });
}
