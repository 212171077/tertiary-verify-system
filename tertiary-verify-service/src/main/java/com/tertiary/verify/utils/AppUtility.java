package com.tertiary.verify.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.tertiary.verify.data_model.model.constants.AppConstants;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.*;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

public class AppUtility implements Serializable {

  /** The Constant sdf. */
  public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

  public static final SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyy/MM/dd");

  /** The Constant logger. */
  protected static final Log logger = LogFactory.getLog(AppUtility.class);

  private static final AppConstants appConstants = new AppConstants();

  /**
   * Adds the days to date.
   *
   * @param start the start
   * @param days the days
   * @return the date
   */
  public static Date addDaysToDate(Date start, int days) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(start);
    cal.add(Calendar.DATE, days);
    return cal.getTime();
  }

  /**
   * Adds the days to date exclude weekends.
   *
   * @param start the start
   * @param days the days
   * @return the date
   */
  public static Date addDaysToDateExcludeWeekends(Date start, int days) {
    if (days < 1) {
      return start;
    }
    LocalDate date = LocalDate.parse(sdf.format(start));
    LocalDate result = date;

    int addedDays = 0;
    while (addedDays < days) {
      result = result.plusDays(1);
      if (!(result.getDayOfWeek() == DayOfWeek.SATURDAY.getValue()
          || result.getDayOfWeek() == DayOfWeek.SUNDAY.getValue())) {
        ++addedDays;
      }
    }

    return result.toDate();
  }

  /**
   * Deduct days from date.
   *
   * @param start the start
   * @param days the days
   * @return the date
   */
  public static Date deductDaysFromDate(Date start, int days) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(start);
    cal.add(Calendar.DATE, -days);
    return cal.getTime();
  }

  /**
   * Gets the first day of this month.
   *
   * @return the first day of this month
   */
  public static Date getFirstDayOfThisMonth() {
    return (new LocalDate().withDayOfMonth(1)).toDate();
  }

  /**
   * Gets the lastt day of this month.
   *
   * @return the lastt day of this month
   */
  public static Date getLasttDayOfThisMonth() {
    return (new LocalDate().dayOfMonth().withMaximumValue()).toDate();
  }

  /**
   * Gets the lastt day of date.
   *
   * @param date the date
   * @return the lastt day of date
   */
  public static Date getLasttDayOfDate(Date date) {
    return (new LocalDate(date).dayOfMonth().withMaximumValue()).toDate();
  }

  public static Date getLasttDayOfYear(Date date) {
    return (new LocalDate(date).dayOfYear().withMaximumValue()).toDate();
  }

  public static Date getFirstDateOfYear(Date date) {
    return (new LocalDate(date).monthOfYear().withMinimumValue().dayOfMonth().withMinimumValue())
        .toDate();
  }

  public static Date getEndOfApril(Date date) {
    return new LocalDateTime(date)
        .withMonthOfYear(4)
        .dayOfMonth()
        .withMaximumValue()
        .withHourOfDay(23)
        .withMinuteOfHour(59)
        .withSecondOfMinute(59)
        .withMillisOfSecond(999)
        .toDate();
  }

  /**
   * Gets the first day of month.
   *
   * @param date the date
   * @return the first day of month
   */
  public static Date getFirstDayOfMonth(Date date) {
    return (new LocalDate(date.getTime()).withDayOfMonth(1)).toDate();
  }

  /**
   * Gets the start of day.
   *
   * @param date the date
   * @return the start of day
   */
  public static Date getStartOfDay(Date date) {
    return (new LocalDateTime(date.getTime())
            .withHourOfDay(0)
            .withMinuteOfHour(0)
            .withSecondOfMinute(0)
            .withMillisOfSecond(0))
        .toDate();
  }

  /**
   * Gets the end of day.
   *
   * @param date the date
   * @return the end of day
   */
  public static Date getEndOfDay(Date date) {
    return (new LocalDateTime(date.getTime())
            .withHourOfDay(23)
            .withMinuteOfHour(59)
            .withSecondOfMinute(59)
            .withMillisOfSecond(999))
        .toDate();
  }

  /**
   * Gets the lastt day of month.
   *
   * @param date the date
   * @return the lastt day of month
   */
  public static Date getLasttDayOfMonth(Date date) {
    return (new LocalDate(date.getTime()).dayOfMonth().withMaximumValue()).toDate();
  }

  /**
   * Gets the second last day.
   *
   * @param date the date
   * @return the second last day
   */
  public static Date getSecondLastDay(Date date) {
    return deductDaysFromDate(getLasttDayOfMonth(addMonthsToDate(date, 1)), 1);
  }

  /**
   * Gets the second last day new.
   *
   * @param date the date
   * @return the second last day new
   */
  public static Date getSecondLastDayNew(Date date) {
    return deductDaysFromDate(getLasttDayOfMonth(date), 1);
  }

  /**
   * Gets the days between dates.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return the days between dates
   */
  public static int getDaysBetweenDates(Date startDate, Date endDate) {
    return Days.daysBetween(new LocalDate(startDate.getTime()), new LocalDate(endDate.getTime()))
        .getDays();
  }

  public static int getHoursBetweenDates(Date startDate, Date endDate) {
    return Hours.hoursBetween(new LocalDate(startDate.getTime()), new LocalDate(endDate.getTime()))
        .getHours();
  }

  /**
   * Gets the months between dates.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return the months between dates
   */
  public static int getMonthsBetweenDates(Date startDate, Date endDate) {
    return Months.monthsBetween(
            new LocalDate(startDate.getTime()), new LocalDate(endDate.getTime()))
        .getMonths();
  }

  /**
   * Gets the months dates.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return the months dates
   */
  public static List<Date> getMonthsDates(Date startDate, Date endDate) {
    int size = getMonthsBetweenDates(getFirstDayOfMonth(startDate), getLasttDayOfMonth(endDate));
    List<Date> dates = new ArrayList<Date>();
    if (size != 0) {
      dates.add(startDate);
    }
    for (int i = 0; i < size; i++) {
      if (i > 0) {
        dates.add(getFirstDayOfMonth(addMonthsToDate(startDate, i)));
      }
    }
    dates.add(endDate);
    return dates;
  }

  /**
   * Gets the years between dates.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return the years between dates
   */
  public static int getYearsBetweenDates(Date startDate, Date endDate) {
    return Years.yearsBetween(new LocalDate(startDate.getTime()), new LocalDate(endDate.getTime()))
        .getYears();
  }

  /**
   * Gets the days between dates exclude weekends.
   *
   * @param start the start
   * @param end the end
   * @return the days between dates exclude weekends
   */
  public static int getDaysBetweenDatesExcludeWeekends(Date start, Date end) {
    DateTime startDateTime = new DateTime(AppUtility.getEndOfDay(start));
    DateTime endDateTime = new DateTime(AppUtility.getEndOfDay(end));

    int dayOfWeek;
    int days = 0;

    while (startDateTime.isBefore(endDateTime)) {
      dayOfWeek = startDateTime.getDayOfWeek();
      if (!(dayOfWeek == DateTimeConstants.SUNDAY || dayOfWeek == DateTimeConstants.SATURDAY)) {
        days++;
      }
      startDateTime = startDateTime.plusDays(1);
    }
    days++;
    return days;
  }

  /**
   * Gets the first day of month one year ago.
   *
   * @return the first day of month one year ago
   */
  public static Date getFirstDayOfMonthOneYearAgo() {
    return (new LocalDate().minusYears(1).withDayOfMonth(1)).toDate();
  }

  /**
   * Start date of current year.
   *
   * @return the date
   */
  public static Date startDateOfCurrentYear() {
    DateTime dt = new DateTime();
    try {
      return (sdf.parse(dt.getYear() + "0101"));

    } catch (ParseException e) {
      logger.fatal(e);
      return null;
    }
  }

  public static Date endDateOfCurrentYear() {
    DateTime dt = new DateTime();
    try {
      return (sdf.parse(dt.getYear() + "1231"));

    } catch (ParseException e) {
      logger.fatal(e);
      return null;
    }
  }

  /**
   * Last date of current year.
   *
   * @return the date
   */
  public static Date lastDateOfCurrentYear() {
    DateTime dt = new DateTime();
    try {
      return (sdf.parse(dt.getYear() + "1231"));

    } catch (ParseException e) {
      logger.fatal(e);
      return null;
    }
  }

  /**
   * Gets the age.
   *
   * @param dob the dob
   * @return the age
   */
  public static int getAge(Date dob) {
    return Years.yearsBetween(new LocalDate(dob.getTime()), new LocalDate()).getYears();
  }

  /**
   * Gen passord.
   *
   * @return the string
   */
  public static String genPassord() {
    return RandomStringUtils.randomAlphabetic(7);
  }

  /**
   * Gets the first day of week.
   *
   * @param date the date
   * @return the first day of week
   */
  public static Date getFirstDayOfWeek(Date date) {
    LocalDate now = new LocalDate(date);
    // System.out.println(now.withDayOfWeek(DateTimeConstants.MONDAY));
    // //prints 2011-01-17
    // System.out.println(now.withDayOfWeek(DateTimeConstants.SUNDAY));
    // //prints 2011-01-23
    return now.withDayOfWeek(1).toDate();
  }

  /**
   * Gets the first day of year.
   *
   * @param date the date
   * @return the first day of year
   */
  public static Date getFirstDayOfYear(Date date) {
    return new DateTime(date).dayOfYear().withMinimumValue().withTimeAtStartOfDay().toDate();
  }

  /**
   * Gets the last day of year.
   *
   * @param date the date
   * @return the last day of year
   */
  public static Date getLastDayOfYear(Date date) {
    return deductDaysFromDate(new DateTime(getFirstDayOfYear(date)).plusYears(1).toDate(), 1);
  }

  /**
   * Gets the last day of week.
   *
   * @param date the date
   * @return the last day of week
   */
  public static Date getLastDayOfWeek(Date date) {
    LocalDate now = new LocalDate(date);
    return now.withDayOfWeek(7).toDate();
  }

  /**
   * Round to precision.
   *
   * @param val the val
   * @param precision the precision
   * @return the big decimal
   */
  public static BigDecimal roundToPrecision(BigDecimal val, int precision) {
    val = val.setScale(precision, RoundingMode.HALF_EVEN);
    return val;
  }

  /**
   * Round to precision.
   *
   * @param value the value
   * @param precision the precision
   * @return the double
   */
  public static Double roundToPrecision(Double value, int precision) {
    BigDecimal val = BigDecimal.valueOf(value);
    val = val.setScale(precision, RoundingMode.HALF_EVEN);
    return val.doubleValue();
  }

  /**
   * Adds the miniutes to date.
   *
   * @param date the date
   * @param minutes the minutes
   * @return the date
   */
  public static Date addMiniutesToDate(Date date, int minutes) {
    if (date == null) return null;
    return (new DateTime(date.getTime()).plusMinutes(minutes)).toDate();
  }

  public static Date addSecondsToDate(Date date, int seconds) {
    if (date == null) return null;
    return (new DateTime(date.getTime()).plusSeconds(seconds)).toDate();
  }

  public static Date addMilliSecondsToDate(Date date, int milli) {
    if (date == null) return null;
    return (new DateTime(date.getTime()).plusMillis(milli)).toDate();
  }

  /**
   * Adds the hours to date.
   *
   * @param date the date
   * @param hours the hours
   * @return the date
   */
  public static Date addHoursToDate(Date date, int hours) {
    if (date == null) return null;
    return (new DateTime(date.getTime()).plusHours(hours)).toDate();
  }

  /**
   * Deduct hours from date.
   *
   * @param date the date
   * @param hours the hours
   * @return the date
   */
  public static Date deductHoursFromDate(Date date, int hours) {
    if (date == null) return null;
    return (new DateTime(date.getTime()).minusHours(hours)).toDate();
  }

  /**
   * Deduct minutes from date.
   *
   * @param date the date
   * @param minutes the minutes
   * @return the date
   */
  public static Date deductMinutesFromDate(Date date, int minutes) {
    if (date == null) return null;
    return (new DateTime(date.getTime()).minusMinutes(minutes)).toDate();
  }

  /**
   * Adds the weeks to date.
   *
   * @param date the date
   * @param weeks the weeks
   * @return the date
   */
  public static Date addWeeksToDate(Date date, int weeks) {
    if (date == null) return null;
    return (new DateTime(date.getTime()).plusWeeks(weeks)).toDate();
  }

  /**
   * Adds the months to date.
   *
   * @param date the date
   * @param months the months
   * @return the date
   */
  public static Date addMonthsToDate(Date date, int months) {
    if (date == null) return null;
    return (new DateTime(date.getTime()).plusMonths(months)).toDate();
  }

  /**
   * Deduct months from date.
   *
   * @param date the date
   * @param months the months
   * @return the date
   */
  public static Date deductMonthsFromDate(Date date, int months) {
    if (date == null) return null;
    return (new DateTime(date.getTime()).minusMonths(months)).toDate();
  }

  /**
   * Adds the years to date.
   *
   * @param date the date
   * @param years the years
   * @return the date
   */
  public static Date addYearsToDate(Date date, int years) {
    if (date == null) return null;
    return (new DateTime(date.getTime()).plusYears(years)).toDate();
  }

  public static Date deductYearsfromDate(Date date, int years) {
    if (date == null) return null;
    return (new DateTime(date.getTime()).minusYears(years)).toDate();
  }

  /**
   * Gets the hours between date.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return the hours between date
   */
  public static Long getHoursBetweenDate(Date startDate, Date endDate) {
    if (startDate == null || endDate == null) return null;
    return (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60);
  }

  /**
   * Gets the month for int.
   *
   * @param num the num
   * @return the month for int
   */
  public static String getMonthForInt(int num) {
    String month = "wrong";
    DateFormatSymbols dfs = new DateFormatSymbols();
    String[] months = dfs.getMonths();
    if (num >= 0 && num <= 11) {
      month = months[num];
    }
    return month;
  }

  /**
   * Removes the last comma.
   *
   * @param string the string
   * @return the string
   */
  public static String removeLastComma(String string) {
    string = string.substring(0, string.lastIndexOf(','));
    return string;
  }

  public static int yearOfDate(Date date) {
    java.time.LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    return localDate.getYear();
  }

  public static int monthOfDate(Date date) {
    java.time.LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    return localDate.getMonthValue();
  }

  public static BufferedImage generateBarcode(String barcode) throws Exception {
    Code39Bean bean = new Code39Bean();
    bean.setHeight(10d);
    bean.doQuietZone(false);
    OutputStream out =
        new FileOutputStream(new File(appConstants.getReportDocPath() + "/" + barcode + ".png"));
    BitmapCanvasProvider provider =
        new BitmapCanvasProvider(out, "image/x-png", 110, BufferedImage.TYPE_BYTE_GRAY, false, 0);
    bean.generateBarcode(provider, barcode);
    provider.finish();
    return provider.getBufferedImage();
  }

  public static BufferedImage generateQRCodeImage(
      String text, int width, int height, String filePath) throws WriterException, IOException {
    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

    Path path =
        FileSystems.getDefault().getPath(appConstants.getReportDocPath() + "/" + text + ".png");
    MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

    return MatrixToImageWriter.toBufferedImage(bitMatrix);
  }

  public static byte[] getQRCodeImage(String text, int width, int height)
      throws WriterException, IOException {
    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

    ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
    byte[] pngData = pngOutputStream.toByteArray();
    return pngData;
  }

  public static byte[] compress(byte[] data) throws IOException {
    Deflater deflater = new Deflater();
    deflater.setInput(data);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
    deflater.finish();
    byte[] buffer = new byte[1024];
    while (!deflater.finished()) {
      int count = deflater.deflate(buffer); // returns the generated code... index
      outputStream.write(buffer, 0, count);
    }
    outputStream.close();
    byte[] output = outputStream.toByteArray();
    logger.debug("Original: " + data.length / 1024 + " Kb");
    logger.debug("Compressed: " + output.length / 1024 + " Kb");
    return output;
  }

  public static byte[] decompress(byte[] data) throws IOException, DataFormatException {
    Inflater inflater = new Inflater();
    inflater.setInput(data);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
    byte[] buffer = new byte[1024];
    while (!inflater.finished()) {
      int count = inflater.inflate(buffer);
      outputStream.write(buffer, 0, count);
    }
    outputStream.close();
    byte[] output = outputStream.toByteArray();
    logger.debug("Original: " + data.length);
    logger.debug("Compressed: " + output.length);
    return output;
  }
}
