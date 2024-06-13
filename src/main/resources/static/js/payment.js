document.addEventListener('DOMContentLoaded', () => {
  fetch('/payments') // 결제 목록 조회 API 호출
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

  data.resources.forEach(item => {
    const row = tableBody.insertRow();

    const id = item.id;
    const paymentKey = item.paymentKey;
    const orderId = item.orderId;
    const amount = item.amount;
    const reservationId = item.reservationId;

    row.insertCell(0).textContent = id;                 // 결제 id
    row.insertCell(1).textContent = paymentKey;         // 결제 key
    row.insertCell(2).textContent = orderId;            // 주문 id
    row.insertCell(3).textContent = amount;             // 금액
    row.insertCell(4).textContent = reservationId;      // 예약 id
  });
}
