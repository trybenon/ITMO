// ===== ПЕРЕМЕННЫЕ И ИНИЦИАЛИЗАЦИЯ =====
let selectedX = null;
let selectedR = null;

// Загрузка при старте
document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('coordinateSVG').addEventListener('click', handleGraphClick);
});

// ===== ОБРАБОТЧИКИ СОБЫТИЙ =====

// Клик по графику
function handleGraphClick(event) {
    if (selectedR === null) {
        showError(document.getElementById('rForm'), "Для интерактивного выбора точки выберите R");
        return;
    }

    const svgCoords = getSVGCoordinates(event);
    const mathCoords = convertPixelToMath(svgCoords.x, svgCoords.y, 300, 300);
    document.getElementById('hiddenX').value = mathCoords.x;
    document.getElementById('hiddenY').value = mathCoords.y;
    document.getElementById('hiddenR').value = selectedR.value;
    document.getElementById('valForm').submit();
}

// Выбор значения X
document.getElementById('xForm').addEventListener('click', function(event){
    if (event.target.type === 'button' && event.target.name === 'x'){
        if (selectedX && event.target === selectedX.element){
            selectedX.element.style.backgroundColor = '#2e7d32';
            selectedX = null;
            return;
        }

        if (selectedX && selectedX.element) {
            selectedX.element.style.backgroundColor = '#2e7d32';
            selectedX.element.style.color = 'white';
        }

        event.target.style.backgroundColor = '#103711';
        event.target.style.color = 'white';
        selectedX = {
            value: event.target.value,
            element: event.target
        };
    }
});

// Выбор значения R
document.getElementById('rForm').addEventListener('click', function(event){
    if (event.target.type === 'button' && event.target.name === 'r'){

        if(selectedR && selectedR.element === event.target){
            selectedR.element.style.backgroundColor = '#2e7d32';
            selectedR = null;
            return;
        }

        if (selectedR && selectedR.element) {
            selectedR.element.style.backgroundColor = '#2e7d32';
            selectedR.element.style.color = 'white';
        }
        event.target.style.backgroundColor = '#103711';
        event.target.style.color = 'white';
        selectedR = {
            value: event.target.value,
            element: event.target
        };
    }
});

// Отправка формы
document.getElementById('valForm').addEventListener('submit', function (e) {
    let x = selectedX?.element;
    let y = document.getElementById('y');
    let r = selectedR?.element;

    if (!validate(x, y, r)) {
        e.preventDefault();
        return;
    }

        document.getElementById('hiddenX').value = x.value;
        document.getElementById('hiddenY').value = y.value;
        document.getElementById('hiddenR').value = r.value;

});

// ===== РАБОТА С ГРАФИКОМ =====

// Получение координат в системе SVG
function getSVGCoordinates(event) {
    const svg = document.getElementById("coordinateSVG");
    const point = svg.createSVGPoint();
    point.x = event.clientX;
    point.y = event.clientY;
    const ctm = svg.getScreenCTM().inverse();
    const svgPoint = point.matrixTransform(ctm);
    return { x: svgPoint.x, y: svgPoint.y };
}

// Конвертация пикселей в математические координаты
function convertPixelToMath(svgX, svgY, svgWidth, svgHeight) {
    const centerX = svgWidth / 2;
    const centerY = svgHeight / 2;
    const scale = 50 / selectedR.value;

    const mathX = (svgX - centerX) / scale;
    const mathY = (centerY - svgY) / scale;

    return {
        x: Math.round((mathX / 2) * 100) / 100,
        y: Math.round((mathY / 2) * 100) / 100
    };
}

// Конвертация математических координат в пиксели
function convertMathToPixel(mathX, mathY, rValue) {
    const centerX = 150;
    const centerY = 150;
    const scale = 50 / rValue;

    return {
        x: centerX + (mathX * 2) * scale,
        y: centerY - (mathY * 2) * scale
    };
}

// ===== ВАЛИДАЦИЯ И УТИЛИТЫ =====

// Валидация формы
function validate(x, y, r) {
    let isValid = true;

    if (!x) {
        showError(document.getElementById('xForm'), "Необходимо выбрать значение X");
        isValid = false;
    }

    if (!y.value.trim()) {
        showError(y, "Необходимо ввести значение Y");
        isValid = false;
    } else if (!/^-?\d+(\.\d+)?$/.test(y.value.trim())) {
        showError(y, "Y должно быть числом");
        isValid = false;
    } else {
        const yNum = parseFloat(y.value);
        if (yNum < -3 || yNum > 3 || isNaN(yNum)) {
            showError(y, "Y должно быть в диапазоне от -3 до 3");
            isValid = false;
        }
    }

    if (!r) {
        showError(document.getElementById('rForm'), "Необходимо выбрать значение R");
        isValid = false;
    }

    return isValid;
}

// Показать сообщение об ошибке
function showError(element, message) {
    const errorElement = document.createElement('div');
    errorElement.classList.add('error-message');
    errorElement.textContent = message;
    errorElement.style.color = 'red';
    errorElement.style.fontSize = '13px';
    errorElement.style.textAlign = 'center';
    element.parentNode.insertBefore(errorElement, element.nextSibling);
    setTimeout(() => errorElement.remove(), 3000);
}