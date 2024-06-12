document.addEventListener('DOMContentLoaded', async () => {
  const data = [
    ...await getMyReservations(),
    ...await getMyReservationWaiting()
  ];
  render(data);

  // ------  결제위젯 초기화 ------
  // @docs https://docs.tosspayments.com/reference/widget-sdk#sdk-설치-및-초기화
  // @docs https://docs.tosspayments.com/reference/widget-sdk#renderpaymentmethods선택자-결제-금액-옵션
  const paymentAmount = 13500;
  const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
  const paymentWidget = PaymentWidget(widgetClientKey, PaymentWidget.ANONYMOUS);

  paymentWidget.renderPaymentMethods(
    "#payment-method",
    {value: paymentAmount},
    {variantKey: "DEFAULT"}
  );

  document.getElementById('content-container').style.display = 'none';
  document.getElementById('reserve-button').addEventListener('click', onReservationButtonClickWithPaymentWidget);
  function onReservationButtonClickWithPaymentWidget(event) {
    onReservationButtonClick(paymentWidget);
  }
});

function getMyReservations() {
  return fetch('/reservations-mine') // 내 예약 목록 조회 API 호출
    .then(response => {
      if (response.status === 200) return response.json();
      throw new Error('Read failed');
    })
    .catch(error => console.error('Error fetching reservations:', error));
}

function getMyReservationWaiting() {
  return fetch('/reservation-waiting-mine') // 내 예약 목록 조회 API 호출
    .then(response => {
      if (response.status === 200) return response.json();
      throw new Error('Read failed');
    })
    .catch(error => console.error('Error fetching reservation waiting:', error));
}

function createWaitingStatus(item) {
  if (item.paymentAvailable) {
    return "결제 대기";
  }

  return item.order + "번째 예약대기";
}

function render(data) {
  const tableBody = document.getElementById('table-body');
  tableBody.innerHTML = '';

  data.forEach(item => {
    const row = tableBody.insertRow();
    const theme = item.theme;
    const date = item.date;
    const time = item.time;
    const status = (item["order"] !== undefined) ? createWaitingStatus(item) : item.status;
    const paymentAvailable = (item["paymentAvailable"] !== undefined) ? item.paymentAvailable  : false;

    row.insertCell(0).textContent = theme;
    row.insertCell(1).textContent = date;
    row.insertCell(2).textContent = time;
    row.insertCell(3).textContent = status;

    if (status === "예약" || status === "취소") { // 예약 완료 상태일 때
      /*
      TODO: [미션4 - 2단계] 내 예약 목록 조회 시,
       예약 완료 상태일 때 결제 정보를 함께 보여주기
       결제 정보 필드명은 자신의 response 에 맞게 변경하기
     */
      row.insertCell(4).textContent = '';
      row.insertCell(5).textContent = item.paymentKey;
      row.insertCell(6).textContent = item.amount;
    } else {
      if (paymentAvailable === true) {
        const paymentCell = row.insertCell(4);
        const paymentButton = document.createElement('button');
        paymentButton.textContent = '결제';
        paymentButton.style.backgroundColor = 'blue';
        paymentButton.className = 'btn btn-danger';
        paymentButton.onclick = function () {
          onPaymentButtonClick(item.id);
        };
        paymentCell.appendChild(paymentButton);
      } else {
        const cancelCell = row.insertCell(4);
        const cancelButton = document.createElement('button');
        cancelButton.textContent = '취소';
        cancelButton.className = 'btn btn-danger';
        cancelButton.onclick = function () {
          requestDeleteWaiting(item.id).then(() => window.location.reload());
        };
        cancelCell.appendChild(cancelButton);
      }
    }
  });
}

function requestDeleteWaiting(id) {
  const endpoint = `/reservation-waiting/${id}`;
  return fetch(endpoint, {
    method: 'DELETE'
  }).then(response => {
    if (response.status === 204) return;
    throw new Error('Delete failed');
  });
}

let paymentReservationWaitingId = undefined;
function onPaymentButtonClick(reservationWaitingId) {
  paymentReservationWaitingId = reservationWaitingId;
  document.getElementById('content-container').style.display = 'block';
  alert(`결제 준비 완료!\n예약 대기 아이디 : ${paymentReservationWaitingId}`);
}

function onReservationButtonClick(paymentWidget) {
  if (paymentReservationWaitingId === undefined) {
    alert("결제 대기 정보가 유효하지 않습니다.");
    return;
  }

  const generateRandomString = () => window.btoa(Math.random()).slice(0, 20);
  // TOSS 결제 위젯 Javascript SDK 연동 방식 중 'Promise로 처리하기'를 적용함
  // https://docs.tosspayments.com/reference/widget-sdk#promise%EB%A1%9C-%EC%B2%98%EB%A6%AC%ED%95%98%EA%B8%B0
  const orderIdPrefix = "kelly_daon";
  const orderId = orderIdPrefix + generateRandomString();
  const orderName = "테스트 방탈출 예약 결제 1건";
  const amount = 13500;

  // 1. 서버에 임시 저장 요청
  fetch("/payments/credentials", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({orderId, amount}),
  }).then(response => {
    if (!response.ok) {
      throw Error("서버에 결제 정보가 임시 저장되지 않음.")
    } else {
      console.log("결제 정보 임시 저장 성공");
    }
  }).catch(error => {
    console.error(error.message);
  });

  // 2. 결제 인증 요청
  paymentWidget.requestPayment({
    orderId,
    orderName,
    amount,
  }).then(function (data) {
    console.debug(data);
    // 3. 결제 승인 요청
    fetchReservationWaitingPaymentApprove(data, paymentReservationWaitingId);
  }).catch(function (error) {
    // TOSS 에러 처리: 에러 목록을 확인하세요
    // https://docs.tosspayments.com/reference/error-codes#failurl 로-전달되는-에러
    alert(error.code + " :" + error.message + "/ orderId : " + error.orderId);
  });
}

async function fetchReservationWaitingPaymentApprove(paymentData, reservationWaitingId) {
  const reservationWaitingPaymentApproveRequest = {
    reservationWaitingId,
    orderId: paymentData.orderId,
    amount: paymentData.amount,
    paymentKey: paymentData.paymentKey,
    paymentType: paymentData.paymentType,
  }

  const reservationWaitingPaymentApproveURL = "/reservation-waiting/approve";
  fetch(reservationWaitingPaymentApproveURL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(reservationWaitingPaymentApproveRequest),
  }).then(response => {
    if (!response.ok) {
      return response.json().then(errorBody => {
        console.error("예약 대기 결제 실패 : " + JSON.stringify(errorBody));
        window.alert(JSON.stringify(errorBody));
      });
    } else {
      response.json().then(successBody => {
        console.log("예약 대기 결제 성공 : " + JSON.stringify(successBody));
        window.location.reload();
      });
    }
  }).catch(error => {
    console.error(error.message);
  });
}
