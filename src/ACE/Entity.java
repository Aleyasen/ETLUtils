/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACE;

/**
 *
 * @author Aale
 */
public class Entity {

    String entId;
    String mentionId;
    String type;
    String text;
    int start;
    int end;

    public String getId() {
        return entId + "-" + mentionId;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getEntId() {
        return entId;
    }

    public void setEntId(String entId) {
        this.entId = entId;
    }

    public String getMentionId() {
        return mentionId;
    }

    public void setMentionId(String mentionId) {
        this.mentionId = mentionId;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
