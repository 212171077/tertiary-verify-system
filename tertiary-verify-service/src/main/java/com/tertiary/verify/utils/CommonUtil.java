package com.tertiary.verify.utils;

import com.tertiary.verify.data_model.model.entities.Address;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CommonUtil {
  private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);

  public static String generateRandomText() {
    int leftLimit = 97; // letter 'a'
    int rightLimit = 122; // letter 'z'
    int targetStringLength = 10;
    Random random = new Random();
    StringBuilder buffer = new StringBuilder(targetStringLength);
    for (int i = 0; i < targetStringLength; i++) {
      int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
      Random rand = new Random();
      int n = rand.nextInt(15) + 1;
      if (n > 10) {
        buffer.append(Character.toUpperCase((char) randomLimitedInt));
      } else {
        buffer.append((char) randomLimitedInt);
      }
    }
    String generatedString = buffer.toString();
    generatedString = generatedString.trim();
    LOGGER.info("Generated String: {}", generatedString);
    return generatedString;
  }

  public static <T> T createObjectFromMap(LinkedHashMap<String, String> map, Class<T> clazz)
      throws Exception {
    T object = clazz.newInstance();

    for (Map.Entry<String, String> entry : map.entrySet()) {
      String fieldName = entry.getKey();
      String value = String.valueOf(entry.getValue());

      Field field = clazz.getDeclaredField(fieldName);
      field.setAccessible(true);

      Class<?> fieldType = field.getType();
      Object fieldValue = null;

      if (fieldType == String.class) {
        fieldValue = value;
      } else if (fieldType == Integer.class && !isValueNull(value)) {
        fieldValue = Integer.valueOf(value);
      } else if (fieldType == Double.class && !isValueNull(value)) {
        fieldValue = Double.valueOf(value);
      } else if (fieldType == Boolean.class && !isValueNull(value)) {
        fieldValue = Boolean.valueOf(value);
      } else if (fieldType == Byte.class) {
        fieldValue = Byte.valueOf(value);
      } else if (fieldType == Short.class && !isValueNull(value)) {
        fieldValue = Short.valueOf(value);
      } else if (fieldType == Long.class && !isValueNull(value)) {
        fieldValue = Long.valueOf(value);
      } else if (fieldType == Float.class && !isValueNull(value)) {
        fieldValue = Float.valueOf(value);
      } else if (fieldType == Character.class) {
        fieldValue = value.charAt(0);
      }

      if (fieldValue != null) {
        field.set(object, fieldValue);
      }
    }

    return object;
  }

  private static boolean isValueNull(String value) {
    return value == null || value.equalsIgnoreCase("null");
  }

  public static List<String> buildAddress(List<Address> addressList) {
    return addressList.stream()
        .map(CommonUtil::buildAddressDisplayValue)
        .collect(Collectors.toList());
  }

  public static String buildAddressDisplayValue(Address address) {
    List<String> addressLines =
        Stream.of(
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getAddressLine3(),
                address.getAddressLine4(),
                address.getCode(),
                address.getSuburb())
            .filter(line -> line != null && !line.isEmpty())
            .collect(Collectors.toList());

    if (address.getCity() != null) {
      addressLines.add(address.getCity().getDescription());
    }

    if (address.getProvince() != null) {
      addressLines.add(address.getProvince().getDescription());
    }

    return String.join(", ", addressLines);
  }
}
