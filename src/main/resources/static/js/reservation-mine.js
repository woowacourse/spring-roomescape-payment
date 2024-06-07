document.addEventListener('DOMContentLoaded', () => {
  fetch('/reservations/mine') // 내 예약 목록 조회 API 호출
      .then(response => {
        if (response.status === 200) return response.json();
        throw new Error('Read failed');
      })
      .then(renderMyReservation)
      .catch(error => console.error('Error fetching reservations:', error));

  fetch('/reservations/waiting/mine') // 내 예약 대기 목록 조회 API 호출
      .then(response => {
        if (response.status === 200) return response.json();
        throw new Error('Read failed');
      })
      .then(renderMyWaiting)
      .catch(error => console.error('Error fetching reservations:', error));
});

function renderMyReservation(data) {
  const tableBody = document.getElementById('my-reservation-table-body');
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
    cancelButton.onclick = function () {
      requestDeleteReservation(item.reservationId).then(() => window.location.reload());
    };
    if (item.paymentKey == null) {
      const paymentCell = row.insertCell(5);
      const paymentButton = document.createElement('button');
      paymentButton.textContent = '결제';
      paymentButton.className = 'btn btn-primary';
      paymentButton.onclick = function () {
        window.location.href = `/payment?reservationId=${item.reservationId}`;
      };
      paymentCell.appendChild(paymentButton);
    }else {
      row.insertCell(5).textContent = item.paymentKey;
      row.insertCell(6).textContent = item.amount;
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

function renderMyWaiting(data) {
  const tableBody = document.getElementById('my-waiting-table-body');
  tableBody.innerHTML = '';

  data.forEach(item => {
    const row = tableBody.insertRow();
    const theme = item.theme;
    const date = item.date;
    const time = item.time;
    const priority = item.priority;

    row.insertCell(0).textContent = theme;
    row.insertCell(1).textContent = date;
    row.insertCell(2).textContent = time;
    row.insertCell(3).textContent = `${priority}번째 예약 대기`;

    const cancelCell = row.insertCell(4);
    const cancelButton = document.createElement('button');
    cancelButton.textContent = '취소';
    cancelButton.className = 'btn btn-danger';

    cancelButton.onclick = function () {
      requestDeleteWaiting(item.reservationId).then(() => window.location.reload());
    };

    cancelCell.appendChild(cancelButton);
  });
}
