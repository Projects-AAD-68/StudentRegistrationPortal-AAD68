package lk.ijse.gdse.aad68.studentmanagementportal.controller;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.gdse.aad68.studentmanagementportal.dto.StudentDTO;
import lk.ijse.gdse.aad68.studentmanagementportal.util.Util;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(urlPatterns = "/student")
public class Student extends HttpServlet {
    Connection connection;
    public static String SAVE_STUDENT = "INSERT INTO student (id,name,email,city,level) VALUES(?,?,?,?,?)";
    public static String GET_STUDENT = "SELECT * FROM student WHERE id=?";
    @Override
    public void init() throws ServletException {
        try {
            var dbClass = getServletContext().getInitParameter("db-class");
            var dbUrl = getServletContext().getInitParameter("dburl");
            var dbUserName = getServletContext().getInitParameter("db-username");
            var dbPassword = getServletContext().getInitParameter("db-password");
            Class.forName(dbClass);
            this.connection = DriverManager.getConnection(dbUrl,dbUserName,dbPassword);
        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getContentType() == null || !req.getContentType().toLowerCase().startsWith("application/json")){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        try (var writer = resp.getWriter()){
            Jsonb jsonb = JsonbBuilder.create();
            StudentDTO student = jsonb.fromJson(req.getReader(), StudentDTO.class);
            student.setId(Util.idGenerate());
            //Save data in the DB
            var ps = connection.prepareStatement(SAVE_STUDENT);
            ps.setString(1, student.getId());
            ps.setString(2, student.getName());
            ps.setString(3, student.getEmail());
            ps.setString(4, student.getCity());
            ps.setString(5, student.getLevel());
            if(ps.executeUpdate() != 0){
                resp.setStatus(HttpServletResponse.SC_CREATED);
                writer.write("Save Student Successfully");

            }else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writer.write("Failed to Save Student");
            }
        }catch (SQLException e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }


    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try(var writer = resp.getWriter()) {
            StudentDTO studentDTO = new StudentDTO();
            Jsonb jsonb = JsonbBuilder.create();
            var studentId = req.getParameter("studentId");
            var ps = connection.prepareStatement(GET_STUDENT);
            ps.setString(1, studentId);
            var rst = ps.executeQuery();
            while (rst.next()){
                studentDTO.setId(rst.getString("id"));
                studentDTO.setName(rst.getString("name"));
                studentDTO.setEmail(rst.getString("email"));
                studentDTO.setCity(rst.getString("city"));
                studentDTO.setLevel(rst.getString("level"));
            }
            resp.setContentType("application/json");
            jsonb.toJson(studentDTO,writer);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Todo: Update student
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Todo: Delete student
    }

}
