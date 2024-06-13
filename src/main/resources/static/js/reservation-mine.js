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

    const cancelCell = row.insertCell(4);
    const cancelButton = document.createElement('button');
    cancelButton.textContent = '취소';
    cancelButton.className = 'btn btn-danger';

    if (status !== '예약') { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능
      cancelButton.onclick = function () {
        requestDeleteWaiting(item.reservationId).then(() => window.location.reload());
      };
    } else { // 예약 완료 상태일 때
      console.log(item.amount);
      row.insertCell(5).textContent = item.paymentKey;
      row.insertCell(6).textContent = item.amount;
      cancelButton.onclick = function () {
        /*
        TODO: [미션4 - 2단계] 내 예약 목록 조회 시,
         예약 완료 상태일 때 결제 정보를 함께 보여주기
         결제 정보 필드명은 자신의 response 에 맞게 변경하기
        */
        requestDeleteReservation(item.reservationId).then(() => window.location.reload());
      };
    }
    cancelCell.appendChild(cancelButton);
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
  const endpoint = `/reservations/waiting/${id}`;
  return fetch(endpoint, {
    method: 'DELETE'
  }).then(response => {
    if (response.status === 204) return;
    throw new Error('Delete failed');
  });
}
