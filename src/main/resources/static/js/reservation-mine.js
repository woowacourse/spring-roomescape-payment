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
            reservation.status = "예약";
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
        if (status !== '예약') { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능
            const cancelCell = row.insertCell(4);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteWaiting(item.id).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);
        } else { // 예약 완료 상태일 때
            /*
TODO: [미션4 - 2단계] 내 예약 목록 조회 시,
예약 완료 상태일 때 결제 정보를 함께 보여주기
결제 정보 필드명은 자신의 response 에 맞게 변경하기
*/
            const payment = item.payment;
            row.insertCell(4).textContent = '';
            if (payment === null) {
                row.insertCell(5).textContent = '';
                row.insertCell(6).textContent = '0';
                return;
            }
            row.insertCell(5).textContent = payment.paymentKey;
            row.insertCell(6).textContent = payment.amount;
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
