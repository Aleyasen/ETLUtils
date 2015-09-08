package freebase.api;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import freebase.api.entity.movie.Film;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Aale
 */
public class FreebaseHelper {

    public static Properties properties = new Properties();

    public static JSONArray getJSON(String fromDate, String toDate) {
        try {
            properties.load(new FileInputStream("freebase.properties"));
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            JSONParser parser = new JSONParser();
            String query = readQueryFromFile("queries/q1.json");
            query = manipulateQuery(query, fromDate, toDate);
            GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/mqlread");
            url.put("query", query);
            url.put("key", properties.get("API_KEY"));
            System.out.println("URL:" + url);
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            JSONObject response = (JSONObject) parser.parse(httpResponse.parseAsString());
            JSONArray results = (JSONArray) response.get("result");
            Utils.writeDataIntoFile(results.toString() + "\n", FreebaseAPI.JSON_DUMP_FILE);

            return results;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static String manipulateQuery(String query, String fromDate, String toDate) {
        String manipulate_query = query.replace("%from_date%", fromDate);
        manipulate_query = manipulate_query.replace("%to_date%", toDate);
        return manipulate_query;
    }

    public static String readQueryFromFile(String filepath) {
        String result = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filepath));
            String line = br.readLine();

            while (line != null) {
                result += line;
                line = br.readLine();
            }
            return result;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
