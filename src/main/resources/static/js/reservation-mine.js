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
    const rank = item.rank;

    let status;
    if (item.status === 'RESERVED') {
      status = '예약';
    } else {
      if (item.rank === 0) {
        status = '결제대기';
      } else {
        status = rank + '번째 예약대기';
      }
    }

    row.insertCell(0).textContent = theme;
    row.insertCell(1).textContent = date;
    row.insertCell(2).textContent = time;
    row.insertCell(3).textContent = status;
    row.insertCell(4).textContent = item.paymentKey;
    row.insertCell(5).textContent = item.amount;
    row.insertCell(6).textContent = item.payMethod;
    row.insertCell(7).textContent = '';
    row.insertCell(8).textContent = '';

    if (item.status === 'STANDBY') {
      // 예약대기 상태
      const cancelCell = row.insertCell(8);
      const cancelButton = document.createElement('button');
      cancelButton.textContent = '대기취소';
      cancelButton.className = 'btn btn-danger';
      cancelButton.onclick = function () {
        requestDeleteWaiting(item.id).then(() => window.location.reload());
      };
      cancelCell.appendChild(cancelButton);
    } else {
      // 예약 완료 상태
      row.insertCell(8).textContent = '';
    }

    if (item.status === 'STANDBY' && item.rank === 0) {
      // 결제대기 상태
      const paySell = row.insertCell(7);
      const payButton = document.createElement('button');
      payButton.textContent = '결제';
      payButton.className = 'btn btn-primary';
      payButton.onclick = function () {
        window.open("payment?id=" + item.id, "결제 창", "width=900, height=600, location=no");
      };
      paySell.appendChild(payButton);
    }
  });
}

function requestDeleteWaiting(id) {
  const endpoint = '/reservations/standby/' + id;
  return fetch(endpoint, {
    method: 'DELETE'
  }).then(response => {
    if (response.status === 204) return;
    throw new Error('Delete failed');
  });
}
