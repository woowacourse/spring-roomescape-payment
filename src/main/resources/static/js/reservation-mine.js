document.addEventListener('DOMContentLoaded', () => {
  fetch('/reservations/mine') // 내 예약 목록 조회 API 호출
      .then(response => {
        if (response.status === 200) return response.json();
        throw new Error('Read failed');
      })
      .then(render)
      .catch(error => console.error('Error fetching reservations:', error));
  document.getElementById('payment-widget').style.display = 'none';
});

function render(data) {
  const tableBody = document.getElementById('table-body');
  tableBody.innerHTML = '';

  data.forEach(item => {
    const row = tableBody.insertRow();

    const theme = item.theme;
    const date = item.date;
    const time = item.time;
    const waitingOrder = item.waitingOrder;
    const status = item.status;

    row.insertCell(0).textContent = theme;
    row.insertCell(1).textContent = date;
    row.insertCell(2).textContent = time;
    row.insertCell(3).textContent = '';
    row.insertCell(4).textContent = '';
    row.insertCell(5).textContent = '';
    row.insertCell(6).textContent = '';
    row.insertCell(7).textContent = '';

    if (status === 'PAYMENT_WAITING') {
      row.cells[3].textContent = '결제 대기';
      const paymentCell = row.cells[4];
      const paymentButton = document.createElement("button");
      paymentButton.textContent = '결제';
      paymentButton.className = 'btn btn-primary';
      paymentButton.onclick = function () {
        renderPaymentWidget(item.id);
      }
      paymentCell.appendChild(paymentButton);
    } else if (status !== 'BOOKED') { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능
      row.cells[3].textContent = waitingOrder + '번째 예약 대기';
      const cancelCell = row.cells[5];
      const cancelButton = document.createElement('button');
      cancelButton.textContent = '취소';
      cancelButton.className = 'btn btn-danger';
      cancelButton.onclick = function () {
        requestDeleteWaiting(item.id).then(() => window.location.reload());
      };
      cancelCell.appendChild(cancelButton);
    } else { // 예약 완료 상태일 때
      /*
      TODO: [미션4 - 2단계] 내 예약 목록 조회 시,
        예약 완료 상태일 때 결제 정보를 함께 보여주기
        결제 정보 필드명은 자신의 response 에 맞게 변경하기
      */
      row.cells[3].textContent = '예약 확정'
      const cancelCell = row.cells[5];
      const cancelButton = document.createElement('button');
      cancelButton.textContent = '취소';
      cancelButton.className = 'btn btn-danger';
      cancelButton.onclick = function () {
        requestDeleteReservation(item.id).then(() => window.location.reload());
      };
      cancelCell.appendChild(cancelButton);
      row.cells[6].textContent = item.paymentKey;
      row.cells[7].textContent = item.amount;
    }
  });
}

function requestDeleteReservation(id) {
  const endpoint = `/reservations/${id}`;
  return fetch(endpoint, {
    method: 'DELETE'
  }).then(response => {
    if (response.status === 204) return;
    throw new Error('Delete failed');
  });
}

function requestDeleteWaiting(id) {
  /*
  TODO: [3단계] 예약 대기 기능 - 예약 대기 취소 API 호출
   */
  const endpoint = `/waitings/${id}`;
  return fetch(endpoint, {
    method: 'DELETE'
  }).then(response => {
    if (response.status === 204) return;
    throw new Error('Delete failed');
  });
}

function renderPaymentWidget(reservationId) {
  document.getElementById('payment-widget').style.display = '';

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

  document.getElementById('pay-button').addEventListener('click', () => {
    onReservationButtonClick(event, reservationId, paymentWidget)
  });
}

function onReservationButtonClick(event, reservationId, paymentWidget) {
  console.log(reservationId);
  const generateRandomString = () => window.btoa(Math.random()).slice(0, 20);
  // TOSS 결제 위젯 Javascript SDK 연동 방식 중 'Promise로 처리하기'를 적용함
  // https://docs.tosspayments.com/reference/widget-sdk#promise%EB%A1%9C-%EC%B2%98%EB%A6%AC%ED%95%98%EA%B8%B0
  const orderIdPrefix = "roomescape_";
  paymentWidget.requestPayment({
    orderId: orderIdPrefix + generateRandomString(),
    orderName: "테스트 방탈출 예약 결제 1건",
    amount: 1000,
  }).then(function (data) {
    console.debug(data);
    fetchReservationPayment(reservationId, data);
  }).catch(function (error) {
    // TOSS 에러 처리: 에러 목록을 확인하세요
    // https://docs.tosspayments.com/reference/error-codes#failurl 로-전달되는-에러
    alert(error.code + " :" + error.message + "/ orderId : " + err.orderId);
  });
}

async function fetchReservationPayment(reservationId, paymentData) {
  const paymentRequest = {
    reservationId: reservationId,
    paymentKey: paymentData.paymentKey,
    orderId: paymentData.orderId,
    amount: paymentData.amount,
    paymentType: paymentData.paymentType,
  }

  const reservationURL = "/payments";
  fetch(reservationURL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(paymentRequest),
  }).then(response => {
    if (!response.ok) {
      return response.json().then(errorBody => {
        console.error("예약 결제 실패 : " + JSON.stringify(errorBody));
        window.alert(errorBody.detail);
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
