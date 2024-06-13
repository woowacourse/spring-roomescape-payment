document.addEventListener('DOMContentLoaded', () => {
  /*
  TODO: [2단계] 내 예약 목록 조회 기능
        endpoint 설정
   */
  fetch('/reservations/my') // 내 예약 목록 조회 API 호출
  .then(response => {
    if (response.status === 200) {
      return response.json();
    }
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

    row.insertCell(0).textContent = theme;
    row.insertCell(1).textContent = date;
    row.insertCell(2).textContent = time;
    row.insertCell(3).textContent = status;

    if (status !== '예약') {
      const cancelCell = row.insertCell(4);
      const cancelButton = document.createElement('button');
      cancelButton.textContent = '취소';
      cancelButton.className = 'btn btn-danger';
      cancelButton.onclick = function () {
        requestDelete(item.reservationId).then(() => window.location.reload());
      };
      cancelCell.appendChild(cancelButton);
    } else {
      row.insertCell(4).textContent = '';
      row.insertCell(5).textContent = item.paymentKey;
      row.insertCell(6).textContent = item.amount;
    }
  });
}

function requestDelete(id) {
  const endpoint = `/reservations/${id}`;
  return fetch(endpoint, {
    method: 'DELETE'
  }).then(response => {
    if (response.status === 204) {
      return;
    }
    throw new Error('Delete failed');
  });
}
