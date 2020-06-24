package business.processor.bean;

import java.util.Date;

public class BakReverseLog {
    private String qn;
    private String mn;
    private int type;
    private String note;
    private String cmd;
    private int tag;
    private String results;
    private String operatorID;
    private String operatorName;
    private Date createTime;
    private Date updateTime;

    public BakReverseLog() {
    }

    public String getQn() {
        return this.qn;
    }

    public void setQn(String qn) {
        this.qn = qn;
    }

    public String getMn() {
        return this.mn;
    }

    public void setMn(String mn) {
        this.mn = mn;
    }

    public String getOperatorID() {
        return this.operatorID;
    }

    public void setOperatorID(String operatorID) {
        this.operatorID = operatorID;
    }

    public String getOperatorName() {
        return this.operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCmd() {
        return this.cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public int getTag() {
        return this.tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getResults() {
        return this.results;
    }

    public void setResults(String results) {
        this.results = results;
    }
}