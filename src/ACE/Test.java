/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACE;

/**
 *
 * @author Amirhossein Aleyasen <aleyase2@illinois.edu>
 * created on Jan 9, 2016, 5:53:01 PM
 */
public class Test {

    public static void main(String[] args) {

        String s = "https://www2.washingtonmonthly.com";
        //s = s.replaceFirst("^(http(s*)://|http(s*)://www(\\d*)\\.|www\\.)","");
//        String pattern = "^(http(s*):\\/\\/|http(s*):\\/\\/www\\d*\\.|www\\d*\\.)";
        String pattern = "^(http(s*):(\\/)(\\/)www(\\d*)(\\.)|https?:(\\/)(\\/)|www(\\d*)(\\.))";
        s = s.replaceFirst(pattern, "");
        

        System.out.println(s);

    }
}
