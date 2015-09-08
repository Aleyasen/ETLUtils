/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACE;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Aale
 */
public class ACE2005Parser {

    public List<Doc> readDir(String dirPath) {
        List<Doc> docs = new ArrayList<>();
        File folder = new File(dirPath);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                Doc d = new Doc(file.getAbsolutePath());
                docs.add(d);
            } else if (file.isDirectory()) {
                System.out.println("Directory " + file.getName() + " ignored.");
            }
        }
        return docs;
    }

    public List<Relation> parseDoc(Doc doc) {
        if (doc.getText() == null) {
            System.out.println("text is null, please read document first!");
            return null;
        }
        List<Relation> rels = new ArrayList<>();
        Document xmldoc = Jsoup.parse(doc.getText());
        Element source_file_tag = xmldoc.getElementsByTag("source_file").get(0);
        Element document_tag = source_file_tag.getElementsByTag("documents").get(0);
        Elements entities_tag = document_tag.getElementsByTag("entity");
        for (Element elem : entities_tag) {

        }

        return rels;
    }

}
