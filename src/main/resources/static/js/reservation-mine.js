document.addEventListener('DOMContentLoaded', () => {
    fetch('/reservations-mine') // 내 예약 목록 조회 API 호출
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

    data.reservations.forEach(item => {
        const row = tableBody.insertRow();
        var index = 0
        const theme = item.theme;
        const date = item.date;
        const time = item.time;
        const status = item.status;
        const paymentKey = item.paymentKey;
        const amount = item.amount;

        row.insertCell(index++).textContent = theme;
        row.insertCell(index++).textContent = date;
        row.insertCell(index++).textContent = time;
        row.insertCell(index++).textContent = status;
        row.insertCell(index++).textContent = paymentKey;
        row.insertCell(index++).textContent = amount;

        if (status === '결제 대기') {
            const paymentCell = row.insertCell(index++);
            const paymentButton = document.createElement('button');
            paymentButton.textContent = '결제';
            paymentButton.className = 'btn btn-primary';
            paymentButton.onclick = function () {
                // TODO: Toss 결제창 열리게 만들기
            }
            paymentCell.appendChild(paymentButton);
        } else if (status !== '예약') { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능
            const cancelCell = row.insertCell(index++);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteWaiting(item.reservationId).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);
        } else { // 예약 완료 상태일 때
            row.insertCell(index++).textContent = '';
        }
    });
}

function requestDeleteWaiting(id) {
    const endpoint = `/reservations/${id}/waitings`;
    return fetch(endpoint, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('Delete failed');
    });
}