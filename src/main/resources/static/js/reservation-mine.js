document.addEventListener('DOMContentLoaded', () => {
    fetch('/reservations/accounts') // 내 예약 목록 조회 API 호출
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

        const theme = item.themeName;
        const date = item.date;
        const time = item.startAt;
        const status = item.status;

        row.insertCell(0).textContent = theme;
        row.insertCell(1).textContent = date;
        row.insertCell(2).textContent = time;
        row.insertCell(3).textContent = status;

        if (status === '어드민 예약 완료' || status === '예약 완료') { // 예약 완료 상태일 때
            row.insertCell(4).textContent = '';
            row.insertCell(5).textContent = item.paymentKey;
            row.insertCell(6).textContent = item.amount;
        } else if (status === '결제 대기') {
            const paymentCell = row.insertCell(4);
            const paymentButton = document.createElement('button');
            paymentButton.textContent = '결제';
            paymentButton.className = 'btn btn-danger';
            paymentButton.onclick = function () {
                window.location.href = `/payment?reservationId=${item.id}`;
            };
            paymentCell.appendChild(paymentButton);
        } else { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능
            const cancelCell = row.insertCell(4);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '대기 취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteWaiting(item.waitingId).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);
        }
    });
}

function requestDeleteWaiting(id) {
    const endpoint = `/waitings/${id}`;
    return fetch(endpoint, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('Delete failed');
    });
}
