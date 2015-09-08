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
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

/**
 *
 * @author Aale
 */
public class ACE2Parser {

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
        Elements entities_tag = xmldoc.select("source_file > documents > entity");
        for (Element elem : entities_tag) {
            Element ent_type_tag = elem.select("entity_type").first();
            String type = ent_type_tag.text();
            Elements ent_mentions_tags = elem.select("entity_mention");

            for (Element ent_mnt : ent_mentions_tags) {
                int start = Integer.parseInt(ent_mnt.select("extent > charseq > start").text());
                int end = Integer.parseInt(ent_mnt.select("extent > charseq > end").text());
                Node comment = getComment(ent_mnt.select("extent > charseq").first());
                String text = comment.outerHtml();
                Entity entity = new Entity();
            }
        }

        return rels;
    }

    public Node getComment(Element elem) {
        for (Element e : elem.getAllElements()) {
            for (Node n : e.childNodes()) {
                if (n instanceof Comment) {
                    return n;
                }
            }
        }
        return null;
    }
}
