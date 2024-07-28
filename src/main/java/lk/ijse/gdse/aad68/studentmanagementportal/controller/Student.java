package lk.ijse.gdse.aad68.studentmanagementportal.controller;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.gdse.aad68.studentmanagementportal.bo.StudentBOIMPL;
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
            var studentBOIMPL = new StudentBOIMPL();
            StudentDTO student = jsonb.fromJson(req.getReader(), StudentDTO.class);
            student.setId(Util.idGenerate());
            //Save data in the DB
            writer.write(studentBOIMPL.saveStudent(student,connection));
            resp.setStatus(HttpServletResponse.SC_CREATED);
        }catch (Exception e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }


    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try(var writer = resp.getWriter()) {
            var studentBOIMPL = new StudentBOIMPL();
            Jsonb jsonb = JsonbBuilder.create();
            //DB Process
            var studentId = req.getParameter("studentId");;
            resp.setContentType("application/json");
            jsonb.toJson(studentBOIMPL.getStudent(studentId,connection),writer);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (var writer = resp.getWriter()) {
            var studentBOIMPL = new StudentBOIMPL();
            var studentId = req.getParameter("studentId");
            Jsonb jsonb = JsonbBuilder.create();
            StudentDTO student = jsonb.fromJson(req.getReader(), StudentDTO.class);
            if(studentBOIMPL.updateStudent(studentId,student,connection)){
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }else {
                writer.write("Update failed");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (var writer = resp.getWriter()) {
            var studentId = req.getParameter("studentId");
            var studentBOIMPL = new StudentBOIMPL();
            if(studentBOIMPL.deleteStudent(studentId,connection)){
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }else {
                writer.write("Delete failed");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

}
