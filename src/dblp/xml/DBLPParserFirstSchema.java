/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dblp.xml;

import java.io.BufferedWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class DBLPParserFirstSchema extends DBLPParserSchema {

    static DBLPCollection authorsCollection = new DBLPCollection();
    static DBLPCollection yearsCollection = new DBLPCollection();
    static DBLPCollection confsCollection = new DBLPCollection();
    static DBLPCollection titlesCollection = new DBLPCollection();
    static String outputDir = "output_y2005_conf100_new_schema1";
    static String title_author_path = "data/" + outputDir + "/title_author.txt";
    static String title_conf_path = "data/" + outputDir + "/title_conf.txt";
    static String title_year_path = "data/" + outputDir + "/title_year.txt";
    static final String title_path = "data/" + outputDir + "/title.txt";
    static final String author_path = "data/" + outputDir + "/author.txt";
    static final String conf_path = "data/" + outputDir + "/conf.txt";
    static final String year_path = "data/" + outputDir + "/year.txt";
    static Writer title_year_writer;
    static Writer title_conf_writer;
    static Writer title_author_writer;

    public static void main(String[] args) {
        parse();
        filter2();

    }

    public static void parse() {
        createDir("data/" + outputDir);
        DBLPParserFirstSchema parser = new DBLPParserFirstSchema();
        try {
            title_year_writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(title_year_path), "utf-8"));

            title_conf_writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(title_conf_path), "utf-8"));

            title_author_writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(title_author_path), "utf-8"));

            SAXBuilder builder = new SAXBuilder();
            String path = "data/dblp.xml";
            Document jdomDocument = builder.build(path);
            Element root = jdomDocument.getRootElement();

//            List<String> types = Arrays.asList("incollection", "article", "inproceedings", "proceedings");
            List<String> types = Arrays.asList("inproceedings");
            for (String type : types) {
                System.out.println("extractElements for " + type);
                parser.extractElements(root, type);
            }
            titlesCollection.writeToFile(title_path);
            authorsCollection.writeToFile(author_path);
            confsCollection.writeToFile(conf_path);
            yearsCollection.writeToFile(year_path);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Set<String> titleSet = new HashSet<>();
    private void extractElements(Element root, final String type) {
        try {
            //            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            List<Element> nList = root.getChildren(type);
//        System.out.println(nList);
            System.out.println(type + " " + nList.size());
            int index = 0;
            int ignore = 0;
            final Map<Integer, String> filteredConfs = readNodeFile(conf_filter_path);
            Set<String> filteredConfsSet = new HashSet<>();
            filteredConfsSet.addAll(filteredConfs.values());
            for (Element eElement : nList) {
                String booktitle = eElement.getChild("booktitle").getText();
                //TODO: should comment out, now it doesn't filter conferences
                if (!filteredConfsSet.contains(booktitle)) {
                    ignore++;
                    continue;
                }
                String year = eElement.getChild("year").getText();
                int year_int = Integer.parseInt(year.trim());
                if (year_int < year_threshold) {
                    ignore++;
                    continue;
                }
                index++;
                if (index % 100 == 0) {
                    System.out.println("Node#: " + index + " Ignore#:" + ignore);
                }
                String title = eElement.getChild("title").getText();
                if (titleSet.contains(title)){
                    ignore++;
                    continue;
                }
                titleSet.add(title);
                int titleId = titlesCollection.get(title);
                int yearId = yearsCollection.get(year);
                int confId = confsCollection.get(booktitle);
                title_conf_writer.write(titleId + " " + confId + "\n");
                title_year_writer.write(titleId + " " + yearId + "\n");
                final List<Element> authors = eElement.getChildren("author");
                for (Element author : authors) {
                    String author_str = author.getText();
                    int authorId = authorsCollection.get(author_str);
                    title_author_writer.write(titleId + " " + authorId + "\n");
                }

            }
            title_author_writer.close();
            title_conf_writer.close();
            title_year_writer.close();
        } catch (IOException ex) {
            Logger.getLogger(DBLPParserFirstSchema.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void filter() {
        DBLPParserSchema parser = new DBLPParserFirstSchema();
        final List<Integer> topAuthors = parser.getTopAuthors(topAuthorCount, title_author_path);
        System.out.println("top authors# :" + topAuthorCount);

        final List<Edge> title_author = parser.readEdgeFile(title_author_path);
        final List<Edge> title_conf = parser.readEdgeFile(title_conf_path);
        final List<Edge> title_year = parser.readEdgeFile(title_year_path);

        final List<Integer> titles = parser.getNeighbourEntities(topAuthors, title_author, 0);

        final List<Integer> authors = parser.getNeighbourEntities(titles, title_author, 1);
        final List<Integer> confs = parser.getNeighbourEntities(titles, title_conf, 1);
        final List<Integer> years = parser.getNeighbourEntities(titles, title_year, 1);

        System.out.println("title#:" + titles.size());
        System.out.println("authors#:" + authors.size());
        System.out.println("conf#:" + confs.size());
        System.out.println("year#:" + years.size());

        final Map<Integer, String> title_nodes = parser.readNodeFile(title_path);
        final Map<Integer, String> author_nodes = parser.readNodeFile(author_path);
        final Map<Integer, String> year_nodes = parser.readNodeFile(year_path);
        final Map<Integer, String> conf_nodes = parser.readNodeFile(conf_path);

        parser.writeNodesToFile(titles, title_nodes, "data/output_filter1/title.txt");
        parser.writeNodesToFile(authors, author_nodes, "data/output_filter1/author.txt");
        parser.writeNodesToFile(confs, conf_nodes, "data/output_filter1/conf.txt");
        parser.writeNodesToFile(years, year_nodes, "data/output_filter1/year.txt");

        parser.writeEdgesToFile(titles, title_author, 0, "data/output_filter1/title_author.txt");
        parser.writeEdgesToFile(titles, title_conf, 0, "data/output_filter1/title_conf.txt");
        parser.writeEdgesToFile(titles, title_year, 0, "data/output_filter1/title_year.txt");

    }

    public static void filter2() {

        String dir = outputDir + "_top" + topAuthorCount;
        createDir("data/" + dir);
        DBLPParserSchema parser = new DBLPParserFirstSchema();
        final List<Integer> topAuthors = parser.getTopAuthors(topAuthorCount, title_author_path);
        System.out.println("top authors# :" + topAuthorCount);

        final List<Edge> title_author = parser.readEdgeFile(title_author_path);
        final List<Edge> title_conf = parser.readEdgeFile(title_conf_path);
        final List<Edge> title_year = parser.readEdgeFile(title_year_path);

        final List<Integer> titles = parser.getNeighbourEntities(topAuthors, title_author, 0);

//        final List<Integer> authors = parser.getNeighbourEntities(titles, title_author, 1);
        final List<Integer> confs = parser.getNeighbourEntities(titles, title_conf, 1);
        final List<Integer> years = parser.getNeighbourEntities(titles, title_year, 1);

        System.out.println("title#:" + titles.size());
        System.out.println("topAuthors#:" + topAuthors.size());
//        System.out.println("authors#:" + authors.size());
        System.out.println("conf#:" + confs.size());
        System.out.println("year#:" + years.size());

        final Map<Integer, String> title_nodes = parser.readNodeFile(title_path);
        final Map<Integer, String> author_nodes = parser.readNodeFile(author_path);
        final Map<Integer, String> year_nodes = parser.readNodeFile(year_path);
        final Map<Integer, String> conf_nodes = parser.readNodeFile(conf_path);

        parser.writeNodesToFile(titles, title_nodes, "data/" + dir + "/title.txt");
        parser.writeNodesToFile(topAuthors, author_nodes, "data/" + dir + "/author.txt");
        parser.writeNodesToFile(confs, conf_nodes, "data/" + dir + "/conf.txt");
        parser.writeNodesToFile(years, year_nodes, "data/" + dir + "/year.txt");

        parser.writeEdgesToFile(titles, title_author, 0, "data/" + dir + "/title_author.txt");
        parser.writeEdgesToFile(titles, title_conf, 0, "data/" + dir + "/title_conf.txt");
        parser.writeEdgesToFile(titles, title_year, 0, "data/" + dir + "/title_year.txt");

    }
}
