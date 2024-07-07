package lk.ijse.gdse.aad68.studentmanagementportal.util;

import java.util.UUID;
public class Util {
    public static String idGenerate(){
        return UUID.randomUUID().toString();
    }
}
