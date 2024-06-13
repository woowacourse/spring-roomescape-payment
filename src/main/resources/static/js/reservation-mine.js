document.addEventListener('DOMContentLoaded', () => {
  fetch('/reservations/mine') // 내 예약 목록 조회 API 호출
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
    const status = item.status;
    const paymentKey = item.paymentKey;
    const amount = item.amount;

    row.insertCell(0).textContent = theme;
    row.insertCell(1).textContent = date;
    row.insertCell(2).textContent = time;
    row.insertCell(3).textContent = status;
    const buttonCell = row.insertCell(4);
    row.insertCell(5).textContent = paymentKey;
    row.insertCell(6).textContent = amount;

    if (status == '결제대기') {
      const paymentButton = document.createElement('button');
      paymentButton.textContent = '결제';
      paymentButton.className = 'btn btn-primary';
      paymentButton.onclick = function () {
        requestPayment(item.id);
      };
      buttonCell.appendChild(paymentButton);
    }

    if (status !== '예약') { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능
      const cancelButton = document.createElement('button');
      cancelButton.textContent = '취소';
      cancelButton.className = 'btn btn-danger';
      cancelButton.onclick = function () {
        requestDeleteWaiting(item.id).then(() => window.location.reload());
      };
      buttonCell.appendChild(cancelButton);
    }
  });
}

async function requestPayment(reservationId) {
   window.location.href = `/payment?reservationId=${reservationId}`;
}

function requestDeleteWaiting(id) {
  const endpoint = `/reservations/${id}`;
  return fetch(endpoint, {
    method: 'DELETE'
  }).then(response => {
    if (response.status === 204) return;
    throw new Error('Delete failed');
  });
}
