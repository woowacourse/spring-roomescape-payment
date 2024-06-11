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

        row.insertCell(0).textContent = theme;
        row.insertCell(1).textContent = date;
        row.insertCell(2).textContent = time;
        row.insertCell(3).textContent = status;

    if (status !== '예약') { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능
      const waitingCancelCell = row.insertCell(4);
      const cancelButton = document.createElement('button');
      cancelButton.textContent = '대기 취소';
      cancelButton.className = 'btn btn-danger';
      cancelButton.onclick = function () {
        requestDeleteWaiting(item.id).then(() => window.location.reload());
      };
      waitingCancelCell.appendChild(cancelButton);
      row.insertCell(5).textContent = '';
      row.insertCell(6).textContent = '';
    } else { // 예약 완료 상태일 때
      const reservationCancelCell = row.insertCell(4);
      const cancelButton = document.createElement('button');
      cancelButton.textContent = '예약 취소';
      cancelButton.className = 'btn btn-info';
      cancelButton.onclick = function () {
        requestDeleteReservation(item.id).then(() => window.location.reload());
      };
      reservationCancelCell.appendChild(cancelButton)
      row.insertCell(5).textContent = item.paymentKey;
      row.insertCell(6).textContent = item.amount;
    }
  });
}

function requestDeleteReservation(id) {
  const endpoint = `/reservations/`;
  return fetch(endpoint + id, {
    method: 'DELETE'
  }).then(response => {
    if (response.status === 204) return;
    throw new Error('Delete failed');
  });
}

function requestDeleteWaiting(id) {
    const endpoint = `/waitings/`;
    return fetch(endpoint + id, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('Delete failed');
    });
}
