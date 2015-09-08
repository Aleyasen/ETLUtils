/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author Aale
 */
public class MAS {

    public static String SEPERATOR = "\t";
    public static String NEWLINE = "\n";

    // social science	22 => communication 2
    // medicine  6   =>  Psychiatry & Psychology 22  ==> on palm, working 
    // social science   22   => political science 8 ==> local, working
    // social science   22   =>  sociology 10 ==> local , done
    // medicine  6   =>  neuroscience 14  ==> oak, working
    public static void main(String[] args) {

        String all_papers_file = "C:\\Users\\Aale\\Box Sync\\MotiProjects\\Universal-DB\\data\\citation\\papers_title_d2_sd18.txt";
        String top_paper_file = "C:\\Users\\Aale\\Box Sync\\MotiProjects\\Universal-DB\\data\\citation\\top_papers.txt";
        String out_file = "C:\\Users\\Aale\\Box Sync\\MotiProjects\\Universal-DB\\data\\citation\\top_papers_with_id.txt";
       
        filterPapersFromTopPaperList(all_papers_file, top_paper_file, out_file);
    }

    public static void main2(String[] args) {
//        extractPaperCategory(0, 2, 18);
//        extractPaperCategory(0, 6, 22);
//        extractPaperCategory(0, 22, 8);
//        extractPaperCategory(0, 22, 10);
//        extractPaperCategory(0, 6, 14);

//        extractPapers(1434300);
//        extractPaperCategory(0, 22, 2);
//        extractAffilition(0);
//        extractAuthor(0);
//        extractConference(0);
//        extractJournal(0);
//        extractPaper_Author(0);
//        extractPaper_Category(4171300);
        int domain = 2;
        int subdomain = 18;
        final List<Integer> paperIds = getPapersIdsFromPaperCatFile("data/papers_d" + domain + "_sd" + subdomain + ".csv");
        System.out.println("Paper#: " + paperIds.size());
        batchPapers(paperIds, "data/papers_title_d" + domain + "_sd" + subdomain + ".csv", "data/papers_title_d" + domain + "_sd" + subdomain + "_dump.json");
//        
//        batchPaperAuthors(paperIds, "data/paper_author_d" + domain + "_sd" + subdomain + ".csv", "data/paper_author_d" + domain + "_sd" + subdomain + "_dump.json");
//        final SortedSet<Integer> authorIds = getAuthorIdsFromPaperAuthorFile("data/paper_author_d" + domain + "_sd" + subdomain + ".csv");
//        System.out.println("Authors#: " + authorIds.size());
//        batchAuthors(authorIds, "data/author_d" + domain + "_sd" + subdomain + ".csv", "data/author_d" + domain + "_sd" + subdomain + "_dump.json");
    }

    public static void filterPapersFromTopPaperList(String all_papers_file, String top_paper_file, String out_file) {
        final List<String> lines = IOUtils.readFileLineByLine(all_papers_file, false);
        Map<String, Integer> papers = new HashMap<>();
        for (String l : lines) {
            String[] split = l.split("\\t");
            Integer id = Integer.parseInt(split[0]);
            String title = split[1];
            papers.put(title, id);
        }
        System.out.println("papers map size = " + papers.size());
        final List<String> lines2 = IOUtils.readFileLineByLine(top_paper_file, false);
        StringBuilder stb = new StringBuilder();
        for (String l : lines2) {
            String[] split = l.split("\\t");
            String title = split[0];
            title = title.substring(0, title.length() - 7);
            System.out.println("title = " + title);
            Integer id = papers.get(title);
            if (id != null) {
                System.out.println("find title = " + title + " " + id);
                stb.append(id).append("\t").append(title).append("\n");
            }
        }
        IOUtils.writeDataIntoFile(stb.toString(), out_file);
    }

    public static void batchAuthors(SortedSet<Integer> author_ids, String csvFile, String jsonFile) {
        int start = 0;
        int limit = 200;
        String str = "";
        for (Integer id : author_ids) {
            if (start == author_ids.size()) {
                break;
            }
            System.out.println("process author: " + id);
            str += "ID%20eq%20" + id + "%20or%20";
            if (start % limit == 0) {
                str = "(" + str.substring(0, str.length() - 8) + ")";
                getAuthors(0, str, csvFile, jsonFile);
                System.out.println("URL: " + str);
                str = "";
            }
            start++;
        }
    }

    public static void batchPapers(List<Integer> paper_ids, String csvFile, String jsonFile) {
        int start = 0;
        int limit = 200;
        String str = "";
        while (true) {
            if (start == paper_ids.size()) {
                break;
            }
//            System.out.println("process paper: " + paper_ids.get(start));
            str += "ID%20eq%20" + paper_ids.get(start) + "%20or%20";
            if (start % limit == 0) {
                System.out.println("process papers: " + start);
                str = "(" + str.substring(0, str.length() - 8) + ")";
                getPapers(0, str, csvFile, jsonFile);
                System.out.println("URL: " + str);
                str = "";
            }
            start++;
        }
    }

    public static void batchPaperAuthors(List<Integer> paper_ids, String csvFile, String jsonFile) {
        int start = 0;
        int limit = 200;
        String str = "";
        while (true) {
            if (start == paper_ids.size()) {
                break;
            }
            System.out.println("process paper: " + paper_ids.get(start));
            str += "PaperID%20eq%20" + paper_ids.get(start) + "%20or%20";
            if (start % limit == 0) {
                str = "(" + str.substring(0, str.length() - 8) + ")";
                getPaperAuthors(0, str, csvFile, jsonFile);
                System.out.println("URL: " + str);
                str = "";
            }
            start++;
        }
    }

    public static void getAuthors(int start, String authors_filter, String csvFile, String jsonFile) {
        String csv_file_path = csvFile;
        String json_dump_file_path = jsonFile;
        String url = "https://api.datamarket.azure.com/MRC/MicrosoftAcademic/v2/Author?$select=ID,Name,NativeName,AffiliationID&$filter=" + authors_filter + "&$format=json";
//        String url = "https://api.datamarket.azure.com/MRC/MicrosoftAcademic/v2/Paper_Author?$select=PaperID,SeqID,AuthorID,Name,AffiliationID&$filter=" + papers_filter + "&$format=json";
        while (true) {
            IOUtils.writeDataIntoFile(start + "", paper_last, false);
            try {
                StringBuilder csv_str = new StringBuilder();
                final String json = getData2(url, start);
//                System.out.println("json=" + json);
                if (json == null) {
                    System.out.println("json is null. skip. old start=" + start);
                    start += 100;
                    Thread.sleep(1000L);
                    continue;
                }
                JSONParser parser = new JSONParser();
                JSONObject jsonObj = (JSONObject) parser.parse(json);
                final JSONObject dObj = (JSONObject) jsonObj.get("d");
                final JSONArray results = (JSONArray) dObj.get("results");
                if (results.size() == 0) {
                    System.out.println("results is Empty, break.");
                    break;
                } else {
                    System.out.println("Paper: start = " + start + " results# = " + results.size());
                    for (Object paper : results) {
                        JSONObject paperObj = (JSONObject) paper;
                        Long id = (Long) paperObj.get("ID");
                        String name = normalized((String) paperObj.get("Name"));
                        String nativeName = normalized((String) paperObj.get("NativeName"));
//                        String affiliation = normalized((String) paperObj.get("Affiliation"));
                        Long affiliationID = (Long) paperObj.get("AffiliationID");
//                        Long version = (Long) paperObj.get("Version");
                        csv_str
                                .append(id).append(SEPERATOR)
                                .append(name).append(SEPERATOR)
                                .append(nativeName).append(SEPERATOR)
                                .append(affiliationID).append(NEWLINE);
                    }
                    IOUtils.writeDataIntoFile(json + "\n", json_dump_file_path);
                    IOUtils.writeDataIntoFile(csv_str.toString(), csv_file_path);
                    start += 100;
                    Thread.sleep(400L);
                }
//                System.out.println("json= " + jsonObj);
            } catch (ParseException ex) {
                System.out.println(ex.getMessage() + " Cause: " + ex.getCause());
                Logger.getLogger(MAS_VLDB.class.getName()).log(Level.SEVERE, null, ex);
                start += 100;
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(MAS_VLDB.class.getName()).log(Level.SEVERE, null, ex1);
                }

            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage() + " Cause: " + ex.getCause());
                Logger.getLogger(MAS_VLDB.class.getName()).log(Level.SEVERE, null, ex);
                start += 100;
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(MAS_VLDB.class.getName()).log(Level.SEVERE, null, ex1);
                }

            }
        }
    }

    public static void getPaperAuthors(int start, String papers_filter, String csvFile, String jsonFile) {
        String csv_file_path = csvFile;
        String json_dump_file_path = jsonFile;
        String url = "https://api.datamarket.azure.com/MRC/MicrosoftAcademic/v2/Paper_Author?$select=PaperID,SeqID,AuthorID,Name,AffiliationID&$filter=" + papers_filter + "&$format=json";
//        String url = "https://api.datamarket.azure.com/MRC/MicrosoftAcademic/v2/Paper_Ref?$select=SrcID,DstID,SeqID&$filter=" + papers_filter + "&$format=json";
        while (true) {
            IOUtils.writeDataIntoFile(start + "", paper_last, false);
            try {
                StringBuilder csv_str = new StringBuilder();
                final String json = getData2(url, start);
//                System.out.println("json=" + json);
                if (json == null) {
                    System.out.println("json is null. skip. old start=" + start);
                    start += 100;
                    Thread.sleep(1000L);
                    continue;
                }
                JSONParser parser = new JSONParser();
                JSONObject jsonObj = (JSONObject) parser.parse(json);
                final JSONObject dObj = (JSONObject) jsonObj.get("d");
                final JSONArray results = (JSONArray) dObj.get("results");
                if (results.size() == 0) {
                    System.out.println("results is Empty, break.");
                    break;
                } else {
                    System.out.println("Paper: start = " + start + " results# = " + results.size());
                    for (Object paper : results) {
                        JSONObject paperObj = (JSONObject) paper;
                        Long paperID = (Long) paperObj.get("PaperID");
                        Long seqID = (Long) paperObj.get("SeqID");
                        Long authorID = (Long) paperObj.get("AuthorID");
                        String name = normalized((String) paperObj.get("Name"));
//                        String affiliation = normalized((String) paperObj.get("Affiliation"));
                        Long affiliationID = (Long) paperObj.get("AffiliationID");
                        csv_str
                                .append(paperID).append(SEPERATOR)
                                .append(seqID).append(SEPERATOR)
                                .append(authorID).append(SEPERATOR)
                                .append(name).append(SEPERATOR)
                                .append(affiliationID).append(NEWLINE);
                    }
                    IOUtils.writeDataIntoFile(json + "\n", json_dump_file_path);
                    IOUtils.writeDataIntoFile(csv_str.toString(), csv_file_path);
                    start += 100;
                    Thread.sleep(300L);
                }
//                System.out.println("json= " + jsonObj);
            } catch (ParseException ex) {
                System.out.println(ex.getMessage() + " Cause: " + ex.getCause());
                Logger.getLogger(MAS_VLDB.class.getName()).log(Level.SEVERE, null, ex);
                start += 100;
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(MAS_VLDB.class.getName()).log(Level.SEVERE, null, ex1);
                }

            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage() + " Cause: " + ex.getCause());
                Logger.getLogger(MAS_VLDB.class.getName()).log(Level.SEVERE, null, ex);
                start += 100;
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(MAS_VLDB.class.getName()).log(Level.SEVERE, null, ex1);
                }

            }
        }
    }

    public static String paper_last = "data/paper.last";

    private static List<Integer> getPapersIdsFromPaperCatFile(String file) {
        final List<String> lines = IOUtils.readFileLineByLine(file, false);
        List<Integer> paper_ids = new ArrayList<>();
        boolean added = true;
        for (String l : lines) {
            String[] split = l.split("\\t");
            if (split.length > 0) {
                int id = Integer.parseInt(split[0]);
//                if (id == 4972689) {
//                    added = true;
//                }
                if (added) {
                    paper_ids.add(id);
                }
            }
        }
        return paper_ids;
    }

    private static SortedSet<Integer> getAuthorIdsFromPaperAuthorFile(String file) {
        final List<String> lines = IOUtils.readFileLineByLine(file, false);
        SortedSet<Integer> author_ids = new TreeSet<>();
        boolean added = true;
        for (String l : lines) {
            String[] split = l.split("\\t");
            if (split.length > 3) {
                int id = Integer.parseInt(split[2]);
//                if (id == 4972689) {
//                    added = true;
//                }
                if (added) {
                    author_ids.add(id);
                }
            }
        }
        return author_ids;
    }

    public static void extractPaperCategory(int start, int domain, int subdomain) {
        String csv_file_path = "data/papers_d" + domain + "_sd" + subdomain + ".csv";
        String json_dump_file_path = "data/papers_dump_d" + domain + "_sd" + subdomain + ".json";
        String url = "https://api.datamarket.azure.com/MRC/MicrosoftAcademic/v2/Paper_Category?$filter=DomainID%20eq%20" + domain + "%20and%20SubDomainID%20eq%20" + subdomain + "&$format=json";
//        String url = "https://api.datamarket.azure.com/MRC/MicrosoftAcademic/v2/Paper?$select=ID,DocType,Title,Year,ConfID,JourID&$filter=Year%20gt%202001&$format=json";
        while (true) {
            IOUtils.writeDataIntoFile(start + "", paper_last, false);
            try {
                StringBuilder csv_str = new StringBuilder();
                final String json = getData2(url, start);
//                System.out.println("json=" + json);
                if (json == null) {
                    System.out.println("json is null. skip. old start=" + start);
                    start += 100;
                    Thread.sleep(1000L);
                    continue;
                }
                JSONParser parser = new JSONParser();
                JSONObject jsonObj = (JSONObject) parser.parse(json);
                final JSONObject dObj = (JSONObject) jsonObj.get("d");
                final JSONArray results = (JSONArray) dObj.get("results");
                if (results.size() == 0) {
                    System.out.println("results is Empty, break.");
                    break;
                } else {
                    System.out.println("Paper: start = " + start + " results# = " + results.size());
                    for (Object paper : results) {
                        JSONObject paperObj = (JSONObject) paper;
                        Long cPaperID = (Long) paperObj.get("CPaperID");
                        Long domainID = (Long) paperObj.get("DomainID");
                        Long subDomainID = (Long) paperObj.get("SubDomainID");
                        csv_str.append(cPaperID).append(SEPERATOR).append(domainID)
                                .append(SEPERATOR).append(subDomainID).append(NEWLINE);
                    }
                    IOUtils.writeDataIntoFile(json + "\n", json_dump_file_path);
                    IOUtils.writeDataIntoFile(csv_str.toString(), csv_file_path);
                    start += 100;
                    Thread.sleep(400L);
                }
//                System.out.println("json= " + jsonObj);
            } catch (ParseException ex) {
                System.out.println(ex.getMessage() + " Cause: " + ex.getCause());
                Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
                start += 100;
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex1);
                }

            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage() + " Cause: " + ex.getCause());
                Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
                start += 100;
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex1);
                }

            }
        }
    }

    public static void extractPapers(int start) {
        String csv_file_path = "data/papers_" + start + ".csv";
        String json_dump_file_path = "data/papers_dump_" + start + ".json";
        String url = "https://api.datamarket.azure.com/MRC/MicrosoftAcademic/v2/Paper?$select=ID,DocType,Title,Year,ConfID,JourID&$filter=Year%20gt%202001&$format=json";
        while (true) {
            IOUtils.writeDataIntoFile(start + "", paper_last, false);
            try {
                StringBuilder csv_str = new StringBuilder();
                final String json = getData2(url, start);
//                System.out.println("json=" + json);
                if (json == null) {
                    System.out.println("json is null. skip. old start=" + start);
                    start += 100;
                    Thread.sleep(1000L);
                    continue;
                }
                JSONParser parser = new JSONParser();
                JSONObject jsonObj = (JSONObject) parser.parse(json);
                final JSONObject dObj = (JSONObject) jsonObj.get("d");
                final JSONArray results = (JSONArray) dObj.get("results");
                if (results.size() == 0) {
                    System.out.println("results is Empty, break.");
                    break;
                } else {
                    System.out.println("Paper: start = " + start + " results# = " + results.size());
                    for (Object paper : results) {
                        JSONObject paperObj = (JSONObject) paper;
                        Long docType = (Long) paperObj.get("DocType");
                        Long year = (Long) paperObj.get("Year");
                        Long jourID = (Long) paperObj.get("JourID");
                        Long confID = (Long) paperObj.get("ConfID");
                        Long id = (Long) paperObj.get("ID");
                        String title = (String) paperObj.get("Title");
                        title = normalized(title);
                        csv_str.append(id)
                                .append(SEPERATOR).append(docType).append(SEPERATOR).append(year)
                                .append(SEPERATOR).append(jourID).append(SEPERATOR)
                                .append(confID).append(SEPERATOR).append(title).append(NEWLINE);
                    }
                    IOUtils.writeDataIntoFile(json + "\n", json_dump_file_path);
                    IOUtils.writeDataIntoFile(csv_str.toString(), csv_file_path);
                    start += 100;
                    Thread.sleep(400L);
                }
//                System.out.println("json= " + jsonObj);
            } catch (ParseException ex) {
                System.out.println(ex.getMessage() + " Cause: " + ex.getCause());
                Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
                start += 100;
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex1);
                }

            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage() + " Cause: " + ex.getCause());
                Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
                start += 100;
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex1);
                }

            }
        }
    }

    public static void getPapers(int start, String papers_filter, String csvFile, String jsonFile) {
        String csv_file_path = csvFile;
        String json_dump_file_path = jsonFile;
        String url = "https://api.datamarket.azure.com/MRC/MicrosoftAcademic/v2/Paper?$select=ID,Title&$filter=" + papers_filter + "&$format=json";
        while (true) {
            IOUtils.writeDataIntoFile(start + "", paper_last, false);
            try {
                StringBuilder csv_str = new StringBuilder();
                final String json = getData2(url, start);
//                System.out.println("json=" + json);
                if (json == null) {
                    System.out.println("json is null. skip. old start=" + start);
                    start += 100;
                    Thread.sleep(1000L);
                    continue;
                }
                JSONParser parser = new JSONParser();
                JSONObject jsonObj = (JSONObject) parser.parse(json);
                final JSONObject dObj = (JSONObject) jsonObj.get("d");
                final JSONArray results = (JSONArray) dObj.get("results");
                if (results.size() == 0) {
                    System.out.println("results is Empty, break.");
                    break;
                } else {
                    System.out.println("Paper: start = " + start + " results# = " + results.size());
                    for (Object paper : results) {
                        JSONObject paperObj = (JSONObject) paper;
//                        Long docType = (Long) paperObj.get("DocType");
//                        Long year = (Long) paperObj.get("Year");
//                        Long jourID = (Long) paperObj.get("JourID");
//                        Long confID = (Long) paperObj.get("ConfID");
                        Long id = (Long) paperObj.get("ID");
                        String title = (String) paperObj.get("Title");
                        title = normalized(title);
                        csv_str.append(id).append(SEPERATOR).append(title).append(NEWLINE);
                    }
                    IOUtils.writeDataIntoFile(json + "\n", json_dump_file_path);
                    IOUtils.writeDataIntoFile(csv_str.toString(), csv_file_path);
                    start += 100;
                    Thread.sleep(400L);
                }
//                System.out.println("json= " + jsonObj);
            } catch (ParseException ex) {
                System.out.println(ex.getMessage() + " Cause: " + ex.getCause());
                Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
                start += 100;
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex1);
                }

            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage() + " Cause: " + ex.getCause());
                Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
                start += 100;
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex1);
                }

            }
        }
    }

    public static void extractAffilition(int start) {
        String file_prefix = "affilitions";
        String csv_file_path = "data/" + file_prefix + ".csv";
        String json_dump_file_path = "data/" + file_prefix + "_dump.json";
        String url = "https://api.datamarket.azure.com/MRC/MicrosoftAcademic/v2/Affiliation?";
        url += "$format=json";
        while (true) {
            try {
                StringBuilder csv_str = new StringBuilder();
                final String json = getData2(url, start);
                JSONParser parser = new JSONParser();
                JSONObject jsonObj = (JSONObject) parser.parse(json);
                final JSONObject dObj = (JSONObject) jsonObj.get("d");
                final JSONArray results = (JSONArray) dObj.get("results");
                if (results.size() == 0) {
                    System.out.println("results is Empty, break.");
                    break;
                } else {
                    System.out.println("Affilition: start = " + start + " results# = " + results.size());
                    for (Object paper : results) {
                        JSONObject paperObj = (JSONObject) paper;
                        Long id = (Long) paperObj.get("ID");
                        String officialName = normalized((String) paperObj.get("OfficialName"));
                        String displayName = normalized((String) paperObj.get("DisplayName"));
                        String nativeName = normalized((String) paperObj.get("NativeName"));
                        Long parentID = (Long) paperObj.get("ParentID");
                        String homepage = normalized((String) paperObj.get("Homepage"));
                        String shortName = normalized((String) paperObj.get("ShortName"));
                        Long type = (Long) paperObj.get("Type");
                        csv_str
                                .append(id).append(SEPERATOR)
                                .append(officialName).append(SEPERATOR)
                                .append(displayName).append(SEPERATOR)
                                .append(nativeName).append(SEPERATOR)
                                .append(parentID).append(SEPERATOR)
                                .append(homepage).append(SEPERATOR)
                                .append(shortName).append(SEPERATOR)
                                .append(type).append(NEWLINE);
                    }
                    IOUtils.writeDataIntoFile(json + "\n", json_dump_file_path);
                    IOUtils.writeDataIntoFile(csv_str.toString(), csv_file_path);
                    start += 100;
                    Thread.sleep(300L);
                }
//                System.out.println("json= " + jsonObj);
            } catch (ParseException ex) {
                Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void extractAuthor(int start) {
        String file_prefix = "authors";
        String csv_file_path = "data/" + file_prefix + ".csv";
        String json_dump_file_path = "data/" + file_prefix + "_dump.json";
        String url = "https://api.datamarket.azure.com/MRC/MicrosoftAcademic/v2/Author?$select=ID,Name,NativeName,Affiliation,AffiliationID,Version";
        url += "&$format=json";
        while (true) {
            try {
                StringBuilder csv_str = new StringBuilder();
                final String json = getData2(url, start);
                JSONParser parser = new JSONParser();
                JSONObject jsonObj = (JSONObject) parser.parse(json);
                final JSONObject dObj = (JSONObject) jsonObj.get("d");
                final JSONArray results = (JSONArray) dObj.get("results");
                if (results.size() == 0) {
                    System.out.println("results is Empty, break.");
                    break;
                } else {
                    System.out.println("Author: start = " + start + " results# = " + results.size());
                    for (Object paper : results) {
                        JSONObject paperObj = (JSONObject) paper;
                        Long id = (Long) paperObj.get("ID");
                        String name = normalized((String) paperObj.get("Name"));
                        String nativeName = normalized((String) paperObj.get("NativeName"));
                        String affiliation = normalized((String) paperObj.get("Affiliation"));
                        Long affiliationID = (Long) paperObj.get("AffiliationID");
                        Long version = (Long) paperObj.get("Version");
                        csv_str
                                .append(id).append(SEPERATOR)
                                .append(name).append(SEPERATOR)
                                .append(nativeName).append(SEPERATOR)
                                .append(affiliation).append(SEPERATOR)
                                .append(affiliationID).append(SEPERATOR)
                                .append(version).append(NEWLINE);
                    }
                    IOUtils.writeDataIntoFile(json + "\n", json_dump_file_path);
                    IOUtils.writeDataIntoFile(csv_str.toString(), csv_file_path);
                    start += 100;
                    Thread.sleep(300L);
                }
//                System.out.println("json= " + jsonObj);
            } catch (ParseException ex) {
                Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void extractConference(int start) {
        String file_prefix = "conferences";
        String csv_file_path = "data/" + file_prefix + ".csv";
        String json_dump_file_path = "data/" + file_prefix + "_dump.json";
        String url = "https://api.datamarket.azure.com/MRC/MicrosoftAcademic/v2/Conference?";
        url += "$format=json";
        while (true) {
            try {
                StringBuilder csv_str = new StringBuilder();
                final String json = getData2(url, start);
                JSONParser parser = new JSONParser();
                JSONObject jsonObj = (JSONObject) parser.parse(json);
                final JSONObject dObj = (JSONObject) jsonObj.get("d");
                final JSONArray results = (JSONArray) dObj.get("results");
                if (results.size() == 0) {
                    System.out.println("results is Empty, break.");
                    break;
                } else {
                    System.out.println("Conference: start = " + start + " results# = " + results.size());
                    for (Object paper : results) {
                        JSONObject paperObj = (JSONObject) paper;
                        Long id = (Long) paperObj.get("ID");
                        String shortName = normalized((String) paperObj.get("ShortName"));
                        String fullName = normalized((String) paperObj.get("FullName"));
                        String homepage = normalized((String) paperObj.get("Homepage"));
                        csv_str
                                .append(id).append(SEPERATOR)
                                .append(shortName).append(SEPERATOR)
                                .append(fullName).append(SEPERATOR)
                                .append(homepage).append(NEWLINE);
                    }
                    IOUtils.writeDataIntoFile(json + "\n", json_dump_file_path);
                    IOUtils.writeDataIntoFile(csv_str.toString(), csv_file_path);
                    start += 100;
                    Thread.sleep(300L);
                }
//                System.out.println("json= " + jsonObj);
            } catch (ParseException ex) {
                Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void extractJournal(int start) {
        String file_prefix = "journals";
        String csv_file_path = "data/" + file_prefix + ".csv";
        String json_dump_file_path = "data/" + file_prefix + "_dump.json";
        String url = "https://api.datamarket.azure.com/MRC/MicrosoftAcademic/v2/Journal?";
        url += "$format=json";
        while (true) {
            try {
                StringBuilder csv_str = new StringBuilder();
                final String json = getData2(url, start);
                JSONParser parser = new JSONParser();
                JSONObject jsonObj = (JSONObject) parser.parse(json);
                final JSONObject dObj = (JSONObject) jsonObj.get("d");
                final JSONArray results = (JSONArray) dObj.get("results");
                if (results.size() == 0) {
                    System.out.println("results is Empty, break.");
                    break;
                } else {
                    System.out.println("Journals: start = " + start + " results# = " + results.size());
                    for (Object paper : results) {
                        JSONObject paperObj = (JSONObject) paper;
                        Long id = (Long) paperObj.get("ID");
                        String shortName = normalized((String) paperObj.get("ShortName"));
                        String fullName = normalized((String) paperObj.get("FullName"));
                        String homepage = normalized((String) paperObj.get("Homepage"));
                        csv_str
                                .append(id).append(SEPERATOR)
                                .append(shortName).append(SEPERATOR)
                                .append(fullName).append(SEPERATOR)
                                .append(homepage).append(NEWLINE);
                    }
                    IOUtils.writeDataIntoFile(json + "\n", json_dump_file_path);
                    IOUtils.writeDataIntoFile(csv_str.toString(), csv_file_path);
                    start += 100;
                    Thread.sleep(300L);
                }
//                System.out.println("json= " + jsonObj);
            } catch (ParseException ex) {
                Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void extractPaper_Author(int start) {
        String file_prefix = "paper_authors";
        String csv_file_path = "data/" + file_prefix + ".csv";
        String json_dump_file_path = "data/" + file_prefix + "_dump.json";
        String url = "https://api.datamarket.azure.com/MRC/MicrosoftAcademic/v2/Paper_Author?";
        url += "$format=json";
        while (true) {
            try {
                StringBuilder csv_str = new StringBuilder();
                final String json = getData2(url, start);
                JSONParser parser = new JSONParser();
                JSONObject jsonObj = (JSONObject) parser.parse(json);
                final JSONObject dObj = (JSONObject) jsonObj.get("d");
                final JSONArray results = (JSONArray) dObj.get("results");
                if (results.size() == 0) {
                    System.out.println("results is Empty, break.");
                    break;
                } else {
                    System.out.println("Paper_Author: start = " + start + " results# = " + results.size());
                    for (Object paper : results) {
                        JSONObject paperObj = (JSONObject) paper;
                        Long paperID = (Long) paperObj.get("PaperID");

                        Long seqID = (Long) paperObj.get("SeqID");

                        Long authorID = (Long) paperObj.get("authorID");
                        String name = normalized((String) paperObj.get("Name"));
                        String affiliation = normalized((String) paperObj.get("Affiliation"));

                        Long affiliationID = (Long) paperObj.get("AffiliationID");
                        csv_str
                                .append(paperID).append(SEPERATOR)
                                .append(seqID).append(SEPERATOR)
                                .append(authorID).append(SEPERATOR)
                                .append(name).append(SEPERATOR)
                                .append(affiliation).append(SEPERATOR)
                                .append(affiliationID).append(NEWLINE);
                    }
                    IOUtils.writeDataIntoFile(json + "\n", json_dump_file_path);
                    IOUtils.writeDataIntoFile(csv_str.toString(), csv_file_path);
                    start += 100;
                    Thread.sleep(300L);
                }
//                System.out.println("json= " + jsonObj);
            } catch (ParseException ex) {
                Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void extractPaper_Category(int start) {
        String file_prefix = "paper_categories";
        String csv_file_path = "data/" + file_prefix + ".csv";
        String json_dump_file_path = "data/" + file_prefix + "_dump.json";
        String url = "https://api.datamarket.azure.com/MRC/MicrosoftAcademic/v2/Paper_Category?";
        url += "$format=json";
        while (true) {
            try {
                StringBuilder csv_str = new StringBuilder();
                final String json = getData2(url, start);
                if (json == null) {
                    System.out.println("json is null. skip. old start=" + start);
                    start += 100;
                    Thread.sleep(5000L);
                    continue;
                }
                JSONParser parser = new JSONParser();
                JSONObject jsonObj = (JSONObject) parser.parse(json);
                final JSONObject dObj = (JSONObject) jsonObj.get("d");
                final JSONArray results = (JSONArray) dObj.get("results");
                if (results.size() == 0) {
                    System.out.println("results is Empty, break.");
                    break;
                } else {
                    System.out.println("Paper_Category: start = " + start + " results# = " + results.size());
                    for (Object paper : results) {
                        JSONObject paperObj = (JSONObject) paper;
                        Long cPaperID = (Long) paperObj.get("CPaperID");

                        Long domainID = (Long) paperObj.get("DomainID");

                        Long subDomainID = (Long) paperObj.get("SubDomainID");
                        csv_str
                                .append(cPaperID).append(SEPERATOR)
                                .append(domainID).append(SEPERATOR)
                                .append(subDomainID).append(NEWLINE);
                    }
                    IOUtils.writeDataIntoFile(json + "\n", json_dump_file_path);
                    IOUtils.writeDataIntoFile(csv_str.toString(), csv_file_path);
                    start += 100;
                    Thread.sleep(300L);
                }
//                System.out.println("json= " + jsonObj);
            } catch (ParseException ex) {
                Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static String getData2(String url_org, int start) {

        try {
            String complete_url = url_org + "&$skip=" + start;
//            String url_str = generateURL(url_org, prop);
            Document doc = Jsoup.connect(complete_url).timeout(25000).ignoreContentType(true).get();
            return doc.text();
        } catch (IOException ex) {
            System.out.println(ex.getMessage() + " Cause: " + ex.getCause());
            Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @param args the command line arguments
     */
    public static String getData_old(String url_org, int start) {
        try {
            String complete_url = url_org + "&$skip=" + start;
//            String url_str = generateURL(url_org, prop);
            URL url = new URL(complete_url);
            URLConnection yc = url.openConnection();
            yc.setConnectTimeout(25 * 1000);
            yc.setReadTimeout(25 * 1000);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream()));
            String inputLine;
            StringBuffer result = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
//                System.out.println(inputLine);
                result.append(inputLine);
            }
            in.close();
            return result.toString();
        } catch (MalformedURLException ex) {
            Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @param args the command line arguments
     */
    // for old version
    public static void getData(int startIdx, int endIdx) {
        try {
            Properties prop = new Properties();
            prop.setProperty("AppId", "1df63064-efad-4bbd-a797-1131499b7728");
            prop.setProperty("ResultObjects", "publication");
            prop.setProperty("DomainID", "22");
            prop.setProperty("SubDomainID", "2");
            prop.setProperty("YearStart", "2001");
            prop.setProperty("YearEnd", "2010");
            prop.setProperty("StartIdx", startIdx + "");
            prop.setProperty("EndIdx", endIdx + "");

            String url_org = "http://academic.research.microsoft.com/json.svc/search";
            String url_str = generateURL(url_org, prop);
            URL url = new URL(url_str);
            URLConnection yc = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }
            in.close();
        } catch (MalformedURLException ex) {
            Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MAS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String generateURL(String url_org, Properties prop) {
        String url = url_org + "?";
        for (Object k : prop.keySet()) {
            String key = (String) k;
            String val = prop.getProperty(key);
            url = url + key + "=" + val + "&";
        }
        return url.substring(0, url.length() - 1);
    }

    private static String normalized(String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
        return str.replaceAll("\\s", " ").replaceAll("\\n", " ");
    }

}
