/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package freebase.api;

/**
 *
 * @author Aale
 */
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import freebase.api.entity.movie.Actor;
import freebase.api.entity.movie.Directedby;
import freebase.api.entity.movie.Director;
import freebase.api.entity.movie.Film;
import freebase.api.entity.movie.Starring;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class FreebaseAPI2 {
    
    public static Properties properties = new Properties();
    public static String current_cursor = "";
    public static Sequence IDs = new Sequence();
    public static Sequence directedbyIDs = new Sequence();
    
    static Map<String, Integer> actor_map = new HashMap<>();
    static Map<String, Integer> film_map = new HashMap<>();
    static Map<String, Integer> director_map = new HashMap<>();
    static Map<String, Integer> character_map = new HashMap<>();
    static Map<String, Integer> starring_map = new HashMap<>();
    
    public static void main(String[] args) {
//        removeDuplicates(CHARACTER_FILE);
//        FetchingFromGoogleAPI();
//        getTopKActors(20);
        filterByTopActors();
        //        System.out.println(getTopKActors(100));
    }
    
    public static Set<Integer> filterBy(String inputfile, Set<Integer> keys, Integer column, String outputfile) {
        return filterBy(inputfile, keys, column, outputfile, null);
        
    }
    
    public static Set<Integer> filterBy(String inputfile, Set<Integer> keys, Integer column, String outputfile, Integer output_column) {
        final List<String> input_list = Utils.readFileLineByLine(inputfile, true);
        StringBuilder output_str = new StringBuilder();
        Set<Integer> output_set = new HashSet();
        for (String line : input_list) {
            String[] split = line.split("\\t");
            Integer key = Integer.parseInt(split[column]);
            if (keys.contains(key)) {
                output_str.append(line).append(NEWLINE);
                if (output_column != null) {
                    output_set.add(Integer.parseInt(split[output_column]));
                }
            }
        }
        Utils.writeDataIntoFile(output_str.toString(), outputfile);
        if (output_column != null) {
            return output_set;
        } else {
            return null;
        }
    }
    
    public static void filterByTopActors() {
        String suffix = ".2k";
        final Set<Integer> topActors = getTopKActors(3);
        final Set<Integer> selected_films = filterBy(ACTOR_FILM_FILE, topActors, 0, ACTOR_FILM_FILE + suffix, 1);
        final Set<Integer> selected_characters = filterBy(ACTOR_CHARACTER_FILE, topActors, 0, ACTOR_CHARACTER_FILE + suffix, 1);
        final Set<Integer> selected_starring = filterBy(ACTOR_STARRING_FILE, topActors, 0, ACTOR_STARRING_FILE + suffix, 1);
        final Set<Integer> selected_director = filterBy(FILM_DIRECTOR_FILE, selected_films, 0, FILM_DIRECTOR_FILE + suffix, 1);
        final Set<Integer> selected_directedby = filterBy(FILM_DIRECTEDBY_FILE, selected_films, 0, FILM_DIRECTEDBY_FILE + suffix, 1);
        
        System.out.println("Actor# " + topActors.size());
        System.out.println("Films# " + selected_films.size());
        System.out.println("Characters# " + selected_characters.size());
        System.out.println("Starring# " + selected_starring.size());
        System.out.println("Directors# " + selected_director.size());
        System.out.println("Directedby# " + selected_directedby.size());
        filterBy(DIRECTOR_DIRECTEDBY_FILE, selected_director, 0, DIRECTOR_DIRECTEDBY_FILE + suffix);
        filterBy(CHARACTER_STARRING_FILE, selected_characters, 0, CHARACTER_STARRING_FILE + suffix);
        filterBy(CHARACTER_FILM_FILE, selected_characters, 0, CHARACTER_FILM_FILE + suffix);
        filterBy(FILM_STARRING_FILE, selected_films, 0, FILM_STARRING_FILE + suffix);
        
        filterBy(ACTOR_FILE, topActors, 0, ACTOR_FILE + suffix);
        filterBy(FILM_FILE, selected_films, 0, FILM_FILE + suffix);
        filterBy(CHARACTER_FILE, selected_characters, 0, CHARACTER_FILE + suffix);
        filterBy(STARRING_FILE, selected_starring, 0, STARRING_FILE + suffix);
        filterBy(DIRECTOR_FILE, selected_director, 0, DIRECTOR_FILE + suffix);
        filterBy(DIRECTEDBY_FILE, selected_directedby, 0, DIRECTEDBY_FILE + suffix);
        
    }
    
    public static Set<Integer> getTopKActors(int minFilmPerActor) {
        final List<String> edges = Utils.readFileLineByLine(ACTOR_FILM_FILE, true);
        Map<String, Integer> film_per_actor_counts = new HashMap();
        for (String line : edges) {
            String split[] = line.split("\\t");
            String actor = split[0];
            String film = split[1];
            Integer film_count = film_per_actor_counts.get(actor);
            if (film_count == null) {
                film_per_actor_counts.put(actor, 1);
            } else {
                film_per_actor_counts.put(actor, film_count + 1);
            }
        }
        Set<Integer> top_actors = new HashSet<>();
        for (String actor : film_per_actor_counts.keySet()) {
            if (film_per_actor_counts.get(actor) >= minFilmPerActor) {
                top_actors.add(Integer.parseInt(actor));
            }
        }
        System.out.println("Top-Actors# :" + top_actors.size());
        return top_actors;
    }
    
    public static void FetchingFromGoogleAPI() {
        
        int max_step = 1;
        int step = 0;
        
        int month_step = 1;
        Calendar last_date = Calendar.getInstance();
        last_date.set(Calendar.YEAR, 2016);
        last_date.set(Calendar.MONTH, 0);
        last_date.set(Calendar.DAY_OF_MONTH, 1);
        
        Calendar from_date = Calendar.getInstance();
        from_date.set(Calendar.YEAR, 2000);
        from_date.set(Calendar.MONTH, 0);
        from_date.set(Calendar.DAY_OF_MONTH, 1);
        
        Calendar to_date = (Calendar) from_date.clone();
        to_date.add(Calendar.MONTH, month_step);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        while (true) {
//            if (step > max_step) {
//                break;
//            }
//            System.out.println(">>> Cursor = " + current_cursor);
//            if (/*films == null || films.isEmpty() ||*/step == max_step) {
//                System.out.println("Films is null or zero size. Break.");
//                break;
//            }
            if (from_date.after(last_date)) {
                System.out.println("Date is after last date");
                break;
            }
            String fromDate = sdf.format(from_date.getTime());
            String toDate = sdf.format(to_date.getTime());
            System.out.println(step + " ) fetch films from " + fromDate + " to " + toDate);
            step++;
            List<Film> films = getFilms(fromDate, toDate);
            System.out.println("Films#: " + films.size());
            writeToFiles(films);
//            for (Film f : films) {
//                System.out.println(f);
//            }
            from_date.add(Calendar.MONTH, month_step);
            to_date.add(Calendar.MONTH, month_step);
            
        }
    }
    
    public static List<Film> getFilms(String fromDate, String toDate) {
        try {
            properties.load(new FileInputStream("freebase.properties"));
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            JSONParser parser = new JSONParser();
//            String query = "[{\"id\":null,\"name\":null,\"type\":\"/astronomy/planet\"}]";
            String query = readQueryFromFile("queries/q1.json");
            query = manipulateQuery(query, fromDate, toDate);
            //JSONArray queryJObj = (JSONArray) JSONValue.parse(query);
            //query = queryJObj.toJSONString();
            //query = "[{\"id\":null,\"name\":null,\"type\":\"/film/film\"}]";
            //query = "[{\"mid\":null,\"name\":null,\"language\":\"English Language\",\"country\":\"United States of America\",\"type\":\"/film/film\",\"initial_release_date>=\":\"2000-01-01\",\"initial_release_date<=\":\"2020-01-01\",\"initial_release_date\":null,\"directed_by\":[{\"mid\":null,\"name\":null}],\"starring\":[{\"mid\":null,\"actor\":[{\"mid\":null,\"name\":null}],\"character\":[{\"mid\":null,\"name\":null}]}],\"limit\":1}]";
            GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/mqlread");
            url.put("query", query);
            url.put("key", properties.get("API_KEY"));
            // url.put("cursor", current_cursor);
            System.out.println("URL:" + url);
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            JSONObject response = (JSONObject) parser.parse(httpResponse.parseAsString());
            JSONArray results = (JSONArray) response.get("result");
//            if (response.get("cursor") instanceof Boolean) {
//                System.out.println("End of The Result, cursor=" + response.get("cursor"));
//            } else {
//                current_cursor = (String) response.get("cursor");
//            }
//            System.out.println(results.toString());
            Utils.writeDataIntoFile(results.toString() + "\n", JSON_DUMP_FILE);
            List<Film> films = encodeJSON(results);
            
            return films;
//            for (Object result : results) {
//                System.out.println(JsonPath.read(result, "$.name").toString());
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
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
    
    private static List<Film> encodeJSON(JSONArray results) {
        List<Film> films = new ArrayList<>();
        for (Object flmObj : results) {
            JSONObject flmJObj = (JSONObject) flmObj;
            Film film = new Film(getId((String) flmJObj.get("mid"), film_map), (String) flmJObj.get("mid"), (String) flmJObj.get("name"));
            
            JSONArray directed_by_arr = (JSONArray) flmJObj.get("directed_by");
            JSONObject directed_by = (JSONObject) directed_by_arr.get(0);
            Directedby directedby = new Directedby();
            directedby.setId(directedbyIDs.next());
            
            Director director = new Director(getId((String) directed_by.get("mid"), director_map), (String) directed_by.get("mid"), (String) directed_by.get("name"));
            directedby.setDirector(director);
            film.setDirectedBy(directedby);
            
            JSONArray starrings = (JSONArray) flmJObj.get("starring");
            for (Object starringObj : starrings) {
                JSONObject starringJObj = (JSONObject) starringObj;
                Starring starring = new Starring();
                starring.setMid((String) starringJObj.get("mid"));
                starring.setId(getId((String) starringJObj.get("mid"), starring_map));
                
                JSONArray actorJObj_arr = (JSONArray) starringJObj.get("actor");
                JSONObject actorJObj = (JSONObject) actorJObj_arr.get(0);
                Actor actor = new Actor(getId((String) actorJObj.get("mid"), actor_map), (String) actorJObj.get("mid"), (String) actorJObj.get("name"));
                starring.setActor(actor);
                
                JSONArray characterJObj_arr = (JSONArray) starringJObj.get("character");
                JSONObject characterJObj = (JSONObject) characterJObj_arr.get(0);
                freebase.api.entity.movie.FCharacter character = new freebase.api.entity.movie.FCharacter(getId((String) characterJObj.get("mid"), character_map), (String) characterJObj.get("mid"), (String) characterJObj.get("name"));
                starring.setCharacter(character);
                
                starring.setFilm(film);
                film.addStarring(starring);
            }
            
            films.add(film);
        }
        return films;
    }
    
    private static String manipulateQuery(String query, String fromDate, String toDate) {
        String manipulate_query = query.replace("%from_date%", fromDate);
        manipulate_query = manipulate_query.replace("%to_date%", toDate);
        return manipulate_query;
    }
    
    final static String ROOT_DIR = "C:/Data/freebase/results2/";
    
    static String JSON_DUMP_FILE = ROOT_DIR + "dump_json.txt";
    //node files
    static String CHARACTER_FILE = ROOT_DIR + "character.txt";
    static String DIRECTOR_FILE = ROOT_DIR + "director.txt";
    static String FILM_FILE = ROOT_DIR + "film.txt";
    static String ACTOR_FILE = ROOT_DIR + "actor.txt";
    static String STARRING_FILE = ROOT_DIR + "starring.txt";
    static String DIRECTEDBY_FILE = ROOT_DIR + "directedby.txt";

    //edge files
    static String FILM_STARRING_FILE = ROOT_DIR + "film_starring.txt";
    static String ACTOR_STARRING_FILE = ROOT_DIR + "actor_starring.txt";
    static String CHARACTER_STARRING_FILE = ROOT_DIR + "character_starring.txt";
    static String FILM_DIRECTEDBY_FILE = ROOT_DIR + "film_directedby.txt";
    static String DIRECTOR_DIRECTEDBY_FILE = ROOT_DIR + "director_directedby.txt";

    //edge files - for imdb
    static String ACTOR_CHARACTER_FILE = ROOT_DIR + "actor_character.txt";
    static String ACTOR_FILM_FILE = ROOT_DIR + "actor_film.txt";
    static String CHARACTER_FILM_FILE = ROOT_DIR + "character_film.txt";
    static String FILM_DIRECTOR_FILE = ROOT_DIR + "film_director.txt";
    
    static String SEPERATOR = "\t";
    static String NEWLINE = "\n";
    
    public static void removeDuplicates(String filepath) {
        final List<String> file = Utils.readFileLineByLine(filepath, true);
        final List<String> file_all = Utils.readFileLineByLine(filepath + ".all", true);
        Set<String> mids = new HashSet<>();
        StringBuilder fileout_str = new StringBuilder();
        StringBuilder fileout_all_str = new StringBuilder();
        int duplicateCounts = 0;
        System.out.println("Removing duplicates from " + filepath);
        for (int i = 0; i < file_all.size(); i++) {
            String line = file_all.get(i);
            String[] split = line.split("\\t");
            String mid = split[1];
//            System.out.println("mid=" + mid);
            if (!mids.contains(mid)) {
                fileout_str.append(file.get(i)).append(NEWLINE);
                fileout_all_str.append(line).append(NEWLINE);
                mids.add(mid);
            } else {
                duplicateCounts++;
            }
        }
        System.out.println("Writing to file...");
        Utils.writeDataIntoFile(fileout_str.toString(), filepath + ".2");
        Utils.writeDataIntoFile(fileout_all_str.toString(), filepath + ".all.2");
        
        System.out.println("Duplicates# :" + duplicateCounts);
    }
    
    private static void writeToFiles(List<Film> films) {
        StringBuilder film_str_all = new StringBuilder();
        StringBuilder film_str = new StringBuilder();
        StringBuilder directedby_str = new StringBuilder();
        StringBuilder film_directedby_str = new StringBuilder();
        StringBuilder film_director_str = new StringBuilder();
        StringBuilder director_str_all = new StringBuilder();
        StringBuilder director_str = new StringBuilder();
        StringBuilder director_directedby_str = new StringBuilder();
        StringBuilder starring_str_all = new StringBuilder();
        StringBuilder starring_str = new StringBuilder();
        StringBuilder film_starring_str = new StringBuilder();
        StringBuilder actor_str_all = new StringBuilder();
        StringBuilder actor_str = new StringBuilder();
        StringBuilder actor_starring_str = new StringBuilder();
        StringBuilder character_str_all = new StringBuilder();
        StringBuilder character_str = new StringBuilder();
        StringBuilder character_starring_str = new StringBuilder();
        StringBuilder character_film_str = new StringBuilder();
        StringBuilder actor_character_str = new StringBuilder();
        StringBuilder actor_film_str = new StringBuilder();
        
        for (Film film : films) {
            System.out.println("Writing film = " + film.getId() + " , " + film.getMid() + " , " + film.getName() + " , starring# = " + film.getStarrings().size());
            //film
            film_str_all.append(film.getId()).append(SEPERATOR).append(film.getMid()).append(SEPERATOR).append(film.getName()).append(NEWLINE);
            film_str.append(film.getId()).append(SEPERATOR).append(film.getName()).append(NEWLINE);

            //directedby
            directedby_str.append(film.getDirectedBy().getId()).append(NEWLINE);

            //film-directedby
            film_directedby_str.append(film.getId()).append(SEPERATOR).append(film.getDirectedBy().getId()).append(NEWLINE);

            //film-director
            film_director_str.append(film.getId()).append(SEPERATOR).append(film.getDirectedBy().getDirector().getId()).append(NEWLINE);

            //director
            director_str_all.append(film.getDirectedBy().getDirector().getId()).append(SEPERATOR).append(film.getDirectedBy().getDirector().getMid()).append(SEPERATOR).append(film.getDirectedBy().getDirector().getName()).append(NEWLINE);
            director_str.append(film.getDirectedBy().getDirector().getId()).append(SEPERATOR).append(film.getDirectedBy().getDirector().getName()).append(NEWLINE);

            //director-directedby
            director_directedby_str.append(film.getDirectedBy().getDirector().getId()).append(SEPERATOR).append(film.getDirectedBy().getId()).append(NEWLINE);
            
            for (Starring starring : film.getStarrings()) {
                //starring
                starring_str_all.append(starring.getId()).append(SEPERATOR).append(starring.getMid()).append(NEWLINE);
                starring_str.append(starring.getId()).append(NEWLINE);

                //film-starring
                film_starring_str.append(film.getId()).append(SEPERATOR).append(starring.getId()).append(NEWLINE);

                //actor
                actor_str_all.append(starring.getActor().getId()).append(SEPERATOR).append(starring.getActor().getMid()).append(SEPERATOR).append(starring.getActor().getName()).append(NEWLINE);
                actor_str.append(starring.getActor().getId()).append(SEPERATOR).append(starring.getActor().getName()).append(NEWLINE);

                //actor-starring
                actor_starring_str.append(starring.getActor().getId()).append(SEPERATOR).append(starring.getId()).append(NEWLINE);

                //character
                character_str_all.append(starring.getCharacter().getId()).append(SEPERATOR).append(starring.getCharacter().getMid()).append(SEPERATOR).append(starring.getCharacter().getName()).append(NEWLINE);
                character_str.append(starring.getCharacter().getId()).append(SEPERATOR).append(starring.getCharacter().getName()).append(NEWLINE);

                //character-starring
                character_starring_str.append(starring.getCharacter().getId()).append(SEPERATOR).append(starring.getId()).append(NEWLINE);

                //character-film
                character_film_str.append(starring.getCharacter().getId()).append(SEPERATOR).append(film.getId()).append(NEWLINE);

                //actor-character
                actor_character_str.append(starring.getActor().getId()).append(SEPERATOR).append(starring.getCharacter().getId()).append(NEWLINE);

                //actor-film
                actor_film_str.append(starring.getActor().getId()).append(SEPERATOR).append(film.getId()).append(NEWLINE);
                
            }
        }
        Utils.writeDataIntoFile(film_str_all.toString(), FILM_FILE + ".all");
        Utils.writeDataIntoFile(film_str.toString(), FILM_FILE);
        Utils.writeDataIntoFile(directedby_str.toString(), DIRECTEDBY_FILE);
        Utils.writeDataIntoFile(film_directedby_str.toString(), FILM_DIRECTEDBY_FILE);
        Utils.writeDataIntoFile(film_director_str.toString(), FILM_DIRECTOR_FILE);
        Utils.writeDataIntoFile(director_str_all.toString(), DIRECTOR_FILE + ".all");
        Utils.writeDataIntoFile(director_str.toString(), DIRECTOR_FILE);
        Utils.writeDataIntoFile(director_directedby_str.toString(), DIRECTOR_DIRECTEDBY_FILE);
        Utils.writeDataIntoFile(starring_str_all.toString(), STARRING_FILE + ".all");
        Utils.writeDataIntoFile(starring_str.toString(), STARRING_FILE);
        Utils.writeDataIntoFile(film_starring_str.toString(), FILM_STARRING_FILE);
        Utils.writeDataIntoFile(actor_str_all.toString(), ACTOR_FILE + ".all");
        Utils.writeDataIntoFile(actor_str.toString(), ACTOR_FILE);
        Utils.writeDataIntoFile(actor_starring_str.toString(), ACTOR_STARRING_FILE);
        Utils.writeDataIntoFile(character_str_all.toString(), CHARACTER_FILE + ".all");
        Utils.writeDataIntoFile(character_str.toString(), CHARACTER_FILE);
        Utils.writeDataIntoFile(character_starring_str.toString(), CHARACTER_STARRING_FILE);
        
        Utils.writeDataIntoFile(character_film_str.toString(), CHARACTER_FILM_FILE);
        Utils.writeDataIntoFile(actor_character_str.toString(), ACTOR_CHARACTER_FILE);
        Utils.writeDataIntoFile(actor_film_str.toString(), ACTOR_FILM_FILE);
        
    }
    
    private static int getId(String mid, Map<String, Integer> map) {
        Integer id = map.get(mid);
        if (id == null) {
            Integer new_id = IDs.next();
            map.put(mid, new_id);
            return new_id;
        } else {
            return id;
        }
    }
    
    private static boolean isNew(String mid, Map<String, Integer> map) {
        Integer id = map.get(mid);
        if (id == null) {
            return true;
        } else {
            return false;
        }
    }
    
    private static Set<Integer> getFilmsBy(Set<Integer> topActors) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
