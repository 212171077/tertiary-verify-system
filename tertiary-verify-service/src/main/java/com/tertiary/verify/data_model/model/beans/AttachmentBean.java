package com.tertiary.verify.data_model.model.beans;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;

public class AttachmentBean {

  private String fileName;
  private String contentType;
  private InputStreamSource inputStreamSource;

  public AttachmentBean() {
    super();
    // TODO Auto-generated constructor stub
  }

  public AttachmentBean(String fileName, String contentType, InputStreamSource inputStreamSource) {
    super();
    this.fileName = fileName;
    this.contentType = contentType;
    this.inputStreamSource = inputStreamSource;
  }

  public AttachmentBean(String fileName, String contentType, byte[] output) {
    super();
    this.fileName = fileName;
    this.contentType = contentType;
    this.inputStreamSource = new ByteArrayResource(output);
  }

  /**
   * @return the fileName
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * @param fileName the fileName to set
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /**
   * @return the contentType
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * @param contentType the contentType to set
   */
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  /**
   * @return the inputStreamSource
   */
  public InputStreamSource getInputStreamSource() {
    return inputStreamSource;
  }

  /**
   * @param inputStreamSource the inputStreamSource to set
   */
  public void setInputStreamSource(InputStreamSource inputStreamSource) {
    this.inputStreamSource = inputStreamSource;
  }
}
