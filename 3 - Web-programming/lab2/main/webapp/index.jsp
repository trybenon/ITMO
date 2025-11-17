<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="util.SessionStorage" %>
<%@ page import="model.Result" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
    <meta charset="UTF-8">
    <title>lab2</title>
</head>
<body>
<header class="header">
    <h2>ФИО: Рыжаков Владислав Александрович</h2>
    <h3>Группа: P3223</h3>
    <h3>Номер варианта: 2498</h3>
</header>
<main>
    <div class="form-block">
        <div class="graph" id="graphContainer" >
            <svg viewBox="0 0 300 300" width="400" height="400" xmlns="http://www.w3.org/2000/svg" id="coordinateSVG">

                <!-- Оси -->
                <line x1="0" x2="300" y1="150" y2="150" stroke="black"></line>
                <line x1="150" x2="150" y1="0" y2="300" stroke="black"></line>

                <!-- Стрелки -->
                <polygon points="150,0 145,15 155,15" fill="black"></polygon>
                <polygon points="300,150 285,145 285,155" fill="black"></polygon>

                <!-- Синяя область -->
                <!-- Прямоугольник (верх слева) -->
                <rect x="150" y="50" width="50" height="100" fill="#005108" fill-opacity="0.6"></rect>

                <!-- Треугольник (верх справа) -->
                <polygon points="150,100 50,150 150,150" fill="#005108" fill-opacity="0.6"></polygon>

                <!-- Четверть круга (низ справа) -->
                <path d="M150 150 L200 150 A50 50 0 0 1 150 200 Z" fill="#005108" fill-opacity="0.6"></path>

                <!-- Засечки по оси X -->
                <line x1="200" x2="200" y1="145" y2="155" stroke="black"></line>
                <line x1="250" x2="250" y1="145" y2="155" stroke="black"></line>
                <line x1="100" x2="100" y1="145" y2="155" stroke="black"></line>
                <line x1="50" x2="50" y1="145" y2="155" stroke="black"></line>

                <!-- Засечки по оси Y -->
                <line x1="145" x2="155" y1="100" y2="100" stroke="black"></line>
                <line x1="145" x2="155" y1="50" y2="50" stroke="black"></line>
                <line x1="145" x2="155" y1="200" y2="200" stroke="black"></line>
                <line x1="145" x2="155" y1="250" y2="250" stroke="black"></line>

                <!-- Подписи -->
                <text x="195" y="140">R/2</text>
                <text x="245" y="140">R</text>
                <text x="90" y="140">-R/2</text>
                <text x="40" y="140">-R</text>

                <text x="160" y="105">R/2</text>
                <text x="160" y="55">R</text>
                <text x="160" y="205">-R/2</text>
                <text x="160" y="255">-R</text>

                <text x="160" y="15">Y</text>
                <text x="285" y="140">X</text>
                <%
                    SessionStorage storage = (SessionStorage) session.getAttribute("sessionStorage");
                    if (storage != null) {
                        for (Result res : storage.getResults()) {
                            double scale = 50 / res.getR();
                            double pxX = 150 + (res.getX() * scale * 2);
                            double pxY = 150 - (res.getY() * scale * 2);
                %>
                <circle cx="<%= pxX %>" cy="<%= pxY %>" r="3" fill="<%= res.isSuccess() ? "green" : "red" %>" stroke="black"></circle>
                <% }} %>
            </svg>
        </div>


        <form id="valForm" method="GET" action="${pageContext.request.contextPath}/controller">
            <div id="xForm" >
                <label>Выберите X:</label><br>
                <input type="button" name="x" value="-3">
                <input type="button" name="x" value="-2">
                <input type="button" name="x" value="-1">
                <input type="button" name="x" value="0">
                <input type="button" name="x" value="1">
                <input type="button" name="x" value="2">
                <input type="button" name="x" value="3">
                <input type="button" name="x" value="4">
                <input type="button" name="x" value="5">
            </div>
            <div id="yForm" >
                <label>Введите Y: <input type="text" id="y" placeholder="Число от -3 до 3"></label>
            </div>
            <div id="rForm">
                <label>Выберите R:</label><br>
                <input type="button" name="r" value="1">
                <input type="button" name="r" value="1.5">
                <input type="button" name="r" value="2">
                <input type="button" name="r" value="2.5">
            </div>

            <input type="hidden" id="hiddenX" name="x">
            <input type="hidden" id="hiddenY" name="y">
            <input type="hidden" id="hiddenR" name="r">
            <input type="hidden" id="operation" name="operation">
            <input type="submit" value="Отправить"><br><br>
            <input type="button" id="clearTableButton" value="Очистить таблицу"
                   onclick="
                           document.getElementById('operation').value = 'clear';
                           document.getElementById('valForm').submit();
                           ">


        </form>
    </div>
    <table id="resultTable">
        <tr><th>Результат</th><th>X</th><th>Y</th><th>R</th><th>Текущее время</th><th>Время работы</th></tr>
        <%
            if (storage != null) {
                List<Result> results = storage.getResults();
                for (Result res : results) {
        %>
        <tr>
            <td><%= res.isSuccess() ? "Попадание" : "Промах" %></td>
            <td><%= res.getX() %></td>
            <td><%= res.getY() %></td>
            <td><%= res.getR() %></td>
            <td><%= res.getCurrent_time() %></td>
            <td><%= res.getExecution_time() %> мс</td>
        </tr>
        <% }} %>
    </table>
</main>
</body>
<script src="${pageContext.request.contextPath}/script.js"></script>
</html>