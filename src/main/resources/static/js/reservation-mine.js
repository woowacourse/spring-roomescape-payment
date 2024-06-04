document.addEventListener('DOMContentLoaded', () => {
    /*
    TODO: [2단계] 내 예약 목록 조회 기능
          endpoint 설정
     */
    Promise.all([
        fetch('/reservations/mine').then(response => {
            if (!response.ok) throw new Error('Failed to fetch reservations');
            return response.json();
        }),
        fetch('/reservation-waitings/mine').then(response => {
            if (!response.ok) throw new Error('Failed to fetch waitings');
            return response.json();
        })
    ]).then(([reservations, waitings]) => {
        reservations.forEach(reservation => {
            if (reservation.payment.id) {
                reservation.status = "예약";
            } else {
                reservation.status = "결제대기";
            }
        });
        waitings.forEach(waiting => {
            if (waiting.deniedAt) {
                waiting.status = "예약대기 거절";
            } else {
                waiting.status = (waiting.order) + "번째 예약대기";
            }
        });
        const combinedData = [...reservations, ...waitings];
        render(combinedData);
    }).catch(error => console.error('Error fetching data:', error));
});

function render(data) {
    const tableBody = document.getElementById('table-body');
    tableBody.innerHTML = '';

    data.forEach(item => {
        const row = tableBody.insertRow();

        /*
        TODO: [2단계] 내 예약 목록 조회 기능
              response 명세에 맞춰 값 설정
         */
        const theme = item.theme.name;
        const date = item.date;
        const time = item.time.startAt;
        const status = item.status;

        row.insertCell(0).textContent = theme;
        row.insertCell(1).textContent = date;
        row.insertCell(2).textContent = time;
        row.insertCell(3).textContent = status;

        /*
        TODO: [3단계] 예약 대기 기능 - 예약 대기 취소 기능 구현 후 활성화
         */
        if (status === '예약') {
            const payment = item.payment;
            row.insertCell(4).textContent = '';
            row.insertCell(5).textContent = payment.paymentKey;
            row.insertCell(6).textContent = payment.totalAmount;
        } else if (status === '결제대기') {
            row.insertCell(4).textContent = '';
            row.insertCell(5).textContent = '';
            row.insertCell(6).textContent = '';
        } else {
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
        }
    });
}

function requestDeleteWaiting(id) {
    /*
    TODO: [3단계] 예약 대기 기능 - 예약 대기 취소 API 호출
     */
    const endpoint = `/reservation-waitings/${id}`;
    return fetch(endpoint, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('Delete failed');
    });
}
