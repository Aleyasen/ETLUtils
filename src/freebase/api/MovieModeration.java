/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package freebase.api;

import freebase.api.entity.Edge;
import freebase.api.entity.movie.Actor;
import freebase.api.entity.movie.Entity;
import freebase.api.entity.movie.FCharacter;
import freebase.api.entity.movie.Film;
import freebase.api.entity.movie.Starring;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Aale
 */
public class MovieModeration {

    public static String rootPath = "C:\\Data\\SIGMOD_DATA\\data\\top2000_imdb_target";

    public static void main(String[] args) {
        List<Film> films = Film.to(Entity.readNodeFile(rootPath + "/movie.txt"));
        List<FCharacter> chars = FCharacter.to(Entity.readNodeFile(rootPath + "/character.txt"));
        List<Actor> actors = Actor.to(Entity.readNodeFile(rootPath + "/actor.txt"));
        List<Starring> starrings = Starring.to(Entity.readNodeFile(rootPath + "/starring.txt"));
        List<Edge> star_film = Edge.readEdgeFile(rootPath + "/starring_movie.txt");
        for (Edge e : star_film) {
            final Starring star = Starring.find(e.node1, starrings);
            final Film film = Film.find(e.node2, films);
            film.addStarring(star);
            star.setFilm(film);
        }
        List<Edge> star_actor = Edge.readEdgeFile(rootPath + "/starring_actor.txt");
        for (Edge e : star_actor) {
            final Starring star = Starring.find(e.node1, starrings);
            final Actor actor = Actor.find(e.node2, actors);
            actor.addStarring(star);
            star.setActor(actor);
        }

        List<Edge> star_char = Edge.readEdgeFile(rootPath + "/starring_character.txt");
        for (Edge e : star_char) {
            final Starring star = Starring.find(e.node1, starrings);
            final FCharacter charac = FCharacter.find(e.node2, chars);
            charac.addStarring(star);
            star.setCharacter(charac);
        }
        System.out.println("remove multiple actors" + " starring# =" + starrings.size());
        // remove multiple actors for same character in one film
        int count = 0;
        List<Starring> starResult = new ArrayList<>();
        for (Starring star1 : starrings) {
            boolean addIt = true;
            for (Starring star2 : starrings) {
                if (star1.getId() > star2.getId()) {
                    if (star1.getFilm().equals(star2.getFilm()) && star1.getCharacter().equals(star2.getCharacter())) {
                        System.out.println(star1.getId() + " <<>> " + star2.getId());
                        System.out.println("duplicate " + star1.getFilm().getName() + "(" + star1.getFilm().getId() + ") <> " + star1.getCharacter().getName() + "(" + star1.getCharacter().getId() + ")");
                        System.out.println("\t " + star1.getActor().getName() + " <> " + star2.getActor().getName());
                        count++;
                        addIt = false;
                        break;
                    }
                }
            }
            if (addIt) {
                starResult.add(star1);
            }
        }
        System.out.println("count=" + count);
        System.out.println("before#= " + starrings.size());
        System.out.println("after#= " + starResult.size());

        String resultsDir1 = "C:\\Data\\SIGMOD_DATA\\data\\top2000_trans1";
        generateTransformation1(starResult, resultsDir1);
//        String resultsDir2 = "C:\\Data\\SIGMOD_DATA\\data\\top2000_trans2";
//        generateTransformation2(starResult, resultsDir2);
//        
//        String resultsDir3 = "C:\\Data\\SIGMOD_DATA\\data\\top2000_niagara_target";
//        generateSchemaNiagaraTarget(starResult, resultsDir3);

    }

    /**
     *
     * @param starResult the value of starResult
     * @param filepath the value of filepath
     */
    private static void generateTransformation1(List<Starring> starResult, String filepath) {
        Set<Entity> actors_ = new HashSet<Entity>();
        Set<Entity> characters_ = new HashSet<Entity>();
        Set<Entity> movies_ = new HashSet<Entity>();
        Set<Entity> starring_ = new HashSet<Entity>();
        List<Edge> star_movie_ = new ArrayList<Edge>();
        List<Edge> star_character_ = new ArrayList<Edge>();
        List<Edge> star_actor_ = new ArrayList<Edge>();

        for (Starring st : starResult) {
            actors_.add(st.getActor());
            characters_.add(st.getCharacter());
            movies_.add(st.getFilm());
            starring_.add(st);
            star_movie_.add(new Edge(st.getId(), st.getFilm().getId()));
            star_character_.add(new Edge(st.getId(), st.getCharacter().getId()));
            star_actor_.add(new Edge(st.getId(), st.getActor().getId()));
        }
        for (Edge e : star_actor_) {
            if (e.node2 == 695) {
                System.out.println(e.node1 + " " + e.node2);
            }
        }
//        Entity.writeNodesToFile(movies_, filepath + "/movie.txt");
//        Entity.writeNodesToFile(characters_, filepath + "/character.txt");
//        Entity.writeNodesToFile(actors_, filepath + "/actor.txt");
//        Entity.writeNodesToFile(starring_, filepath + "/starring.txt");
//        Edge.writeEdgesToFile(star_movie_, filepath + "/starring_movie.txt");
//        Edge.writeEdgesToFile(star_character_, filepath + "/starring_character.txt");
//        Edge.writeEdgesToFile(star_actor_, filepath + "/starring_actor.txt");
    }

    /**
     *
     * @param starResult the value of starResult
     * @param filepath the value of filepath
     */
    private static void generateTransformation2(List<Starring> starResult, String filepath) {
        Set<Entity> actors_ = new HashSet<Entity>();
        Set<Entity> characters_ = new HashSet<Entity>();
        Set<Entity> movies_ = new HashSet<Entity>();
        List<Edge> movie_character_ = new ArrayList<Edge>();
        List<Edge> movie_actor_ = new ArrayList<Edge>();
        List<Edge> actor_character_ = new ArrayList<Edge>();

        for (Starring st : starResult) {
            actors_.add(st.getActor());
            characters_.add(st.getCharacter());
            movies_.add(st.getFilm());
            movie_character_.add(new Edge(st.getFilm().getId(), st.getCharacter().getId()));
            movie_actor_.add(new Edge(st.getFilm().getId(), st.getActor().getId()));
            actor_character_.add(new Edge(st.getActor().getId(), st.getCharacter().getId()));
        }
        Entity.writeNodesToFile(movies_, filepath + "/movie.txt");
        Entity.writeNodesToFile(characters_, filepath + "/character.txt");
        Entity.writeNodesToFile(actors_, filepath + "/actor.txt");
        Edge.writeEdgesToFile(movie_character_, filepath + "/movie_character.txt");
        Edge.writeEdgesToFile(movie_actor_, filepath + "/movie_actor.txt");
        Edge.writeEdgesToFile(actor_character_, filepath + "/actor_character.txt");
    }

    private static void generateSchemaNiagaraTarget(List<Starring> starResult, String filepath) {
        Set<Entity> actors_ = new HashSet<Entity>();
//        Set<Entity> characters_ = new HashSet<Entity>();
        Set<Entity> movies_ = new HashSet<Entity>();
        Set<Entity> casts_ = new HashSet<Entity>();
        List<Edge> actor_cast_ = new ArrayList<Edge>();
//        List<Edge> star_character_ = new ArrayList<Edge>();
        List<Edge> film_cast_ = new ArrayList<Edge>();
        Map<Integer, List<Integer>> actorsPerMovie = new HashMap<Integer, List<Integer>>();
        for (Starring st : starResult) {
            actors_.add(st.getActor());
//            characters_.add(st.getCharacter());
            movies_.add(st.getFilm());
            final List<Integer> m = actorsPerMovie.get(st.getFilm().getId());
            if (m == null) {
                actorsPerMovie.put(st.getFilm().getId(), new ArrayList<Integer>());
            }
            actorsPerMovie.get(st.getFilm().getId()).add(st.getActor().getId());
        }
        int castSequence = 100000;
        for (Integer film : actorsPerMovie.keySet()) {
            Entity cast = new Entity(castSequence, null, "C" + castSequence);
            casts_.add(cast);
            castSequence++;
            System.out.println(film + " <> " + actorsPerMovie.get(film).size());
            for (Integer actor : actorsPerMovie.get(film)) {
                actor_cast_.add(new Edge(actor, cast.getId()));
                film_cast_.add(new Edge(film, cast.getId()));
            }
        }
        Entity.writeNodesToFile(movies_, filepath + "/movie.txt");
//        Entity.writeNodesToFile(characters_, filepath + "/character.txt");
        Entity.writeNodesToFile(actors_, filepath + "/actor.txt");
        Entity.writeNodesToFile(casts_, filepath + "/cast.txt");
        Edge.writeEdgesToFile(actor_cast_, filepath + "/actor_cast.txt");
        Edge.writeEdgesToFile(film_cast_, filepath + "/film_cast.txt");
    }

}
