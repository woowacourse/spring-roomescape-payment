document.addEventListener('DOMContentLoaded', () => {
  fetch('/reservations/me')
    .then(response => {
      if (response.status === 200) return response.json();
      throw new Error('Read failed');
    })
    .then(render)
    .catch(error => console.error('Error fetching reservations:', error));
});

function render(data) {
  const tableBody = document.getElementById('table-body');
  tableBody.innerHTML = '';

  data.forEach(item => {
    const row = tableBody.insertRow();

    const theme = item.theme;
    const date = item.date;
    const time = item.time;
    let status;

    if (item.payment == null) {
      status = item.waitingCount + '번째 예약 대기';
    } else if (item.payment.status === 'SUCCESS') {
      status = '예약';
    } else if (item.payment.status === 'PENDING') {
      status = '결제 대기';
    }

    row.insertCell(0).textContent = theme;
    row.insertCell(1).textContent = date;
    row.insertCell(2).textContent = time;
    row.insertCell(3).textContent = status;

    if (item.waitingCount > 0) { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능
      const cancelCell = row.insertCell(4);
      const cancelButton = document.createElement('button');
      cancelButton.textContent = '취소';
      cancelButton.className = 'btn btn-danger';
      cancelButton.onclick = function () {
        requestDeleteWaiting(item.id).then(() => window.location.reload());
      };
      cancelCell.appendChild(cancelButton);
      row.insertCell(5).textContent = '';
      row.insertCell(6).textContent = '';
    } else if (status === '예약') {
      row.insertCell(4).textContent = '';
      row.insertCell(5).textContent = item.payment.orderId;
      row.insertCell(6).textContent = item.payment.amount;
    } else if (status === '결제 대기') {
      data = {
        themeName: item.theme,
        paymentKey: item.payment.paymentKey,
        amount: item.payment.amount,
        orderId: item.payment.orderId
      };
      let btn = document.createElement('button');
      btn.className = 'btn btn-primary';
      btn.textContent = '결제하기';
      btn.onclick = function () {
        let modal = document.getElementById('payment-modal');
        modal.classList.remove('hide');
        modal.classList.add('show');
        modal.style.display = 'block';
        ready(data);
      };
      row.insertCell(4).appendChild(btn);
      row.insertCell(5).textContent = item.payment.orderId;
      row.insertCell(6).textContent = item.payment.amount;
    }
  });
}

function ready(data) {
  // ------  결제위젯 초기화 ------
  // @docs https://docs.tosspayments.com/reference/widget-sdk#sdk-설치-및-초기화
  // @docs https://docs.tosspayments.com/reference/widget-sdk#renderpaymentmethods선택자-결제-금액-옵션
  const paymentAmount = 0;
  const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
  const paymentWidget = PaymentWidget(widgetClientKey, PaymentWidget.ANONYMOUS);
  const renderedPaymentWidget = paymentWidget.renderPaymentMethods(
    "#payment-method",
    {value: paymentAmount},
    {variantKey: "DEFAULT"}
  );
  renderedPaymentWidget.updateAmount(data.amount);
  document.getElementById('reserve-confirm').addEventListener('click', function () {
    purchase(data, paymentWidget);
  });
}

function purchase(data, paymentWidget) {
  paymentWidget.requestPayment({
    orderId: data.orderId,
    orderName: data.themeName,
    amount: data.amount
  }).then(function (data) {
    console.debug(data);
    confirmPurchaseReservation(data);
  }).catch(function (error) {
    // TOSS 에러 처리: 에러 목록을 확인하세요
    // https://docs.tosspayments.com/reference/error-codes#failurl 로-전달되는-에러
    alert(error.message);
  });
}

async function confirmPurchaseReservation(paymentData) {
  const price = document.querySelector('.theme-slot.active')?.getAttribute('data-theme-price');
  const request = {
    amount: price,
    paymentKey: paymentData.paymentKey,
    orderId: paymentData.orderId
  }
  fetch('/payment',
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(paymentData)
    }).then(response => {
    if (!response.ok) {
      return response.json().then(errorBody => {
        console.error("결제 실패 : " + JSON.stringify(errorBody));
        window.alert("결제 실패 메시지");
      });
    } else {
      response.json().then(successBody => {
        console.log("결제 성공 : " + JSON.stringify(successBody));
        alert("결제가 완료되었습니다.");
        window.location.reload();
      });
    }
  })
}

function requestDeleteWaiting(id) {
  const endpoint = '/reservations/queue/' + id;
  return fetch(endpoint, {
    method: 'DELETE'
  }).then(response => {
    if (response.status === 204) return;
    throw new Error('Delete failed');
  });
}
