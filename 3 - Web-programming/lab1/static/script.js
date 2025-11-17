const hitSound = new Audio("hit.mp3");
const missSound = new Audio("miss.mp3");

document.getElementById('valForm').addEventListener('submit', function (e) {
    e.preventDefault();
    let x = document.querySelector('input[name = "x"]:checked');
    let y = document.getElementById('y');
    let r = document.getElementById('r');
    if (!validate(x, y, r)) {
        return false
    } else fetch(`http://localhost:26534/fcgi-bin/server-1.0.jar?x=${x.value}&y=${y.value}&r=${r.value}`, {
        method: 'GET'
    }).then(response => {
        if (!response.ok) {
            throw new Error(`${response.status}`)
        }
        return response.text();
    }).then(text => {
        console.log("Raw response:", text);
            try {
                const data = JSON.parse(text);
                console.log("Data:", data);
                let table = document.getElementById("resultTable");
                if (!table) {
                    table = document.createElement("table");
                    table.id = "resultTable";



                    let header = table.insertRow();
                    ["Результат", "X", "Y", "R", "Текущее время", "Время работы"].forEach(title => {
                        let th = document.createElement("th");
                        th.textContent = title;
                        header.appendChild(th);
                    });

                    document.body.appendChild(table);
                }

                let row = table.insertRow();
                row.insertCell().textContent = data.result ? "Попадание" : "Промах";
                row.insertCell().textContent = data.x;
                row.insertCell().textContent = data.y;
                row.insertCell().textContent = data.r;
                row.insertCell().textContent = data.current_time;
                row.insertCell().textContent = data.execution_time;

                if (data.result){
                    hitSound.play()
                } else {
                    missSound.play()
                }

            } catch (e) {
                console.error("JSON parse error:", e);
            }
    })
});


function showError(element, message) {
    const errorElement = document.createElement('div')
    errorElement.classList.add('error-message');
    errorElement.textContent = message;
    errorElement.style.color = 'red';
    errorElement.style.fontSize = '13px'
    errorElement.style.textAlign = 'center'
    element.parentNode.insertBefore(errorElement, element.nextSibling);
    setTimeout(function () {
        errorElement.remove();
    }, 3000);
}

function validate(x, y, r) {
    let isValid = true;


    if (x === null) {
        showError(document.getElementById('xForm'), "Необходимо выбрать значение координаты X");
        isValid = false;
    }


    if (!y.value.trim()) {
        showError(y, "Необходимо ввести значение координаты Y");
        isValid = false;
    } else if (!/^-?\d+(\.\d+)?$/.test(y.value.trim())) {
        showError(y, "Координата Y должна быть числом");
        isValid = false;
    } else {
        const yNum = parseFloat(y.value);
        if (yNum < -5 || yNum > 5 || isNaN(yNum)) {
            showError(y, "Координата Y должна быть в диапазоне от -5 до 5");
            isValid = false;
        }
    }


    if (!r.value.trim()) {
        showError(r, "Необходимо ввести значение координаты R");
        isValid = false;
    } else if (!/^-?\d+(\.\d+)?$/.test(r.value.trim())) {
        showError(r, "Координата R должна быть числом");
        isValid = false;
    } else {
        const rNum = parseFloat(r.value);
        if (rNum < 1 || rNum > 4 || isNaN(rNum)) {
            showError(r, "Координата R должна быть в диапазоне от 1 до 4");
            isValid = false;
        }
    }

    return isValid;
}