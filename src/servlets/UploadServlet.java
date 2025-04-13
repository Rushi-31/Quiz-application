package servlets;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@MultipartConfig
public class UploadServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part filePart = request.getPart("file");

        try (InputStream is = filePart.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quiz_app", "root", "password");

            PreparedStatement quizStmt = conn.prepareStatement("INSERT INTO quizzes (title) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            quizStmt.setString(1, "Uploaded Quiz");
            quizStmt.executeUpdate();
            ResultSet rs = quizStmt.getGeneratedKeys();
            rs.next();
            int quizId = rs.getInt(1);

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String question = row.getCell(1).getStringCellValue();
                int timer = (int) row.getCell(7).getNumericCellValue();
                String correct = row.getCell(6).getStringCellValue();

                PreparedStatement qStmt = conn.prepareStatement("INSERT INTO questions (quiz_id, question_text, timer_seconds) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                qStmt.setInt(1, quizId);
                qStmt.setString(2, question);
                qStmt.setInt(3, timer);
                qStmt.executeUpdate();

                ResultSet qRs = qStmt.getGeneratedKeys();
                qRs.next();
                int qId = qRs.getInt(1);

                for (int i = 0; i < 4; i++) {
                    String opt = row.getCell(2 + i).getStringCellValue();
                    boolean isCorrect = correct.equalsIgnoreCase(String.valueOf((char) ('A' + i)));
                    PreparedStatement oStmt = conn.prepareStatement("INSERT INTO options (question_id, option_text, is_correct) VALUES (?, ?, ?)");
                    oStmt.setInt(1, qId);
                    oStmt.setString(2, opt);
                    oStmt.setBoolean(3, isCorrect);
                    oStmt.executeUpdate();
                }
            }

            response.sendRedirect("quiz.jsp?quizId=" + quizId);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
