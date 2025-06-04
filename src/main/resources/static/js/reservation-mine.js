document.addEventListener('DOMContentLoaded', () => {
    fetch('/mypage/registrations') // 내 예약 목록 조회 API 호출
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
        let reservationStatus = item.reservationStatus;
        if (reservationStatus === "예약 대기") {
            reservationStatus = `${item.rank}번째 ${item.reservationStatus}`;
        }
        const paymentStatus = item.paymentStatus;

        row.insertCell(0).textContent = theme;
        row.insertCell(1).textContent = date;
        row.insertCell(2).textContent = time;
        row.insertCell(3).textContent = reservationStatus;

        if (reservationStatus.includes('예약 대기')) { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능
            const cancelCell = row.insertCell(4);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteWaiting(item.reservationId).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);
        } else { // 예약 완료 상태일 때상
            row.insertCell(4).textContent = '';
            row.insertCell(5).textContent = paymentStatus;
            const payCell = row.insertCell(6);
            if(paymentStatus === '결제 전') {
                const payButton = document.createElement('button');
                payButton.textContent = '결제';
                payButton.className = 'btn btn-primary';
                payButton.onclick = function () {
                    redirectToPaymentPage(item.reservationId).then(() => window.location.reload());
                };
                payCell.appendChild(payButton);
            } else {
                row.insertCell(6).textContent = '';
            }
            row.insertCell(7).textContent = item.paymentKey;
            row.insertCell(8).textContent = item.amount;
        }
    });
}

function requestDeleteWaiting(id) {
    const endpoint = `/mypage/waitings/${id}`;
    return fetch(endpoint, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('Delete failed');
    });
}

function redirectToPaymentPage(reservationId) {
    window.location.href = `/mypage/${reservationId}/payment`;
}
