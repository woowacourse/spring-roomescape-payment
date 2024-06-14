document.addEventListener('DOMContentLoaded', () => {
    fetch('reservations-waiting') // 내 예약 목록 조회 API 호출
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

        const id = item.id;
        const waitingNumber = item.waitingNumber;
        const name = item.member;
        const theme = item.theme;
        const date = item.date;
        const startAt = item.time;

        row.insertCell(0).textContent = id;            // 예약 id
        row.insertCell(1).textContent = waitingNumber;            // 예약 대기 id
        row.insertCell(2).textContent = name;          // 예약자명
        row.insertCell(3).textContent = theme;         // 테마명
        row.insertCell(4).textContent = date;          // 예약 날짜
        row.insertCell(5).textContent = startAt;       // 시작 시간

        const actionCell = row.insertCell(row.cells.length);

        // actionCell.appendChild(createActionButton('승인', 'btn-primary', approve));
        actionCell.appendChild(createActionButton('거절', 'btn-danger', deny));
    });
}

function approve(event) {
    const row = event.target.closest('tr');
    const id = row.cells[0].textContent;

    const endpoint = '' + id;
    return fetch(endpoint, {
        method: ''
    }).then(response => {
        if (response.status === 200) return;
        throw new Error('Delete failed');
    }).then(() => location.reload());
}

function deny(event) {
    const row = event.target.closest('tr');
    const id = row.cells[0].textContent;

    const endpoint = `/admin/reservations-waiting/${id}`;
    return fetch(endpoint, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('Delete failed');
    }).then(() => location.reload());
}

function createActionButton(label, className, eventListener) {
    const button = document.createElement('button');
    button.textContent = label;
    button.classList.add('btn', className, 'mr-2');
    button.addEventListener('click', eventListener);
    return button;
}
