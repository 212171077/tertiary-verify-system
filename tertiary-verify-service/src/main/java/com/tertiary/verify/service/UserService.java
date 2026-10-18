package com.tertiary.verify.service;

import com.tertiary.verify.data_model.exeption.UserNotException;
import com.tertiary.verify.data_model.model.beans.*;
import com.tertiary.verify.data_model.model.constants.AppConstants;
import com.tertiary.verify.data_model.model.entities.Address;
import com.tertiary.verify.data_model.model.entities.ImageModel;
import com.tertiary.verify.data_model.model.entities.Role;
import com.tertiary.verify.data_model.model.entities.User;
import com.tertiary.verify.data_model.model.entities.UserRole;
import com.tertiary.verify.data_model.model.entities.enums.AddressTypeEnum;
import com.tertiary.verify.data_model.model.entities.enums.ERole;
import com.tertiary.verify.data_model.model.entities.lookup.*;
import com.tertiary.verify.data_model.model.repositories.RoleRepository;
import com.tertiary.verify.data_model.model.repositories.UserRepository;
import com.tertiary.verify.data_model.model.repositories.UserRoleRepository;
import com.tertiary.verify.framework.AbstractService;
import com.tertiary.verify.mail.MailSender;
import com.tertiary.verify.service.lookup.CityService;
import com.tertiary.verify.service.lookup.GenderService;
import com.tertiary.verify.service.lookup.ProvinceService;
import com.tertiary.verify.service.lookup.TitleService;
import com.tertiary.verify.utils.AppUtility;
import com.tertiary.verify.utils.ConstantUtil;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService extends AbstractService {
    private static final SimpleDateFormat sdfIdDate = new SimpleDateFormat("yyMMdd", Locale.ENGLISH);
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private static final String USER_DETAILS_UPDATED = "User details updated";
    private static final String PROFILE_IMAGE_PREFIX = "profile-image:";
    private static final int MAX_PROFILE_IMAGE_BYTES = 5 * 1024 * 1024;
    private static final int MAX_PROFILE_IMAGE_DIMENSION = 4096;
    private static final int NORMALIZED_PROFILE_IMAGE_DIMENSION = 512;
    private static final int MAX_STORED_PROFILE_IMAGE_BYTES = 512 * 1024;
    @Autowired
    private UserRepository repository;
    @Autowired
    private MailSender mailSender;
    @Autowired
    private GenderService genderService;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private CommonService commonService;
    @Autowired
    private TitleService titleService;
    @Autowired
    private TertiaryVerifyService tertiaryVerifyService;
    @Autowired
    private ImagesService imagesService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private CityService cityService;
    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private PasswordEncoder encoder;

    @CacheEvict(value = "users", allEntries = true)
    public void saveUser(User user) throws Exception {
        boolean isNew = false;
        if (user.getId() == null) {
            user.setDeleted(false);
            user.setCreateDate(new Date());
            if (user.getPassword() == null) {
                user.setPassword(generatePassword());
                user.setChangePassword(true);
            }
            isNew = true;
            String plainPass = user.getPassword();
            // Setting gender and date of birth
            user.setDob(sdfIdDate.parse(user.getRsaId().substring(0, 6)));
            if (Integer.parseInt(user.getRsaId().substring(6, 7)) > 4) {
                user.setGender(genderService.findByGenderName("Male"));
            } else {
                user.setGender(genderService.findByGenderName("Female"));
            }

            if (user.getPassword() != null) {
                String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
                user.setPassword(encodedPassword);
            }

            // Send login email notification
            try {
                registrationNotification(user, plainPass);
            } catch (Exception e) {
                LOGGER.error("Sending Email Error: ", e);
            }

        } else {
            user.setLastUpdateDate(new Date());
        }

        user.setUsername(user.getEmail());
        repository.save(user);

        if (isNew) {
            addGeneralUserRole(user);
        }
    }

    public List<UserDetails> findAllUserDetails() {
        List<User> list = findAllUser();
        return list.stream().map(this::buildUserDetails).collect(Collectors.toList());
    }

    public UserDetails findUserDetailsByID(Long id) throws Exception {
        Optional<User> optionalUser = Optional.ofNullable(findByIdAndDeleted(id, false));
        if (optionalUser.isPresent()) {
            return buildUserDetails(optionalUser.get());
        } else {
            throw new Exception("User not found");
        }
    }

    public ResponseEntity<?> findRoleList() {
        try {
            return ResponseEntity.ok(findAllRoles());
        } catch (Exception e) {
            LOGGER.error("Error: ", e);
            return ResponseEntity.badRequest().body(new MessageResponse(false, e.getMessage(), null));
        }
    }

    public ResponseEntity<?> findAllUsers() {
        try {
            return ResponseEntity.ok(findAllUserDetails());
        } catch (Exception e) {
            LOGGER.error("Error: ", e);
            return ResponseEntity.badRequest().body(new MessageResponse(false, e.getMessage(), null));
        }
    }

    public ResponseEntity<? extends Serializable> findUserByID(Long id) {
        try {
            return ResponseEntity.ok(findUserDetailsByID(id));
        } catch (Exception e) {
            LOGGER.error("Error: ", e);
            return ResponseEntity.badRequest().body(new MessageResponse(false, e.getMessage(), null));
        }
    }

    @Cacheable("roles")
    public List<com.tertiary.verify.data_model.model.beans.Role> findAllRoles() {
        return roleRepository.findAll().stream()
                .map(
                        role ->
                                com.tertiary.verify.data_model.model.beans.Role.builder()
                                        .id(role.getId())
                                        .description(role.getDescription())
                                        .name(role.getName().name().replace("ROLE_", ""))
                                        .build())
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "users", allEntries = true)
    public ResponseEntity<MessageResponse> updateImage(UpdatedImageRequest request) {
        User user = repository.findByIdAndDeleted(request.getId(), false);
        if (user != null) {
            user.setImage(request.getImageName());
            repository.save(user);
            return ResponseEntity.ok(
                    MessageResponse.builder().success(true).message("Image updated successfully").build());
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse(false, "User not found", null));
        }
    }

    @CacheEvict(value = "users", allEntries = true)
    public void recordLogin(Long userId) {
        User user = repository.findByIdAndDeleted(userId, false);
        if (user != null) {
            user.setLastLoginDate(new Date());
            repository.save(user);
        }
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public ResponseEntity<MessageResponse> updateProfileImage(
            Long userId, ProfileImageRequest request) {
        User user = repository.findByIdAndDeleted(userId, false);
        if (user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse(false, "User not found", null));
        }

        byte[] imageBytes;
        try {
            imageBytes = Base64.getDecoder().decode(request.getImage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(false, "The profile image is not valid base64 data", null));
        }
        if (imageBytes.length == 0 || imageBytes.length > MAX_PROFILE_IMAGE_BYTES) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(false, "Profile images must be 5 MB or smaller", null));
        }

        String contentType;
        try {
            contentType = validateProfileImage(imageBytes, request.getContentType());
        } catch (IOException e) {
            LOGGER.warn("Unable to validate profile image for user {}", userId, e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(false, "The selected file is not a valid image", null));
        }
        if (contentType == null) {
            return ResponseEntity.badRequest()
                    .body(
                            new MessageResponse(
                                    false,
                                    "Only valid JPEG, PNG, or GIF images up to 4096 x 4096 pixels are supported",
                                    null));
        }

        ImageModel imageModel = findExistingProfileImage(user.getImage());
        if (imageModel == null) {
            imageModel = new ImageModel();
            imageModel.setName("user-profile-" + userId);
        }
        byte[] storedImageBytes;
        try {
            storedImageBytes = normalizeProfileImage(imageBytes);
        } catch (IOException e) {
            LOGGER.warn("Unable to normalize profile image for user {}", userId, e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(false, "The selected file could not be processed", null));
        }
        if (storedImageBytes == null) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(false, "The selected image could not be compressed", null));
        }

        contentType = "image/jpeg";
        imageModel.setType(contentType);
        imageModel.setPic(storedImageBytes);
        imageModel = imagesService.save(imageModel);
        user.setImage(PROFILE_IMAGE_PREFIX + imageModel.getId());
        repository.save(user);

        return ResponseEntity.ok(
                new MessageResponse(
                        true,
                        "Profile image updated successfully",
                        "data:"
                                + contentType
                                + ";base64,"
                                + Base64.getEncoder().encodeToString(storedImageBytes)));
    }

    private String validateProfileImage(byte[] imageBytes, String claimedContentType)
            throws IOException {
        ImageInputStream input =
                ImageIO.createImageInputStream(new ByteArrayInputStream(imageBytes));
        if (input == null) {
            return null;
        }

        try {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                return null;
            }

            ImageReader reader = readers.next();
            try {
                reader.setInput(input, true, true);
                String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                String detectedContentType;
                if ("jpg".equals(format) || "jpeg".equals(format)) {
                    detectedContentType = "image/jpeg";
                } else if ("png".equals(format)) {
                    detectedContentType = "image/png";
                } else if ("gif".equals(format)) {
                    detectedContentType = "image/gif";
                } else {
                    return null;
                }

                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                if (!detectedContentType.equals(claimedContentType)
                        || width <= 0
                        || height <= 0
                        || width > MAX_PROFILE_IMAGE_DIMENSION
                        || height > MAX_PROFILE_IMAGE_DIMENSION) {
                    return null;
                }
                return detectedContentType;
            } finally {
                reader.dispose();
            }
        } finally {
            input.close();
        }
    }

    private byte[] normalizeProfileImage(byte[] imageBytes) throws IOException {
        BufferedImage sourceImage = readProfileImage(imageBytes);
        if (sourceImage == null) {
            return null;
        }

        for (int maxDimension : new int[] {512, 384, 256}) {
            double scale =
                    Math.min(1.0, (double) maxDimension / Math.max(sourceImage.getWidth(), sourceImage.getHeight()));
            int width = Math.max(1, (int) Math.round(sourceImage.getWidth() * scale));
            int height = Math.max(1, (int) Math.round(sourceImage.getHeight() * scale));
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = resizedImage.createGraphics();
            try {
                graphics.setColor(Color.WHITE);
                graphics.fillRect(0, 0, width, height);
                graphics.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                graphics.drawImage(sourceImage, 0, 0, width, height, null);
            } finally {
                graphics.dispose();
            }

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            if (!ImageIO.write(resizedImage, "jpeg", output)) {
                return null;
            }
            byte[] normalized = output.toByteArray();
            if (normalized.length <= MAX_STORED_PROFILE_IMAGE_BYTES) {
                return normalized;
            }
        }
        return null;
    }

    private BufferedImage readProfileImage(byte[] imageBytes) throws IOException {
        ImageInputStream input =
                ImageIO.createImageInputStream(new ByteArrayInputStream(imageBytes));
        if (input == null) {
            return null;
        }

        try {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                return null;
            }

            ImageReader reader = readers.next();
            try {
                reader.setInput(input, true, true);
                int largestDimension = Math.max(reader.getWidth(0), reader.getHeight(0));
                int subsampling =
                        Math.max(
                                1,
                                (int)
                                        Math.ceil(
                                                (double) largestDimension
                                                        / NORMALIZED_PROFILE_IMAGE_DIMENSION));
                ImageReadParam readParam = reader.getDefaultReadParam();
                readParam.setSourceSubsampling(subsampling, subsampling, 0, 0);
                return reader.read(0, readParam);
            } finally {
                reader.dispose();
            }
        } finally {
            input.close();
        }
    }

    private ImageModel findExistingProfileImage(String imageReference) {
        if (imageReference == null || !imageReference.startsWith(PROFILE_IMAGE_PREFIX)) {
            return null;
        }
        try {
            return imagesService.getById(
                    Long.parseLong(imageReference.substring(PROFILE_IMAGE_PREFIX.length())));
        } catch (NumberFormatException e) {
            LOGGER.warn("Invalid profile image reference: {}", imageReference);
            return null;
        }
    }

    private UserDetails buildUserDetails(User user) {

        List<com.tertiary.verify.data_model.model.beans.Role> roles =
                user.getRoles().stream()
                        .map(
                                role ->
                                        com.tertiary.verify.data_model.model.beans.Role.builder()
                                                .id(role.getId())
                                                .description(role.getDescription())
                                                .name(role.getName().name().replace("ROLE_", ""))
                                                .build())
                        .collect(Collectors.toList());

        City ressCity =
                Optional.ofNullable(user.getResidentialAddress()).map(Address::getCity).orElse(null);
        Province ressProvince =
                Optional.ofNullable(user.getResidentialAddress()).map(Address::getProvince).orElse(null);

        City postalCity =
                Optional.ofNullable(user.getPostalAddress()).map(Address::getCity).orElse(null);
        Province postalProvince =
                Optional.ofNullable(user.getPostalAddress()).map(Address::getProvince).orElse(null);

        return UserDetails.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .roles(roles)
                .rsaId(user.getRsaId())
                .dateOfBirth(AppUtility.sdfYMD.format(user.getDob()))
                .createDate(user.getCreateDate())
                .lastLoginDate(user.getLastLoginDate())
                .status(user.getStatus() != null ? user.getStatus().getFriendlyName() : null)
                .cellNumber(
                        (user.getCellNumber() != null ? user.getCellNumber().replaceAll("\\s", "") : ""))
                .titles(
                        commonService.buildTitlesDropDown(
                                Optional.ofNullable(user.getTitle()).map(Title::getDescription).orElse(null)))
                .genders(
                        commonService.buildGenderDropDown(
                                Optional.ofNullable(user.getGender()).map(Gender::getGenderName).orElse(null)))
                .imageUrl(getImage(user))
                // *************Residential Address ******************
                .residentialAddressLine1(
                        Optional.ofNullable(user.getResidentialAddress())
                                .map(Address::getAddressLine1)
                                .orElse(ConstantUtil.EMPTY))
                .residentialAddressLine2(
                        Optional.ofNullable(user.getResidentialAddress())
                                .map(Address::getAddressLine2)
                                .orElse(ConstantUtil.EMPTY))
                .residentialAddressLine3(
                        Optional.ofNullable(user.getResidentialAddress())
                                .map(Address::getAddressLine3)
                                .orElse(ConstantUtil.EMPTY))
                .residentialAddressLine4(
                        Optional.ofNullable(user.getResidentialAddress())
                                .map(Address::getAddressLine4)
                                .orElse(ConstantUtil.EMPTY))
                .residentialAddressCities(
                        commonService.buildCitiesDropDown(
                                Optional.ofNullable(ressCity).map(City::getDescription).orElse(null)))
                .residentialAddressSuburb(
                        Optional.ofNullable(user.getResidentialAddress())
                                .map(Address::getSuburb)
                                .orElse(ConstantUtil.EMPTY))
                .residentialCode(
                        Optional.ofNullable(user.getResidentialAddress())
                                .map(Address::getCode)
                                .orElse(ConstantUtil.EMPTY))
                .residentialAddressProvinces(
                        commonService.buildProvincesDropDown(
                                Optional.ofNullable(ressProvince).map(Province::getDescription).orElse(null)))
                // ***************Postal Address**************************
                .postalAddressLine1(
                        Optional.ofNullable(user.getPostalAddress())
                                .map(Address::getAddressLine1)
                                .orElse(ConstantUtil.EMPTY))
                .postalAddressLine2(
                        Optional.ofNullable(user.getPostalAddress())
                                .map(Address::getAddressLine2)
                                .orElse(ConstantUtil.EMPTY))
                .postalAddressLine3(
                        Optional.ofNullable(user.getPostalAddress())
                                .map(Address::getAddressLine3)
                                .orElse(ConstantUtil.EMPTY))
                .postalAddressLine4(
                        Optional.ofNullable(user.getPostalAddress())
                                .map(Address::getAddressLine4)
                                .orElse(ConstantUtil.EMPTY))
                .postalAddressCities(
                        commonService.buildCitiesDropDown(
                                Optional.ofNullable(postalCity).map(City::getDescription).orElse(null)))
                .postalAddressSuburb(
                        Optional.ofNullable(user.getPostalAddress())
                                .map(Address::getSuburb)
                                .orElse(ConstantUtil.EMPTY))
                .postalAddressCode(
                        Optional.ofNullable(user.getPostalAddress())
                                .map(Address::getCode)
                                .orElse(ConstantUtil.EMPTY))
                .postalAddressProvinces(
                        commonService.buildProvincesDropDown(
                                Optional.ofNullable(postalProvince).map(Province::getDescription).orElse(null)))
                .menus(buildUserMenus(roles))
                .build();
    }

    private String getImage(User user) {
        if (user.getImage() != null && user.getImage().startsWith(PROFILE_IMAGE_PREFIX)) {
            ImageModel imageModel = findExistingProfileImage(user.getImage());
            if (imageModel != null && imageModel.getPic() != null && imageModel.getType() != null) {
                return "data:"
                        + imageModel.getType()
                        + ";base64,"
                        + Base64.getEncoder().encodeToString(imageModel.getPic());
            }
            LOGGER.warn("Profile image not found for user {}", user.getId());
            return tertiaryVerifyService
                    .findAppConfigsByCode("DEFAULT_PROFILE_IMAGE")
                    .get(0)
                    .getValue();
        }

        AppConfig displayDefaultImg =
                tertiaryVerifyService.findAppConfigsByCode("DISPLAY_DEFAULT_IMAGES").get(0);
        if (Boolean.parseBoolean(displayDefaultImg.getValue())) {
            return tertiaryVerifyService.findAppConfigsByCode("DEFAULT_PROFILE_IMAGE").get(0).getValue();
        } else {
            AppConfig imageUrlAppConfig = tertiaryVerifyService.findAppConfigsByCode("IMAGES_URL").get(0);
            return imageUrlAppConfig
                    .getValue()
                    .concat(
                            user.getImage() == null || user.getImage().isEmpty()
                                    ? "profile.jpg"
                                    : user.getImage());
        }
    }

    private List<Menu> buildUserMenus(List<com.tertiary.verify.data_model.model.beans.Role> roles) {
        List<Menu> menuList = new ArrayList<>();
        List<MenuItem> quickOptionItems = new ArrayList<>();
        Menu quickOptionMenu = new Menu();
        quickOptionMenu.setLabel("Quick options");
        quickOptionMenu.setIcon("pi pi-fw pi-home");

        // General User
        if (haveRole("USER", roles)) {
            quickOptionItems.add(
                    MenuItem.builder()
                            .label("Dashboard")
                            .icon("pi pi-fw pi-home")
                            .routerLink(Collections.singletonList("/"))
                            .build());

            quickOptionItems.add(
                    MenuItem.builder()
                            .label("My Profile")
                            .icon("pi pi-fw pi-user")
                            .routerLink(Collections.singletonList("/pages/profile"))
                            .build());
        }

        // Investigator
        if (haveRole("INV", roles)) {
            quickOptionItems.add(
                    MenuItem.builder()
                            .label("Reported Institutions")
                            .icon("pi pi-fw pi-times-circle")
                            .routerLink(Collections.singletonList("/pages/my-reported-institutions"))
                            .build());
        }

        // Supervisor
        if (haveRole("SPVS", roles)) {
            quickOptionItems.add(
                    MenuItem.builder()
                            .label("Pending Approval")
                            .icon("pi pi-fw pi-check-square")
                            .routerLink(Collections.singletonList("/pages/pending-institutions"))
                            .build());
        }

        quickOptionMenu.setItems(quickOptionItems);
        menuList.add(quickOptionMenu);

        // Administrator
        if (haveRole("ADMIN", roles)) {
            Menu menu = new Menu();
            menu.setLabel("Configurations");
            menu.setIcon("pi pi-fw pi-cog");
            List<MenuItem> items = new ArrayList<>();
            List<MenuItem> lookups = new ArrayList<>();

            // Location
            MenuItem location = new MenuItem();
            location.setLabel("Location");
            location.setIcon("pi pi-fw pi-cog");
            location.setItems(new ArrayList<>());
            location
                    .getItems()
                    .add(
                            MenuItem.builder()
                                    .label("Cities")
                                    .icon("pi pi-fw pi-sign-in")
                                    .routerLink(Collections.singletonList("/pages/lookup/cities"))
                                    .build());
            location
                    .getItems()
                    .add(
                            MenuItem.builder()
                                    .label("Provinces")
                                    .icon("pi pi-fw pi-sign-in")
                                    .routerLink(Collections.singletonList("/pages/lookup/provinces"))
                                    .build());
            lookups.add(location);

            // Education
            MenuItem education = new MenuItem();
            education.setLabel("Education");
            education.setIcon("pi pi-fw pi-cog");
            education.setItems(new ArrayList<>());
            education
                    .getItems()
                    .add(
                            MenuItem.builder()
                                    .label("Courses")
                                    .icon("pi pi-fw pi-sign-in")
                                    .routerLink(Collections.singletonList("/pages/lookup/courses"))
                                    .build());
            education
                    .getItems()
                    .add(
                            MenuItem.builder()
                                    .label("Course Levels")
                                    .icon("pi pi-fw pi-sign-in")
                                    .routerLink(Collections.singletonList("/pages/lookup/course-levels"))
                                    .build());
            education
                    .getItems()
                    .add(
                            MenuItem.builder()
                                    .label("Course Types")
                                    .icon("pi pi-fw pi-sign-in")
                                    .routerLink(Collections.singletonList("/pages/lookup/course-types"))
                                    .build());
            education
                    .getItems()
                    .add(
                            MenuItem.builder()
                                    .label("Type of Institutions")
                                    .icon("pi pi-fw pi-sign-in")
                                    .routerLink(Collections.singletonList("/pages/lookup/institution-types"))
                                    .build());
            lookups.add(education);

            // News and Events
            MenuItem newsAndEvents = new MenuItem();
            newsAndEvents.setLabel("News and Events");
            newsAndEvents.setIcon("pi pi-fw pi-cog");
            newsAndEvents.setItems(new ArrayList<>());
            newsAndEvents
                    .getItems()
                    .add(
                            MenuItem.builder()
                                    .label("Events")
                                    .icon("pi pi-fw pi-sign-in")
                                    .routerLink(Collections.singletonList("/pages/lookup/events"))
                                    .build());
            newsAndEvents
                    .getItems()
                    .add(
                            MenuItem.builder()
                                    .label("News")
                                    .icon("pi pi-fw pi-sign-in")
                                    .routerLink(Collections.singletonList("/pages/lookup/news"))
                                    .build());
            lookups.add(newsAndEvents);

            // Miscellaneous
            MenuItem miscellaneous = new MenuItem();
            miscellaneous.setLabel("Miscellaneous");
            miscellaneous.setIcon("pi pi-fw pi-cog");
            miscellaneous.setItems(new ArrayList<>());
            miscellaneous
                    .getItems()
                    .add(
                            MenuItem.builder()
                                    .label("App Config")
                                    .icon("pi pi-fw pi-sign-in")
                                    .routerLink(Collections.singletonList("/pages/lookup/app-config"))
                                    .build());

            miscellaneous
                    .getItems()
                    .add(
                            MenuItem.builder()
                                    .label("Reject Reasons")
                                    .icon("pi pi-fw pi-sign-in")
                                    .routerLink(Collections.singletonList("/pages/lookup/reject-reasons"))
                                    .build());

            miscellaneous
                    .getItems()
                    .add(
                            MenuItem.builder()
                                    .label("Genders")
                                    .icon("pi pi-fw pi-sign-in")
                                    .routerLink(Collections.singletonList("/pages/lookup/genders"))
                                    .build());
            miscellaneous
                    .getItems()
                    .add(
                            MenuItem.builder()
                                    .label("Titles")
                                    .icon("pi pi-fw pi-sign-in")
                                    .routerLink(Collections.singletonList("/pages/lookup/titles"))
                                    .build());
            lookups.add(miscellaneous);

            items.add(
                    MenuItem.builder().label("Lookups").icon("pi pi-fw pi-list").items(lookups).build());

            items.add(
                    MenuItem.builder()
                            .label("User Management")
                            .icon("pi pi-fw pi-users")
                            .routerLink(Collections.singletonList("/pages/users"))
                            .build());

            items.add(
                    MenuItem.builder()
                            .label("Institutions")
                            .icon("pi pi-fw pi-building")
                            .routerLink(Collections.singletonList("/pages/institution-registration"))
                            .build());
            items.add(
                    MenuItem.builder()
                            .label("All Reported Institutions")
                            .icon("pi pi-fw pi-times-circle")
                            .routerLink(Collections.singletonList("/pages/all-reported-institutions"))
                            .build());

            menu.setItems(items);
            menuList.add(menu);
        }

        // menuList.addAll(buildUserMenusTemplate(roles));
        return menuList;
    }

    public ResponseEntity<MessageResponse> changePassword(ChangePassRequest changePassRequest) {
        LOGGER.info("Changing password, User ID: {}", changePassRequest.getUserId());
        if (changePassRequest
                .getConfirmPassword()
                .equalsIgnoreCase(changePassRequest.getNewPassword())) {
            Optional<User> optionalUser =
                    Optional.ofNullable(repository.findByIdAndDeleted(changePassRequest.getUserId(), false));

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setChangePassword(false);
                user.setPassword(encoder.encode(changePassRequest.getNewPassword()));
                repository.save(user);
                LOGGER.info("Password updated, User ID: {}", changePassRequest.getUserId());

                notifyUserPasswordChange(user, changePassRequest.getNewPassword());

                return ResponseEntity.ok(new MessageResponse(false, "Password updated successful", null));

            } else {
                LOGGER.error("User not found, User ID: {}", changePassRequest.getUserId());
                return ResponseEntity.badRequest()
                        .body(new MessageResponse(false, "Invalid user ID", null));
            }
        } else {
            LOGGER.error("Password mismatch, User ID: {}", changePassRequest.getUserId());
            return ResponseEntity.badRequest()
                    .body(
                            new MessageResponse(
                                    false,
                                    "The password and confirm password fields do not match. Please make sure you enter the same password in both fields.",
                                    null));
        }
    }

    public ResponseEntity<MessageResponse> updatePassword(UpdatePassRequest changePassRequest) {
        LOGGER.info("Updating password, User ID: {}", changePassRequest.getUserId());
        if (changePassRequest
                .getConfirmPassword()
                .equalsIgnoreCase(changePassRequest.getNewPassword())) {
            Optional<User> optionalUser =
                    Optional.ofNullable(repository.findByIdAndDeleted(changePassRequest.getUserId(), false));
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                if (encoder.matches(changePassRequest.getOldPassword(), user.getPassword())) {
                    user.setChangePassword(false);
                    user.setPassword(encoder.encode(changePassRequest.getNewPassword()));
                    repository.save(user);
                    LOGGER.info("Password updated, User ID: {}", changePassRequest.getUserId());
                    notifyUserPasswordChange(user, changePassRequest.getNewPassword());
                    return ResponseEntity.ok(new MessageResponse(false, "Password updated successful", null));
                } else {
                    LOGGER.error("Invalid old password, User ID: {}", changePassRequest.getUserId());
                    return ResponseEntity.badRequest()
                            .body(new MessageResponse(false, "Invalid old password", null));
                }
            } else {
                LOGGER.error("User not found, User ID: {}", changePassRequest.getUserId());
                return ResponseEntity.badRequest()
                        .body(new MessageResponse(false, "Invalid user ID", null));
            }
        } else {
            LOGGER.error("Password mismatch, User ID: {}", changePassRequest.getUserId());
            return ResponseEntity.badRequest()
                    .body(
                            new MessageResponse(
                                    false,
                                    "The password and confirm password fields do not match. Please make sure you enter the same password in both fields.",
                                    null));
        }
    }

    private boolean haveRole(
            String role, List<com.tertiary.verify.data_model.model.beans.Role> roles) {
        for (com.tertiary.verify.data_model.model.beans.Role rl : roles) {
            if (rl.getName().equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    public void addGeneralUserRole(User user) throws Exception {
        UserRole userRole = new UserRole();
        Optional<Role> role = roleRepository.findByName(ERole.ROLE_USER);
        if (!role.isPresent()) {
            throw new Exception("User role not found");
        }
        userRole.setRole(role.get());
        userRole.setUser(user);
        userRoleRepository.save(userRole);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void deleteUser(User user) {
        user.setDeleted(true);
        repository.save(user);
    }

    @Cacheable("users")
    public List<User> findAllUser() {
        return repository.findByDeleted(false);
    }

    public ResponseEntity<? extends Serializable> deleteUserById(Long id) {
        Optional<User> optionalUser = Optional.ofNullable(findByIdAndDeleted(id, false));
        if (!optionalUser.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse(false, "Invalid user ID", null));
        }
        deleteUser(optionalUser.get());
        return ResponseEntity.ok(new MessageResponse(true, "User deleted", null));
    }

    public User findByEmail(String email) {
        List<User> list = findAllUser();
        if (!list.isEmpty()) {
            return list.stream()
                    .filter(user -> email.equalsIgnoreCase(user.getEmail()))
                    .findAny()
                    .orElse(null);
        } else {
            return null;
        }
    }

    public User findByIdAndDeleted(Long id, boolean deleted) {
        List<User> list = findAllUser();
        if (!list.isEmpty() || id == null) {
            return list.stream()
                    .filter(user -> Objects.equals(id, user.getId()) && deleted == user.getDeleted())
                    .findAny()
                    .orElse(null);
        } else {
            return null;
        }
    }

    public User findByEmailAndDeleted(String email, boolean deleted) {
        return repository.findByEmailAndDeleted(email, deleted);
    }

    public User findByEmailAndIdNumber(String email, String idNumber) {
        List<User> list = findAllUser();
        if (!list.isEmpty()) {
            User user = list.stream()
                    .filter(
                            u ->
                                    email.equalsIgnoreCase(u.getEmail())
                                            && idNumber.equalsIgnoreCase(u.getRsaId()))
                    .findAny()
                    .orElse(null);

            if (user == null) {
                logger.info(String.format("User is not registered. [Email : %s, ID Number: %s]", email, idNumber));
            }

            return user;

        } else {
            return null;
        }
    }

    public void notifyUserNewPasswordEmail(String email, String idNumber) throws Exception {
        User ul = findByEmailAndIdNumber(email, idNumber);
        if (ul == null) {
            throw new UserNotException(
                    "User is not registered. Please contact us to register or check your credentials.");
        } else {
            notifyUserNewPassword(ul);
        }
    }

    /**
     * Notify a user of new password.
     *
     * @param u the u @ the exception
     */
    public void notifyUserNewPassword(User u) {
        // Generate password
        String pwd = generatePassword();
        u.setPassword(bCryptPasswordEncoder.encode(pwd));
        u.setChangePassword(true);
        repository.save(u);

        String welcome =
                "<p>Dear #NAME#,</p>"
                        + "<p>We received your request to reset your password on our website. "
                        + "As per your request, we have generated a new password for your account. "
                        + "Your new login details are as follow: <p>"
                        + "<ul>"
                        + "<li><strong>Username: </strong> "
                        + u.getUsername()
                        + "</li>"
                        + "<li><strong>New Password:</strong> "
                        + pwd
                        + "</li>"
                        + "</ul>"
                        + "</b></p>"
                        + "<p>Please use this password to log in to your account. Once you have "
                        + "logged in, you will be prompted to change your password for security "
                        + "reasons. Please choose a password that is both secure and easy "
                        + "for you to remember.</p>"
                        + "<p>If you did not request a password reset, or if you have any issues "
                        + "logging in or changing your password, please contact us immediately "
                        + "for assistance. We take the security of our user accounts very seriously "
                        + "and will work with you to ensure that your account is secure.</p>"
                        + "<p>Thank you for being a valued member. "
                        + "We look forward to your continued participation and engagement.</p>";
        welcome = welcome.replaceAll("#NAME#", u.getName() + " " + u.getSurname());

        Mail mail = new Mail();

        mail.setContent(welcome);
        mail.setFrom(AppConstants.FROM_EMAIL);
        String[] to = {u.getEmail()};
        mail.setTo(to);
        mail.setSubject("New Password for Your Account");
        mail.setCc(to);
        mailSender.saveEmail(mail);
    }

    public void notifyUserPasswordChange(User u, String password) {
        String welcome =
                "<p>Dear #NAME#,</p>We hope this email finds you well. "
                        + "We want to inform you that your password has been "
                        + "successfully updated for your Tertiary Verify account. "
                        + "Your new login details are as follow:<p>"
                        + "<ul>"
                        + "<li><strong>Username: </strong> "
                        + u.getUsername()
                        + "</li>"
                        + "<li><strong>New Password:</strong> "
                        + password
                        + "</li>"
                        + "</ul>"
                        + "</b></p>"
                        + "<p>If you did not initiate this password change or "
                        + "suspect any unauthorized access to your account, please "
                        + "contact our support team immediately for further assistance.</p>"
                        + "<p>As always, please remember to keep your login credentials "
                        + "secure and avoid sharing them with anyone. Our team will "
                        + "never ask you to disclose your password or personal "
                        + "information via email or any other means.</p>"
                        + "<p>Thank you for being a valued member. "
                        + "We look forward to your continued participation and engagement.</p>";
        welcome = welcome.replaceAll("#NAME#", u.getName() + " " + u.getSurname());

        Mail mail = new Mail();

        mail.setContent(welcome);
        mail.setFrom(AppConstants.FROM_EMAIL);
        String[] to = {u.getEmail()};
        mail.setTo(to);
        mail.setSubject("Password Updated Successfully");
        mail.setCc(to);
        mailSender.saveEmail(mail);
    }

    /**
     * Notify a user.
     */
    public void registrationNotification(User u, String plainPass) {

        String welcome =
                "<p>Dear #NAME#,</p>"
                        + "<p>We are delighted to inform you that your Tertiary Verify "
                        + "account has been successfully created. Your account details are as follows:</p>"
                        + "<ul>"
                        + "<li><strong>Username: </strong> "
                        + u.getEmail()
                        + "</li>"
                        + "<li><strong>Password:</strong> "
                        + plainPass
                        + "</li>"
                        + "</ul>"
                        + "<p>Please use these details to log in to your account. "
                        + "We recommend that you change your password as soon as "
                        + "you log in for security reasons.</p>"
                        + "<p>If you have any questions or need assistance with "
                        + "your account, please do not hesitate to contact us. "
                        + "We are always happy to help you.</p>"
                        + "<p>Thank you for joining Tertiary Verify. "
                        + "We look forward to work with  you.</p>";
        welcome = welcome.replaceAll("#NAME#", u.getName() + " " + u.getSurname());

        Mail mail = new Mail();

        mail.setContent(welcome);
        mail.setFrom(AppConstants.FROM_EMAIL);
        String[] to = {u.getEmail()};
        mail.setTo(to);
        mail.setSubject("Tertiary Verify Account Created");
        mail.setCc(to);
        mailSender.saveEmail(mail);
    }

    public String generatePassword() {

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
        System.out.println(generatedString);
        return generatedString;
    }

    public long countByTitle(Title title) {
        return repository.countByTitleAndDeleted(title, false);
    }

    public long countByGender(Gender gender) {
        return repository.countByGenderAndDeleted(gender, false);
    }

    public ResponseEntity<?> updateUserRoles(UserRoleRequest request) {
        Optional<User> optionalUser =
                Optional.ofNullable(findByIdAndDeleted(request.getUserId(), false));

        if (optionalUser.isPresent()) {
            List<UserRole> oldRoleList = userRoleRepository.findByUser(optionalUser.get());
            userRoleRepository.deleteAll(oldRoleList);
            List<UserRole> newRoleList = new ArrayList<>();
            for (Long id : request.getRoleIds()) {
                Optional<Role> role = roleRepository.findById(id);
                if (role.isPresent()) {
                    UserRole userRole = new UserRole();
                    userRole.setRole(role.get());
                    userRole.setUser(optionalUser.get());
                    newRoleList.add(userRole);
                }
            }
            userRoleRepository.saveAll(newRoleList);

            return ResponseEntity.ok().body(new MessageResponse(true, "User roles updated", null));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse(false, "Invalid user ID", null));
        }
    }

    public ResponseEntity<?> registerUser(PersonalDetailsRequest userDetails) {
        try {
            User user;
            String mssg = USER_DETAILS_UPDATED;
            Optional<User> optionalUser =
                    Optional.ofNullable(findByIdAndDeleted(userDetails.getId(), false));

            if (!optionalUser.isPresent()) {
                mssg = "User registered successful";
                Optional<User> optionalExistingUser =
                        Optional.ofNullable(findByEmailAndDeleted(userDetails.getEmail(), false));
                if (optionalExistingUser.isPresent()) {
                    throw new ValidationException("This email address is already registered. Please use a different email or log in with this email address");
                }
                user = new User();
            } else {
                user = optionalUser.get();
            }


            user.setName(userDetails.getName());
            user.setSurname(userDetails.getSurname());
            user.setEmail(userDetails.getEmail());
            user.setCellNumber(userDetails.getCellNumber());
            user.setTitle(titleService.findById(userDetails.getTitle().getId()));
            user.setGender(genderService.findById(userDetails.getGender().getId()));
            user.setRsaId(userDetails.getIdNumber());
            saveUser(user);

            return ResponseEntity.ok().body(new MessageResponse(true, mssg, null));

        } catch (Exception e) {
            LOGGER.error("Error: ", e);
            return ResponseEntity.badRequest().body(new MessageResponse(false, e.getMessage(), null));
        }
    }

    public ResponseEntity<MessageResponse> updateUser(PersonalDetailsRequest userDetails) {
        LOGGER.info("Updating user details, User ID: {}", userDetails.getId());
        Optional<User> optionalUser =
                Optional.ofNullable(findByIdAndDeleted(userDetails.getId(), false));
        if (optionalUser.isPresent()) {

            User user = optionalUser.get();

            user.setName(userDetails.getName());
            user.setSurname(userDetails.getSurname());
            user.setEmail(userDetails.getEmail());
            user.setCellNumber(userDetails.getCellNumber());
            user.setTitle(titleService.findById(userDetails.getTitle().getId()));
            user.setGender(genderService.findById(userDetails.getGender().getId()));

            repository.save(user);
            return ResponseEntity.ok().body(new MessageResponse(true, USER_DETAILS_UPDATED, null));
        } else {
            LOGGER.error("Failed to update user details, User Details: {}", userDetails);
            return ResponseEntity.badRequest().body(new MessageResponse(false, "Invalid user ID", null));
        }
    }

    public ResponseEntity<MessageResponse> updateAddress(
            AddressDetailsRequest addressDetailsRequest) {
        LOGGER.info("Updating address details, User ID: {}", addressDetailsRequest.getUserId());
        Optional<User> optionalUser =
                Optional.ofNullable(findByIdAndDeleted(addressDetailsRequest.getUserId(), false));
        if (optionalUser.isPresent()) {

            try {
                User user = optionalUser.get();
                if (addressDetailsRequest.getAddressType() == AddressTypeEnum.RESIDENTIAL_ADDRESS) {
                    user.setResidentialAddress(
                            buildAddress(addressDetailsRequest, user.getResidentialAddress()));
                } else if (addressDetailsRequest.getAddressType() == AddressTypeEnum.POSTAL_ADDRESS) {
                    user.setPostalAddress(buildAddress(addressDetailsRequest, user.getPostalAddress()));
                } else {
                    throw new ValidationException(
                            "Invalid address type, supported address types: "
                                    .concat(
                                            AddressTypeEnum.RESIDENTIAL_ADDRESS
                                                    .name()
                                                    .concat(" and ".concat(AddressTypeEnum.POSTAL_ADDRESS.name()))));
                }

                repository.save(user);
                return ResponseEntity.ok().body(new MessageResponse(true, USER_DETAILS_UPDATED, null));
            } catch (Exception e) {
                LOGGER.error("Error: ", e);
                return ResponseEntity.badRequest()
                        .body(new MessageResponse(false, ConstantUtil.SERVICE_UNAVAILABLE_MSSG, null));
            }
        } else {
            LOGGER.error("Failed to update user details, User Details: {}", addressDetailsRequest);
            return ResponseEntity.badRequest().body(new MessageResponse(false, "Invalid user ID", null));
        }
    }

    private Address buildAddress(AddressDetailsRequest addressDetailsRequest, Address address) {
        if (address == null) {
            address = new Address();
        }
        address.setAddressLine1(addressDetailsRequest.getAddressLine1());
        address.setAddressLine2(addressDetailsRequest.getAddressLine2());
        address.setAddressLine3(addressDetailsRequest.getAddressLine3());
        address.setAddressLine4(addressDetailsRequest.getAddressLine4());
        address.setCity(cityService.findById(addressDetailsRequest.getCity().getId()));
        address.setProvince(provinceService.findById(addressDetailsRequest.getProvince().getId()));
        address.setCode(addressDetailsRequest.getCode());

        addressService.saveAddress(address);
        return address;
    }
}
