let isEditing = false;
const TIME_API_ENDPOINT = '/times';
const timesOptions = [];

document.addEventListener('DOMContentLoaded', () => {
    fetch('/admin/reservations/canceled') // 내 예약 목록 조회 API 호출
        .then(response => {
            if (response.status === 200) return response.json();
            throw new Error('Read failed');
        })
        .then(render)
        .catch(error => console.error('Error fetching reservations:', error));

    fetchTimes();
});

function render(data) {
    const tableBody = document.getElementById('table-body');
    tableBody.innerHTML = '';

    data.forEach(item => {
        const row = tableBody.insertRow();

        row.insertCell(0).textContent = item.id;
        row.insertCell(1).textContent = item.member.name;
        row.insertCell(2).textContent = item.theme.name;
        row.insertCell(3).textContent = item.date;
        row.insertCell(4).textContent = item.time.startAt;
        row.insertCell(5).textContent = item.status;
        row.insertCell(6).textContent = item.paymentKey;
        row.insertCell(7).textContent = item.totalAmount;
    });
}

function fetchTimes() {
    requestRead(TIME_API_ENDPOINT)
        .then(data => {
            timesOptions.push(...data);
        })
        .catch(error => console.error('Error fetching time:', error));
}

function requestRead(endpoint) {
    return fetch(endpoint)
        .then(response => {
            if (response.status === 200) return response.json();
            throw new Error('Read failed');
        });
}
