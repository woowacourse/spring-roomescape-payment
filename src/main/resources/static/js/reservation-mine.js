document.addEventListener('DOMContentLoaded', () => {
    /*
    TODO: [2단계] 내 예약 목록 조회 기능
          endpoint 설정
     */
    fetch('/reservations/my') // 내 예약 목록 조회 API 호출
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

        /*
        TODO: [2단계] 내 예약 목록 조회 기능
              response 명세에 맞춰 값 설정
         */
        const theme = item.theme;
        const date = item.date;
        const time = item.time;
        const status = item.status;

        row.insertCell(0).textContent = theme;
        row.insertCell(1).textContent = date;
        row.insertCell(2).textContent = time;
        row.insertCell(3).textContent = status;

        /*
        TODO: [3단계] 예약 대기 기능 - 예약 대기 취소 기능 구현 후 활성화
         */
        const cancelCell = row.insertCell(4);
        const cancelButton = document.createElement('button');
        cancelButton.className = 'btn btn-danger';

        const payCell = row.insertCell(5);
        const payButton = document.createElement('button');
        payButton.className = 'btn btn-danger';


        if (status !== '예약') {
            cancelButton.textContent = '대기 취소';
            cancelButton.onclick = function () {
                requestDelete(item.reservationId).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);

            if (status === '결제 대기') {
                payButton.textContent = '결제';
                payButton.onclick = function () {
                    //requestDelete(item.reservationId).then(() => window.location.reload());
                    //결제 메서드 연결
                };
                payCell.appendChild(payButton);
            }
        }
        else{
            cancelButton.textContent = '예약 취소';
            cancelButton.onclick = function () {
                requestDelete(item.reservationId).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);

                /*
                TODO: [미션4 - 2단계] 내 예약 목록 조회 시,
                    예약 완료 상태일 때 결제 정보를 함께 보여주기
                    결제 정보 필드명은 자신의 response 에 맞게 변경하기
                */
            row.insertCell(6).textContent = item.paymentKey;
            row.insertCell(7).textContent = item.amount;
        }
    });
}

function requestDelete(id) {
    /*
    TODO: [3단계] 예약 대기 기능 - 예약 대기 취소 API 호출
     */
    const endpoint = `/reservations/${id}`;
    return fetch(endpoint, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('Delete failed');
    });
}
