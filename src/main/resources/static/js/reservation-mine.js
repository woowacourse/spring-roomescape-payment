document.addEventListener('DOMContentLoaded', () => {
    const tableBody = document.getElementById('table-body');
    // 테이블 초기화(한 번만 비우기)
    tableBody.innerHTML = '';

    // 두 개의 fetch를 Promise.all로 병렬 호출
    Promise.all([
        fetch('/reservations-mine').then(response => {
            if (response.status === 200) return response.json();
            throw new Error('Read reservations failed');
        }),
        fetch('/waitings-mine').then(response => {
            if (response.status === 200) return response.json();
            throw new Error('Read waitings failed');
        })
    ])
        .then(([reservationsData, waitingsData]) => {
            renderReservation(reservationsData);
            renderWaitings(waitingsData);
        })
        .catch(error => console.error('Error fetching data:', error));
});

function renderReservation(data) {
    const tableBody = document.getElementById('table-body');
    tableBody.innerHTML = '';

    data.forEach(item => {
        const row = tableBody.insertRow();

        const theme = item.myReservationResponse.theme;
        const date = item.myReservationResponse.date;
        const time = item.myReservationResponse.time;
        const status = item.myReservationResponse.status;
        const isWaiting = item.myReservationResponse.isWaiting;

        row.insertCell(0).textContent = theme;
        row.insertCell(0).textContent = theme;
        row.insertCell(1).textContent = date;
        row.insertCell(2).textContent = time;
        row.insertCell(3).textContent = status;

        if (isWaiting) { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능
            const cancelCell = row.insertCell(4);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteWaiting(item.id).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);
        } else { // 예약 완료 상태일 때
            row.insertCell(4).textContent = '';
            row.insertCell(5).textContent = item.paymentResponse.paymentKey;
            row.insertCell(6).textContent = item.paymentResponse.totalAmount;
        }
    });
}

function renderWaitings(data) {
    const tableBody = document.getElementById('table-body');
    tableBody.innerHTML = '';

    data.forEach(item => {
        const row = tableBody.insertRow();

        const theme = item.theme;
        const date = item.date;
        const time = item.time;
        const status = item.status;
        const isWaiting = item.isWaiting;

        row.insertCell(0).textContent = theme;
        row.insertCell(1).textContent = date;
        row.insertCell(2).textContent = time;
        row.insertCell(3).textContent = status;

        if (isWaiting) { // 예약 대기 상태일 때 예약 대기 취소 버튼 추가하는 코드, 상태 값은 변경 가능
            const cancelCell = row.insertCell(4);
            const cancelButton = document.createElement('button');
            cancelButton.textContent = '취소';
            cancelButton.className = 'btn btn-danger';
            cancelButton.onclick = function () {
                requestDeleteWaiting(item.id).then(() => window.location.reload());
            };
            cancelCell.appendChild(cancelButton);
        } else { // 예약 완료 상태일 때
            row.insertCell(4).textContent = '';
            row.insertCell(5).textContent = item.paymentKey;
            row.insertCell(6).textContent = item.totalAmount;
        }
    });
}

function requestDeleteWaiting(id) {
    const endpoint = `/reservations/waitings/${id}`;
    return fetch(endpoint, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('Delete failed');
    });
}
