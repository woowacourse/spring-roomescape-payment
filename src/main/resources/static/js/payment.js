function getReservationIdFromUrl() {
  const params = new URLSearchParams(window.location.search);
  return params.get("reservationId");
}

document.addEventListener('DOMContentLoaded', () => {
  const paymentAmount = 1000;
  const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
  const paymentWidget = PaymentWidget(widgetClientKey, PaymentWidget.ANONYMOUS);

  paymentWidget.renderPaymentMethods(
      "#payment-method",
      { value: paymentAmount },
      { variantKey: "DEFAULT" }
  );

  const reserveButton = document.getElementById("payment-button");
  reserveButton.addEventListener("click", (event) => {
    onReservationButtonClick(event, paymentWidget);
  });
});

function onReservationButtonClick(event, paymentWidget) {
  const reservationId = getReservationIdFromUrl();

  if (!reservationId) {
    alert("예약 ID가 없습니다.");
    return;
  }

  const generateRandomString = () => window.btoa(Math.random()).slice(0, 20);
  const orderIdPrefix = "WTEST";

  paymentWidget.requestPayment({
    orderId: orderIdPrefix + generateRandomString(),
    orderName: "테스트 방탈출 예약 결제 1건",
    amount: 1000,
  })
  .then(data => {
    console.debug("결제 완료 응답:", data);
    fetchReservationPayment(data, reservationId);
  })
  .catch(error => {
    alert(error.code + " :" + error.message);
  });
}

async function fetchReservationPayment(paymentData, reservationId) {
  const reservationPaymentRequest = {
    reservationId: Number(reservationId), // Long 타입으로 변환
    paymentKey: paymentData.paymentKey,
    orderId: paymentData.orderId,
    amount: paymentData.amount,
  };

  const reservationURL = "/payment";

  try {
    const response = await fetch(reservationURL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(reservationPaymentRequest),
    });

    if (!response.ok) {
      const errorBody = await response.json();
      console.error("예약 결제 실패:", errorBody);
      alert("예약 결제 실패: " + (errorBody.message || "알 수 없는 오류"));
      return;
    }

    const successBody = await response.json();
    console.log("예약 결제 성공:", successBody);
    alert("예약 결제가 성공적으로 완료되었습니다!");
  } catch (error) {
    console.error("예약 결제 중 오류:", error);
    alert("예약 결제 중 오류가 발생했습니다.");
  }
}
