package com.luoheng.miu.bean;

import java.util.Date;

public class DiscussComment {
    private String discussId;
    private String userMail;
    private String content;
    private Date createDate;

    public DiscussComment() {
    }

    public DiscussComment(String discussId, String userMail, String content, Date createDate) {
        this.discussId = discussId;
        this.userMail = userMail;
        this.content = content;
        this.createDate = createDate;
    }

    public String getDiscussId() {
        return discussId;
    }

    public void setDiscussId(String discussId) {
        this.discussId = discussId;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
