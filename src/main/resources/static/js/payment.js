const url = new URL(window.location.href);
const inputReservationId = url.searchParams.get("reservationId");
const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
const paymentWidget = PaymentWidget(widgetClientKey, PaymentWidget.ANONYMOUS);
paymentWidget.renderPaymentMethods(
    "#payment-method",
    {value: 1000},
    {variantKey: "DEFAULT"}
);

document.getElementById('payment-button').addEventListener('click', onReservationButtonClickWithPaymentWidget);
console.log('render payment page');
function onReservationButtonClickWithPaymentWidget(event) {
  onReservationButtonClick(event, paymentWidget);
}

function onReservationButtonClick(event, paymentWidget) {
  console.log('click payment button');
  const generateRandomString = () =>
      window.btoa(Math.random()).slice(0, 20);
  const orderIdPrefix = "WTEST";
  paymentWidget.requestPayment({
    orderId: orderIdPrefix + generateRandomString(),
    orderName: "테스트 방탈출 예약 결제 1건",
    amount: 1000,
  }).then(function (data) {
    const approveRequest = {
      paymentKey: data.paymentKey,
      orderId: data.orderId,
      amount: data.amount,
      reservationId: inputReservationId
    };

    fetchPayment(approveRequest)
        .then(response => {
          window.location.href = "/reservation-mine";
        });
  }).catch(function (error) {
    console.dir(error);
    alert(error.code + " :" + error.message + "/ orderId : " + err.orderId);
  });
}

function fetchPayment(data) {
    const paymentRequest = {
        paymentKey: data.paymentKey,
        orderId: data.orderId,
        amount: data.amount,
        reservationId: data.reservationId,
        paymentType: data.paymentType,
    };


  const reservationURL = "/payments";
  return fetch(reservationURL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  }).then(response => {
    if (response.status === 201) {
        response.json().then(successBody => {
            console.log("결제 성공 : " + JSON.stringify(successBody));
            window.location.href = '/reservation-mine';
        });
    } else {
        return response.json().then(errorBody => {
            console.error("결제 실패 : " + JSON.stringify(errorBody));
            window.alert("마이 페이지에서 재결제 해주세요.");
        });
    }
  }).catch(error => {
    console.error(error.message);
  });
}
