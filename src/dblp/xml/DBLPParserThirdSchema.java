/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dblp.xml;

import static dblp.xml.DBLPParserSchema.*;
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

public class DBLPParserThirdSchema extends DBLPParserSchema {

    static DBLPCollection authorsCollection = new DBLPCollection();
    static DBLPCollection yearConfsCollection = new DBLPCollection();
    static DBLPCollection confsCollection = new DBLPCollection();
    static DBLPCollection titlesCollection = new DBLPCollection();
    static String outputDir = "output_y2005_conf50_schema3";
    static String title_author_path = "data/" + outputDir + "/title_author.txt";
    static String title_conf_path = "data/" + outputDir + "/title_conf.txt";
    static String conf_year_path = "data/" + outputDir + "/conf_year.txt";
    static final String title_path = "data/" + outputDir + "/title.txt";
    static final String author_path = "data/" + outputDir + "/author.txt";
    static final String conf_path = "data/" + outputDir + "/conf.txt";
    static final String year_with_conf_path = "data/" + outputDir + "/year-with-conf.txt";
    static final String year_path = "data/" + outputDir + "/year.txt";

    static Writer title_author_writer;
    static Writer title_conf_writer;
    static Writer conf_year_writer;

    public static void main(String[] args) {
        parse();
//        filter2();
    }

    public static void parse() {
        createDir("data/" + outputDir);
        DBLPParserThirdSchema parser = new DBLPParserThirdSchema();
        try {

            title_author_writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(title_author_path), "utf-8"));
            title_conf_writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(title_conf_path), "utf-8"));
            conf_year_writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(conf_year_path), "utf-8"));

            SAXBuilder builder = new SAXBuilder();
            String path = "data/dblp.xml";
            Document jdomDocument = builder.build(path);
            Element root = jdomDocument.getRootElement();

//            List<String> types = Arrays.asList("incollection", "article", "inproceedings", "proceedings");
            List<String> types = Arrays.asList("inproceedings");
            for (String type : types) {
                parser.extractElements(root, type);
            }
            titlesCollection.writeToFile(title_path);
            authorsCollection.writeToFile(author_path);
            confsCollection.writeToFile(conf_path);
            yearConfsCollection.writeToFile(year_with_conf_path);
            yearConfsCollection.writeToFileJustFirsToken(year_path);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                int titleId = titlesCollection.get(title);
                int confId = confsCollection.get(booktitle);
                String yearConf = year + " " + booktitle;
                int yearConfId = yearConfsCollection.get(yearConf);
                title_conf_writer.write(titleId + " " + confId + "\n");
                conf_year_writer.write(confId + " " + yearConfId + "\n");
                final List<Element> authors = eElement.getChildren("author");
                for (Element author : authors) {
                    String author_str = author.getText();
                    int authorId = authorsCollection.get(author_str);
                    title_author_writer.write(titleId + " " + authorId + "\n");
                }
            }
            title_author_writer.close();
            title_conf_writer.close();
            conf_year_writer.close();
        } catch (IOException ex) {
            Logger.getLogger(DBLPParserThirdSchema.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void filter() {
        DBLPParserSchema parser = new DBLPParserFirstSchema();
        final List<Integer> topAuthors = parser.getTopAuthors(topAuthorCount, title_author_path);
        System.out.println("top authors# :" + topAuthorCount);

        final List<Edge> title_author = parser.readEdgeFile(title_author_path);
        final List<Edge> title_conf = parser.readEdgeFile(title_conf_path);
        final List<Edge> conf_year = parser.readEdgeFile(conf_year_path);

        final List<Integer> titles = parser.getNeighbourEntities(topAuthors, title_author, 0);

        final List<Integer> authors = parser.getNeighbourEntities(titles, title_author, 1);
        final List<Integer> confs = parser.getNeighbourEntities(titles, title_conf, 1);
        final List<Integer> years = parser.getNeighbourEntities(confs, conf_year, 1);

        System.out.println("title#:" + titles.size());
        System.out.println("authors#:" + authors.size());
        System.out.println("conf#:" + confs.size());
        System.out.println("year#:" + years.size());

        final Map<Integer, String> title_nodes = parser.readNodeFile(title_path);
        final Map<Integer, String> author_nodes = parser.readNodeFile(author_path);
        final Map<Integer, String> year_nodes = parser.readNodeFile(year_path);
        final Map<Integer, String> conf_nodes = parser.readNodeFile(conf_path);

        parser.writeNodesToFile(titles, title_nodes, "data/output_filter3/title.txt");
        parser.writeNodesToFile(authors, author_nodes, "data/output_filter3/author.txt");
        parser.writeNodesToFile(confs, conf_nodes, "data/output_filter3/conf.txt");
        parser.writeNodesToFile(years, year_nodes, "data/output_filter3/year.txt");

        parser.writeEdgesToFile(titles, title_author, 0, "data/output_filter3/title_author.txt");
        parser.writeEdgesToFile(titles, title_conf, 0, "data/output_filter3/title_conf.txt");
        parser.writeEdgesToFile(confs, conf_year, 0, "data/output_filter3/conf_year.txt");

    }

    public static void filter2() {

        String dir = outputDir + "_top" + topAuthorCount;
        createDir("data/" + dir);

        DBLPParserSchema parser = new DBLPParserFirstSchema();
        final List<Integer> topAuthors = parser.getTopAuthors(topAuthorCount, title_author_path);
        System.out.println("top authors# :" + topAuthorCount);

        final List<Edge> title_author = parser.readEdgeFile(title_author_path);
        final List<Edge> title_conf = parser.readEdgeFile(title_conf_path);
        final List<Edge> conf_year = parser.readEdgeFile(conf_year_path);

        final List<Integer> titles = parser.getNeighbourEntities(topAuthors, title_author, 0);

//        final List<Integer> authors = parser.getNeighbourEntities(titles, title_author, 1);
        final List<Integer> confs = parser.getNeighbourEntities(titles, title_conf, 1);
        final List<Integer> years = parser.getNeighbourEntities(confs, conf_year, 1);

        System.out.println("title#:" + titles.size());
        System.out.println("topAuthors#:" + topAuthors.size());
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
        parser.writeEdgesToFile(confs, conf_year, 0, "data/" + dir + "/conf_year.txt");

    }
}
