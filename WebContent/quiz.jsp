<%@ page import="java.sql.*" %>
<%
    int quizId = Integer.parseInt(request.getParameter("quizId"));
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quiz_app", "root", "password");

    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM questions WHERE quiz_id = ?");
    stmt.setInt(1, quizId);
    ResultSet rs = stmt.executeQuery();
%>
<html>
<head>
    <title>Quiz</title>
    <script>
        function startTimer(seconds, display, formId) {
            let timer = seconds;
            const interval = setInterval(() => {
                display.textContent = timer + " sec";
                timer--;
                if (timer < 0) {
                    clearInterval(interval);
                    document.getElementById(formId).submit();
                }
            }, 1000);
        }
    </script>
</head>
<body>
<h2>Quiz</h2>
<form method="post" action="submit">
<%
    while (rs.next()) {
        int qId = rs.getInt("id");
        String qText = rs.getString("question_text");
        int timer = rs.getInt("timer_seconds");

        out.println("<div style='border:1px solid #ccc; padding:10px;'>");
        out.println("<b>" + qText + "</b><br>");
        out.println("Time: <span id='timer" + qId + "'></span><br>");

        PreparedStatement oStmt = conn.prepareStatement("SELECT * FROM options WHERE question_id = ?");
        oStmt.setInt(1, qId);
        ResultSet ors = oStmt.executeQuery();

        while (ors.next()) {
            int optId = ors.getInt("id");
            String optText = ors.getString("option_text");
            out.println("<input type='radio' name='q" + qId + "' value='" + optId + "' required> " + optText + "<br>");
        }

        out.println("</div><script>startTimer(" + timer + ", document.getElementById('timer" + qId + "'), 'form');</script><br>");
    }
%>
<input type="submit" value="Submit Quiz">
</form>
</body>
</html>
