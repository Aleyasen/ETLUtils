/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author Aale
 */
public class WSUParser {

    public static void main(String[] args) {
        try {
            SAXBuilder builder = new SAXBuilder();
            String path = "data/wsu.xml";
            Document jdomDocument = builder.build(path);
            Element root = jdomDocument.getRootElement();

            extractElements(root);
        } catch (JDOMException ex) {
            Logger.getLogger(WSUParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WSUParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Set<Entity> courses = new HashSet<>();
    public static Set<Entity> AcceptedCourses = new HashSet<>();
    public static Set<Entity> courseOffers = new HashSet<>();
    public static Set<Entity> subjects = new HashSet<>();
    public static Set<Entity> instructors = new HashSet<>();

    public static Set<Edge> crs_instr = new HashSet<Edge>();
    public static Set<Edge> crs_crsoffer = new HashSet<Edge>();
    public static Set<Edge> crs_subj = new HashSet<Edge>();
    public static Set<Edge> crsoffer_instr = new HashSet<Edge>();
    public static Set<Edge> crsoffer_subj = new HashSet<Edge>();
    public static Set<String> crsNamesWithMultiInst = new HashSet<String>();
    public static Map<String, Set<String>> instsPerCourse = new HashMap<String, Set<String>>();

    public static int currentRegNum = 1000;

    public static int getNextRegNum() {
        return currentRegNum++;
    }

    private static void extractElements(Element root) {

        List<Element> nList = root.getChildren("course");
        System.out.println("#Courses=" + nList.size());
        for (Element eElement : nList) {
//            String reg_num = eElement.getChild("reg_num").getText() + genRandomStr();
            String reg_num = getNextRegNum() + "";
            String title = eElement.getChild("title").getText();
            String subj = eElement.getChild("prefix").getText();
            String instructor = eElement.getChild("instructor").getText();
            if (instructor.length() == 0) {
                continue;
            }
            Entity courseTempEntity = getNode(title, courses);
            if (courseTempEntity != null) {
                Entity subjectTempEntity = getNode(subj, subjects);
                if (subjectTempEntity != null) {
                    List<Integer> subjects_ = findEdge(courseTempEntity, crs_subj);
                    boolean ignore = false;
                    for (int s : subjects_) {
                        if (s != subjectTempEntity.getId()) {
                            ignore = true;
                        }
                    }
                    if (ignore) {
                        continue;
                    }
                } else { //subjectTempEntity == null
                    List<Integer> subjects_ = findEdge(courseTempEntity, crs_subj);
                    if (!subjects_.isEmpty()) {
                        continue;
                    }
                }
            }
//            System.out.println(reg_num + "\t" + title + "\t" + instructor);
            Entity courseEnt = getOrAddNode(title, courses);
            Entity courseOfferingEnt = getOrAddNode(reg_num, courseOffers);
            Entity instructorEnt = getOrAddNode(instructor, instructors);
            Entity subjEnt = getOrAddNode(subj, subjects);
            addEdge(courseEnt, instructorEnt, crs_instr);
            addEdge(courseEnt, courseOfferingEnt, crs_crsoffer);
            addEdge(courseEnt, subjEnt, crs_subj);
            addEdge(courseOfferingEnt, instructorEnt, crsoffer_instr);
            addEdge(courseOfferingEnt, subjEnt, crsoffer_subj);

            final Set<String> crs = instsPerCourse.get(courseEnt.getName());
            if (crs == null) {
                instsPerCourse.put(courseEnt.getName(), new HashSet<String>());
            }
            instsPerCourse.get(courseEnt.getName()).add(instructorEnt.getName());
        }
        System.out.println("before");
        printStatistics();

        System.out.println();
        System.out.println(instsPerCourse);
        int index = 1;
        for (String c : instsPerCourse.keySet()) {
            if (instsPerCourse.get(c).size() > 1) {
                crsNamesWithMultiInst.add(c);
//                System.out.println(index + ") " + c + " : " + instsPerCourse.get(c));
                index++;
            }
        }
        removeCoursesWithMultiInst();

        System.out.println("after");
        printStatistics();
        saveNodeEdgeFiles();
    }

    public static int idSequence = 1;

    private static Entity getOrAddNode(String name, Set<Entity> entities) {
        for (Entity ent : entities) {
            if (ent.name.equals(name)) {
                return ent;
            }
        }
        Entity newEnt = new Entity(idSequence, name);
        idSequence++;
        entities.add(newEnt);
        return newEnt;
    }

    private static Entity getNode(String name, Set<Entity> entities) {
        for (Entity ent : entities) {
            if (ent.name.equals(name)) {
                return ent;
            }
        }
        return null;
    }

    private static void addEdge(Entity ent1, Entity ent2, Set<Edge> edges) {
        edges.add(new Edge(ent1.getId(), ent2.getId()));
    }

    private static void removeCoursesWithMultiInst() {
        List<Integer> removedNodeIds = new ArrayList<>();
        for (String name : crsNamesWithMultiInst) {
            Integer id = removeNodeByName(courses, name);
            if (id != null) {
                removedNodeIds.add(id);
            }
        }
        for (Integer nodeId : removedNodeIds) {
            crs_subj = removeEdge(crs_subj, nodeId, 1);
            crs_crsoffer = removeEdge(crs_crsoffer, nodeId, 1);
            crs_instr = removeEdge(crs_instr, nodeId, 1);
        }

    }

    private static Integer removeNodeByName(Collection<Entity> list, String name) {
        for (Entity ent : list) {
            if (ent.getName().equals(name)) {
                Integer id = ent.getId();
                list.remove(ent);
                return id;
            }
        }
        return null;
    }

    private static Set<Edge> removeEdge(Collection<Edge> edges, int node, int column) {
        Set<Edge> newEdges = new HashSet<Edge>();
        for (Edge e : edges) {
            if (column == 1) {
                if (e.node1 != node) {
                    newEdges.add(e);
//                    edges.remove(e);
                }
            } else if (column == 2) {
                if (e.node2 != node) {
                    newEdges.add(e);
//                    edges.remove(e);
                }
            } else {
                System.out.println("column is not valid. " + column);
            }
        }
        return newEdges;
    }

    private static void printStatistics() {
        System.out.println("courses=" + courses.size());
        System.out.println("courseOffers=" + courseOffers.size());
        System.out.println("subjects=" + subjects.size());
        System.out.println("instructors=" + instructors.size());
        System.out.println("crs_instr=" + crs_instr.size());
        System.out.println("crs_crsoffer=" + crs_crsoffer.size());
        System.out.println("crs_subj=" + crs_subj.size());
        System.out.println("crsoffer_instr=" + crsoffer_instr.size());
        System.out.println("crsoffer_subj=" + crsoffer_subj.size());
    }

    private static void saveNodeEdgeFiles() {
        String output_root = "data//output-wsu-after-removing-empty-inst//";

        Entity.writeNodesToFile(courses, output_root + "course.txt");
        Entity.writeNodesToFile(courseOffers, output_root + "offer.txt");
        Entity.writeNodesToFile(instructors, output_root + "inst.txt");
        Entity.writeNodesToFile(subjects, output_root + "subject.txt");
        Edge.writeEdgesToFile(crs_instr, output_root + "course_inst.txt", true);
        Edge.writeEdgesToFile(crs_crsoffer, output_root + "course_offer.txt", true);
        Edge.writeEdgesToFile(crs_subj, output_root + "course_subject.txt", true);
        Edge.writeEdgesToFile(crsoffer_instr, output_root + "offer_inst.txt", true);
        Edge.writeEdgesToFile(crsoffer_subj, output_root + "offer_subject.txt", true);
    }

    private static String genRandomStr() {
        return "_" + (int) ((Math.random() * 1000000) % 324521);
    }

    private static List<Integer> findEdge(Entity courseTempEntity, Set<Edge> crs_subj) {
        List<Integer> subjects_ = new ArrayList<>();
        for (Edge e : crs_subj) {
            if (e.node1 == courseTempEntity.getId()) {
                subjects_.add(e.node2);
            }
        }
        return subjects_;
    }

}
