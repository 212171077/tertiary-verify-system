package com.tertiary.verify.service;

import com.tertiary.verify.data_model.model.beans.ContactUsRequest;
import com.tertiary.verify.data_model.model.beans.Mail;
import com.tertiary.verify.data_model.model.constants.AppConstants;
import com.tertiary.verify.data_model.model.entities.ContactUs;
import com.tertiary.verify.data_model.model.entities.lookup.AppConfig;
import com.tertiary.verify.data_model.model.repositories.ContactUsRepository;
import com.tertiary.verify.framework.AbstractService;
import com.tertiary.verify.mail.MailSender;
import com.tertiary.verify.utils.CommonUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ContactUsService extends AbstractService {
  @Autowired ContactUsRepository repository;
  @Autowired MailSender mailSender;
  @Autowired TertiaryVerifyService tertiaryVerifyService;

  @CacheEvict(value = "contact_us", allEntries = true)
  public void saveContactUs(ContactUs contactUs) {
    if (contactUs.getId() == null) {
      contactUs.setCreateDate(new Date());
    } else {
      contactUs.setLastUpdateDate(new Date());
    }
    sendContactUsMessage(contactUs);
  }

  @CacheEvict(value = "contact_us", allEntries = true)
  public void deleteContactUs(ContactUs contactUs) {
    repository.delete(contactUs);
  }

  @CacheEvict(value = "contact_us", allEntries = true)
  public void deleteContactUsByID(Integer arg0) {
    repository.deleteById(arg0);
  }

  @Cacheable("contact_us")
  public List<ContactUs> findAllContactUs() {
    return repository.findAll();
  }

  public ContactUs findById(Long parseLong) {
    return repository.findById(parseLong);
  }

  public ContactUs sendContactUsMessage(ContactUs contactUs) {
    contactUs.setPhoneNumber("0" + contactUs.getPhoneNumber());
    repository.save(contactUs);
    sendEmailToAdmin(contactUs);
    sendConfirmationEmail(contactUs);
    return contactUs;
  }

  public void sendContactUsMessage(ContactUsRequest contactUsRequest) {
    String phone =
        (contactUsRequest.getCellNumber().startsWith("0")
            ? contactUsRequest.getCellNumber()
            : "0".concat(contactUsRequest.getCellNumber()));
    ContactUs contactUs =
        ContactUs.builder()
            .fullName(contactUsRequest.getFullName())
            .email(contactUsRequest.getEmail())
            .phoneNumber(phone)
            .message(contactUsRequest.getMessage())
            .build();
    repository.save(contactUs);
    sendEmailToAdmin(contactUs);
    sendConfirmationEmail(contactUs);
  }

  private void sendConfirmationEmail(ContactUs contactUs) {
    String welcome =
        "<p>Dear "
            + contactUs.getFullName()
            + ",</p>"
            + "<p>We appreciate you taking the time to contact us. "
            + "We have received your message and would like to thank "
            + "you for bringing this to our attention.</p>"
            + "<p>Please be assured that we are reviewing your message and if need be, "
            + "we will respond as soon as possible. In the meantime, if you have any "
            + "additional information that may be relevant to your inquiry,"
            + " please do not hesitate to contact us.</p>";

    Mail mail = new Mail();
    mail.setContent(welcome);
    mail.setFrom(AppConstants.FROM_EMAIL);
    String[] to = {contactUs.getEmail()};
    mail.setTo(to);
    mail.setSubject("Thank you for Contacting us");
    mail.setCc(to);
    mailSender.saveEmail(mail);
  }

  private void sendEmailToAdmin(ContactUs contactUs) {
    List<AppConfig> contactUsList = tertiaryVerifyService.findAppConfigsByCode("CONTACT_US");
    String welcome =
        "<p>Dear Administrator,</p>"
            + "<p>I trust this message finds you well. " +
                "I would like to inform you that a new message " +
                "has been submitted via the Contact Us form on the " +
                "Tertiary Verify website/app. " +
                "Below are the details of the submission:</p>\n"
            + "<ul>"
            + "<li><strong>Full Name:</strong> "
            + contactUs.getFullName()
            + "</li>"
            + "<li><strong>Email:</strong> "
            + contactUs.getEmail()
            + "</li>"
            + "<li><strong>Phone Number:</strong> "
            + contactUs.getPhoneNumber()
            + "</li>"
            + "<li><strong>Message:</strong> "
            + contactUs.getMessage()
            + "</li>"
            + "</ul>"
            + "<p>Please take the necessary actions and respond " +
                "to this message at your earliest convenience, " +
                "should it be required.</p>"
            +"<p>Thank you for your prompt attention to this matter.</p>";
    Mail mail = new Mail();
    mail.setContent(welcome);
    mail.setFrom(AppConstants.FROM_EMAIL);
    List<String> toList = new ArrayList<>();
    if (contactUsList != null && !contactUsList.isEmpty()) {
      for (AppConfig appConfig : contactUsList) {
        toList.add(appConfig.getValue().trim());
      }
    } else {
      toList.add("venussibiya@gmail.com");
    }
    String[] to = new String[toList.size()];
    to = toList.toArray(to);
    mail.setTo(to);
    mail.setSubject("Tertiary Verify Contact us");
    mail.setCc(to);
    mailSender.saveEmail(mail);
  }
}
