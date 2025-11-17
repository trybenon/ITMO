<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="util.SessionStorage, model.Result, java.util.List" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Результаты проверки</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
<header class="header">
    <h2>Результаты проверки точек</h2>
</header>

<main>
    <div class="graph">
        <div class="current-result">
            <h3>Текущий результат</h3>
            <%
                SessionStorage storage = (SessionStorage) session.getAttribute("sessionStorage");
                if (storage != null && !storage.getResults().isEmpty()) {
                    Result last = storage.getResults().get(storage.getResults().size() - 1);
            %>
            <table id="resultTable">
                <tr><th>X</th><th>Y</th><th>R</th><th>Результат</th><th>Время</th><th>Время выполнения</th></tr>
                <tr>
                    <td><%= last.getX() %></td>
                    <td><%= last.getY() %></td>
                    <td><%= last.getR() %></td>
                    <td><%= last.isSuccess() ? "Попадание" : "Промах" %></td>
                    <td><%= last.getCurrent_time() %></td>
                    <td><%= last.getExecution_time() %> мс</td>
                </tr>
            </table>
            <% } else { %>
            <p class="no-results">Результатов пока нет.</p>
            <% } %>
        </div>

        <h3>История всех результатов</h3>
        <%
            if (storage != null && !storage.getResults().isEmpty()) {
        %>
        <table id="resultTable">
            <tr><th>X</th><th>Y</th><th>R</th><th>Результат</th><th>Время</th><th>Время выполнения</th></tr>
            <%
                for (Result res : storage.getResults()) {
            %>
            <tr>
                <td><%= res.getX() %></td>
                <td><%= res.getY() %></td>
                <td><%= res.getR() %></td>
                <td><%= res.isSuccess() ? "Попадание" : "Промах" %></td>
                <td><%= res.getCurrent_time() %></td>
                <td><%= res.getExecution_time() %> мс</td>
            </tr>
            <% } %>
        </table>
        <% } %>
    </div>

    <div id="backForm">
        <form style="display: inline;" method="get" action="${pageContext.request.contextPath}/controller">
            <input type="submit" value="Вернуться к форме">
        </form>
        <form style="display: inline;" method="get" action="${pageContext.request.contextPath}/controller?operation=clear">
            <input type="submit" id="clearTableButton" value="Очистить таблицу">
        </form>
    </div>
</main>
</body>
</html>
