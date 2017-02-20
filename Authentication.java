package in.zycon.auth;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.buf.HexUtils;

/**
 *
 * @author tibin_lukose
 */
public class Authentication extends HttpServlet {

    private static final int TOKEN_VALIDITY_MIN = 5;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = new PrintWriter(response.getWriter());
        try {
            if (request.getParameter("actionType").equals("login")) {
//           
//         Check Username and Password..
//         Basic Authencation , if Valid.. Return the Password_Hash from DB
//        Suppose Passsword hash for emplid 1247793 is d7fcceb1625bd54065de1c3ae57f0f3fb78e4279 
//          To Client Hash and Token will be sent...   

                String username = request.getParameter("username");
                String password = request.getParameter("password");

                String digest = "1022871";
                String timeToken = generateToken(digest);
                out.println("digest:" + digest);
                out.println("token:" + timeToken);

            } else if (request.getParameter("actionType").equals("doJob")) {
//                
//                After Authentication. Mobile has to sendBack digest & token.
//                        Token will be valid for T minutes
//                use digest to check whether data is present, if present it can be
//                        used to pull user details
                String digest = request.getParameter("digest");
                String token = request.getParameter("token");
                if (checkDigest(digest)) {
                    if (tokenCheck(token, digest)) {
                        out.println("VALID TOKEN AND DO YOUR PROCESSING");
                        out.println("DIGEST:" + digest);
                        out.println("NEW TOKEN:" + generateToken(digest));
                    } else {
                        response.setStatus(400);
                        out.print("INVALID TOKEN OR EXPIRED");
                    }

                }

            } else if (request.getParameter("actionType").equals("updateToken")) {
                String digest = request.getParameter("digest");
                String token = request.getParameter("token");
                if (checkDigest(digest)) {
                    if (tokenCheck(token, digest)) {
                        out.println("TOKEN REFRESHED");
                        out.println("DIGEST:" + digest);
                        out.println("NEW TOKEN:" + generateToken(digest));
                    } else {
                        response.setStatus(400);
                        out.print("INVALID TOKEN OR EXPIRED");
                    }

                }
            } else {
                response.setStatus(400);
                out.print("Bad Request Type");
            }
        } catch (Exception e) {
            response.setStatus(400);
            out.print("Bad Request");
        }

    }

    private String generateToken(String hash) throws NoSuchAlgorithmException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        String time = df.format(date);
        //A SERVER KEY IS USED.. NEVER SHARE THIS KEY...
        digest.update((hash + time + "4dc403f6f46307f19c6c3c2d858923e0bfcede7a").getBytes());
        //IF YOU ARE FACING ANY PROBLEM FOR HEX  USE ANY OTHER LIBRARY
        return HexUtils.toHexString(digest.digest());

    }

    private Boolean checkDigest(String digest) {
        //CHECK WHETHER THE DIGEST IS VALID BY DOING SQL QUERY
        //ASSUMING VALID NOW
        return Boolean.TRUE;
    }

    private Boolean tokenCheck(String token, String empid) throws NoSuchAlgorithmException {
        Boolean tokenValidity = Boolean.FALSE;
        for (int min = TOKEN_VALIDITY_MIN; min >=0; min--) {
            if (generateTimeToken(empid, min).equals(token)) {
                tokenValidity = Boolean.TRUE;
                break;  
            }
        }
        return tokenValidity;
    }

    private String generateTimeToken(String hash, int minute) throws NoSuchAlgorithmException {
        //CUSTOM TOKEN GENERATOR WITH ADDED TIME SLOTS 
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(System.currentTimeMillis() - minute * 60 * 1000);

        String time = df.format(date);
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        //A SERVER KEY IS USED.. NEVER SHARE THIS KEY...
        digest.update((hash + time + "4dc403f6f46307f19c6c3c2d858923e0bfcede7a").getBytes());
        //IF YOU ARE FACING ANY PROBLEM FOR HEX  USE ANY OTHER LIBRARY
        return HexUtils.toHexString(digest.digest());

    }

}
