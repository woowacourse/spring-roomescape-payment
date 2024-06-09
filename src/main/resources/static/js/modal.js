var modal = document.getElementById("payment-modal");
var btn = document.getElementById("reserve-button");
var span = document.getElementsByClassName("close")[0];

btn.onclick = function() {
    modal.classList.remove('hide');
    modal.classList.add('show');
    modal.style.display = "block";
}

span.onclick = function() {
    modal.classList.remove('show');
    modal.classList.add('hide');
    setTimeout(() => {
        modal.style.display = "none";
    }, 400);
}

window.onclick = function(event) {
    if (event.target === modal) {
        modal.classList.remove('show');
        modal.classList.add('hide');
        setTimeout(() => {
            modal.style.display = "none";
        }, 400);
    }
}
