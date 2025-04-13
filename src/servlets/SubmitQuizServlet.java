package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.*;

public class SubmitQuizServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int score = 0;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quiz_app", "root", "password");

            Enumeration<String> params = request.getParameterNames();
            while (params.hasMoreElements()) {
                String param = params.nextElement();
                if (param.startsWith("q")) {
                    int selectedOption = Integer.parseInt(request.getParameter(param));
                    PreparedStatement stmt = conn.prepareStatement("SELECT is_correct FROM options WHERE id = ?");
                    stmt.setInt(1, selectedOption);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next() && rs.getBoolean("is_correct")) {
                        score++;
                    }
                }
            }

            request.setAttribute("score", score);
            RequestDispatcher rd = request.getRequestDispatcher("result.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
