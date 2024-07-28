package lk.ijse.gdse.aad68.studentmanagementportal.controller;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.gdse.aad68.studentmanagementportal.dao.StudentDAOIMPL;
import lk.ijse.gdse.aad68.studentmanagementportal.dto.StudentDTO;
import lk.ijse.gdse.aad68.studentmanagementportal.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/student",loadOnStartup = 2)
public class Student extends HttpServlet {
    static Logger logger = LoggerFactory.getLogger(Student.class);
    Connection connection;
    public static String SAVE_STUDENT = "INSERT INTO student (id,name,email,city,level) VALUES(?,?,?,?,?)";
    public static String GET_STUDENT = "SELECT * FROM student WHERE id=?";
    public static String UPDATE_STUDENT = "UPDATE student SET name=?,email=?,city=?,level=? WHERE id=?";
    public static String DELETE_STUDENT = "DELETE FROM student WHERE id=?";
    @Override
    public void init() throws ServletException {
        logger.info("Init method invoked");
        try {
            var ctx = new InitialContext();
            DataSource pool = (DataSource) ctx.lookup("java:comp/env/jdbc/stuReg");
            this.connection = pool.getConnection();
            logger.debug("Connection initialized",this.connection);

        }catch ( SQLException | NamingException e){
            logger.error("DB connection not init" );
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
            var studentDAOIMPL = new StudentDAOIMPL();
            StudentDTO student = jsonb.fromJson(req.getReader(), StudentDTO.class);
            student.setId(Util.idGenerate());
            //Save data in the DB
            writer.write(studentDAOIMPL.saveStudent(student,connection));
            resp.setStatus(HttpServletResponse.SC_CREATED);
        }catch (Exception e){
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

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (var writer = resp.getWriter()) {
            var studentId = req.getParameter("studentId");
            Jsonb jsonb = JsonbBuilder.create();
            StudentDTO student = jsonb.fromJson(req.getReader(), StudentDTO.class);

            //SQL process
            var ps = connection.prepareStatement(UPDATE_STUDENT);
            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setString(3, student.getCity());
            ps.setString(4, student.getLevel());
            ps.setString(5, studentId);
            if(ps.executeUpdate() != 0){
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }else {
                writer.write("Update failed");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (var writer = resp.getWriter()) {
            var studentId = req.getParameter("studentId");

            var ps = connection.prepareStatement(DELETE_STUDENT);
            ps.setString(1, studentId);
            if(ps.executeUpdate() != 0){
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }else {
                writer.write("Delete failed");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }


    }

}
