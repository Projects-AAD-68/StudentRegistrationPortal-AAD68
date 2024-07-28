package lk.ijse.gdse.aad68.studentmanagementportal.dao;

import lk.ijse.gdse.aad68.studentmanagementportal.controller.Student;
import lk.ijse.gdse.aad68.studentmanagementportal.dto.StudentDTO;

import java.sql.Connection;

public sealed interface StudentDAO permits StudentDAOIMPL  {
    String saveStudent(StudentDTO student, Connection connection)throws Exception;
    boolean deleteStudent(String id, Connection connection)throws Exception;
    boolean updateStudent(String id,StudentDTO student,Connection connection)throws Exception;
    StudentDTO getStudent(String id,Connection connection)throws Exception;
}
