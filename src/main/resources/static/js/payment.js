const url = new URL(window.location.href);
console.log(url);
const reservationId = url.searchParams.get("reservationId");
const paymentAmount = 50;
const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
const paymentWidget = PaymentWidget(widgetClientKey, PaymentWidget.ANONYMOUS);
paymentWidget.renderPaymentMethods(
    "#payment-method",
    {value: paymentAmount},
    {variantKey: "DEFAULT"}
);

document.getElementById('payment-button').addEventListener('click', onReservationButtonClickWithPaymentWidget);
function onReservationButtonClickWithPaymentWidget(event) {
  onReservationButtonClick(event, paymentWidget);
}

function onReservationButtonClick(event, paymentWidget) {
  const generateRandomString = () =>
      window.btoa(Math.random()).slice(0, 20);
  const orderIdPrefix = "MyNameIsNicoRobin";
  paymentWidget.requestPayment({
    orderId: orderIdPrefix + generateRandomString(),
    orderName: reservationId +"번 예약 결제"
  }).then(function (data) {
    const approveRequest = {
      paymentKey: data.paymentKey,
      orderId: data.orderId,
      amount: data.amount,
      reservationId: reservationId
    };
    fetchPaymentApprove(approveRequest)
        .then(response => {
          window.location.href = "/reservation-mine";
        });
  }).catch(function (error) {
    console.dir(error);
    alert(error.code + " :" + error.message + "/ orderId : " + err.orderId);
  });
}
function fetchPaymentApprove(data) {
  console.log(data);
  const reservationURL = "/payment";
  return fetch(reservationURL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  }).then(response => {
    if (!response.ok) {
      return response.json().then(errorBody => {
        console.error("예약 결제 실패 : " + errorBody.message);
        window.alert(errorBody.message);
      });
    } else {
      response.json().then(successBody => {
        console.log("예약 결제 성공 : " + JSON.stringify(successBody));
        window.location.reload();
      });
    }
  }).catch(error => {
    console.error(error.message);
  });
}
