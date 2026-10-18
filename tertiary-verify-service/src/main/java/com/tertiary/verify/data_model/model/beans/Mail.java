package com.tertiary.verify.data_model.model.beans;

import java.util.List;
import org.springframework.core.io.FileSystemResource;

public class Mail {
  private static final long serialVersionUID = 1L;

  private Integer id;
  private String from;
  private String[] to;
  private String[] cc;
  private String subject;
  private String content;
  private List<FileSystemResource> attachments;
  private List<AttachmentBean> attachmentBeanList;

  public Mail() {
    content = "text/plain";
  }

  public Mail(
      Integer id,
      String from,
      String[] to,
      String[] cc,
      String subject,
      String content,
      List<FileSystemResource> attachments) {
    super();
    this.id = id;
    this.from = from;
    this.to = to;
    this.cc = cc;
    this.subject = subject;
    this.content = content;
    this.attachments = attachments;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public List<FileSystemResource> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<FileSystemResource> attachments) {
    this.attachments = attachments;
  }

  @Override
  public String toString() {
    return "Mail{"
        + "from='"
        + from
        + '\''
        + ", to='"
        + to
        + '\''
        + ", subject='"
        + subject
        + '\''
        + ", content='"
        + content
        + '\''
        + '}';
  }

  public String[] getTo() {
    return to;
  }

  public void setTo(String[] to) {
    this.to = to;
  }

  public String[] getCc() {
    return cc;
  }

  public void setCc(String[] cc) {
    this.cc = cc;
  }

  /**
   * @return the attachmentBeanList
   */
  public List<AttachmentBean> getAttachmentBeanList() {
    return attachmentBeanList;
  }

  /**
   * @param attachmentBeanList the attachmentBeanList to set
   */
  public void setAttachmentBeanList(List<AttachmentBean> attachmentBeanList) {
    this.attachmentBeanList = attachmentBeanList;
  }
}
