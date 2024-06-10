const RESERVATION_API_ENDPOINT = '/reservations'

document.addEventListener('DOMContentLoaded', () => {
    fetch('/reservations/mine') // 내 예약 목록 조회 API 호출
        .then(response => {
            if (response.status === 200) return response.json();
            throw new Error('Read failed');
        })
        .then(data => render(data.responses))
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

        if (status === '대기') {
            row.insertCell(3).textContent = `${item.rank}번째 예약대기`;
        } else {
            row.insertCell(3).textContent = status;
        }

        if (status === '대기') {
            row.insertCell(4).textContent = item.paymentKey;
            row.insertCell(5).textContent = item.amount;

            const cancelCell = row.insertCell(6);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '대기 취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteWaiting(item.reservationId).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);
        } else if (status === '예약') { // 예약 완료 상태일 때
            /*
            TODO: [미션4 - 2단계] 내 예약 목록 조회 시,
            예약 완료 상태일 때 결제 정보를 함께 보여주기
            결제 정보 필드명은 자신의 response 에 맞게 변경하기
            */
            row.insertCell(4).textContent = item.paymentKey;
            row.insertCell(5).textContent = item.amount;

            const cancelCell = row.insertCell(6);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '예약 취소';
            cancelButton.className = 'btn btn-primary';
            cancelButton.onclick = function () {
                requestDelete(item.reservationId).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);
        } else if (status === '결제대기') {
            row.insertCell(4).textContent = '';
            row.insertCell(5).textContent = '';

            const confirmPayment = row.insertCell(6);
            const confirmPaymentButton = document.createElement('button');
            confirmPaymentButton.textContent = '결제하기';
            confirmPaymentButton.className = 'btn btn-primary';
            confirmPaymentButton.onclick = function () {
                // requestDelete(item.reservationId).then(() => window.location.reload());
            };
            confirmPayment.appendChild(cancelButton);
        } else {
            row.insertCell(4).textContent = '';
            row.insertCell(5).textContent = '';
        }
    });
}

function requestDeleteWaiting(id) {
    const endpoint = `${RESERVATION_API_ENDPOINT}/${id}`;
    return fetch(endpoint, {
        method: 'DELETE'
    }).then(response => {
        if (!response.ok) {
            return response.text().then(errorResponse => {
                throw new Error(errorResponse);
            })
        }
        alert("예약 대기가 취소 되었습니다.")
    }).catch(error => {
        alert(error.message);
    });
}

function requestDelete(id) {
    const endpoint = `${RESERVATION_API_ENDPOINT}/${id}`;

    const data = {
        cancelReason: "단순 변심"
    };

    return fetch(endpoint, {
        method: 'PATCH',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(data)
    }).then(response => {
        if (!response.ok) {
            return response.text().then(errorResponse => {
                throw new Error(errorResponse);
            })
        }
        alert("예약이 취소 되었습니다.")
    }).catch(error => {
        alert(error.message);
    });
}
