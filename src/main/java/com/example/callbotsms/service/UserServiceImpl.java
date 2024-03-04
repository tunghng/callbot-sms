package com.example.callbotsms.service;

import com.example.callbotsms.dto.mapper.AppUserMapper;
import com.example.callbotsms.dto.model.AppUserDto;
import com.example.callbotsms.dto.model.LogDto;
import com.example.callbotsms.dto.request.SignUpRequest;
import com.example.callbotsms.dto.request.UserActivateRequest;
import com.example.callbotsms.dto.response.UserProfileResponse;
import com.example.callbotsms.dto.response.page.PageData;
import com.example.callbotsms.dto.response.page.PageLink;
import com.example.callbotsms.model.AppUser;
import com.example.callbotsms.model.UserCredential;
import com.example.callbotsms.model.enums.*;
import com.example.callbotsms.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final AppUserRepository userRepository;

    private final AppComponentRepository componentRepository;

    private final UserCredentialsRepository userCredentialsRepository;

    private final UserCredentialsService userCredentialsService;

    private final UserComponentService userComponentService;

    private final ContactRepository contactRepository;

    private final AppUserMapper userMapper;

    private final DataKvRepository dataKvRepository;

    private final AppComponentMapper componentMapper;

    private final UserProducer userProducer;

    private final AccountPlanRepository accountPlanRepository;

    private final UserSubPlanRepository userSubPlanRepository;

    @Autowired
    LogService logService;

    @Override
    public AppUserDto save(AppUserDto userDto, AppUserDto currentUser) {

        ActionType actionType = userDto.getId() == null ? ActionType.CREATED : ActionType.UPDATED;
        validateSave(userDto, currentUser, actionType);

        AppUser user = new AppUser();

        if (actionType.equals(ActionType.CREATED)) {
            checkIfEmailExist(userDto.getEmail(), currentUser, userDto);
        } else {
            user = userMapper.toModel(userDto);
        }

        BeanUtils.copyProperties(userDto, user,
                "createdAt", "createdBy"
        );

        if (currentUser != null) {
            if (actionType.equals(ActionType.CREATED)) {
                user.setCreatedBy(currentUser.getId());
            }
            user.setTenantId(currentUser.getTenantId());
        }

        user.setAuthority(AuthorityType.lookup(userDto.getAuthority()));
        user.setRole(RoleType.lookup(userDto.getRole()));
        if (userDto.getContactId() != null)
            user.setContact(
                    contactRepository.findById(userDto.getContactId()).orElse(null)
            );

        if (currentUser != null)
            user.setUpdatedBy(currentUser.getId());

        AppUser savedUser = userRepository.saveAndFlush(user);

        if (savedUser.getAuthority().equals(AuthorityType.TENANT_ADMIN)) {
            savedUser.setTenantId(savedUser.getId());
            savedUser = userRepository.saveAndFlush(savedUser);
            saveNewTenantBasicPlan(actionType, savedUser);
        }

        saveNewCustomerDefaultComponent(actionType, savedUser, currentUser);

        if (actionType.equals(ActionType.CREATED)) {
            userCredentialsService.setPassword(savedUser.getId());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        logService.save(LogDto.builder()
                .entityType(EntityType.USER)
                .entityId(savedUser.getId())
                .actionStatus(ActionStatus.SUCCESS)
                .actionType(actionType)
                .actionData(objectMapper.valueToTree(userDto)).build(), currentUser);

        AppUserDto savedUserDto = userMapper.toDto(savedUser);
        return savedUserDto;
    }

    @Override
    public AppUserDto signUp(SignUpRequest signUpRequest) {
        signUpAvailable(signUpRequest);
        checkIfEmailExist(signUpRequest.getEmail(), null, signUpRequest);

        AppUser defaultTenant = userRepository.findByEmail(
                dataKvRepository.findById(AppInfoType.DEFAULT_TENANT_EMAIL.name()
                ).orElse(null).getValue());
        Contact defaultContact = contactRepository.findById(
                UUID.fromString(dataKvRepository.findById(AppInfoType.DEFAULT_CONTACT_ID.name())
                        .orElse(null).getValue())).orElse(null);

        AppUser user = new AppUser();
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setEmail(signUpRequest.getEmail());
        user.setAuthority(AuthorityType.CUSTOMER_USER);
        user.setRole(RoleType.CUSTOMER);
        user.setTenantId(defaultTenant.getTenantId());
        user.setContact(defaultContact);

        AppUser savedUser = userRepository.saveAndFlush(user);
        AppUserDto savedUserDto = userMapper.toDto(savedUser);



        userProducer.sendMessage(savedUserDto);
        return savedUserDto;
    }

    @Override
    public PageData<?> findUsers(
            PageLink pageLink, RoleType role,
            UUID contactId, AppUserDto currentUser,
            Long createdAtStartTs, Long createdAtEndTs,
            Boolean isEnabled,
            Boolean isSearchMatchCase
    ) {
        Pageable pageable = PageRequest.of(pageLink.getPage(), pageLink.getPageSize(), pageLink.toSort(pageLink.getSortOrder()));
        isTimeStampValid(createdAtStartTs, createdAtEndTs);

        String searchText = Objects.toString(pageLink.getSearchText(), "")
                .replace("%", "\\%");

        searchText =  isSearchMatchCase ? searchText : removeAccent(searchText.toLowerCase());


        Page<AppUser> userPage = currentUser.getAuthority().equals(AuthorityType.SYS_ADMIN.name())
                ? userRepository.findUsersBySysAdmin(
                searchText,
                isSearchMatchCase,
                convertTimestampToDateTime(createdAtStartTs),
                convertTimestampToDateTime(createdAtEndTs),
                isEnabled,
                pageable
        ) : userRepository.findUsersByTenant(
                searchText,
                isSearchMatchCase,
                role, contactId, AuthorityType.CUSTOMER_USER,
                currentUser.getId(),
                convertTimestampToDateTime(createdAtStartTs),
                convertTimestampToDateTime(createdAtEndTs),
                isEnabled,
                pageable
        );
        Page<UserProfileResponse> userDtoPage = userPage.map(user ->
                getUserProfile(user.getId())
        );
        return new PageData<>(userDtoPage);
    }

    private void isTimeStampValid(Long startTs, Long endTs) {
        if (startTs != null && endTs != null) {
            if (!(startTs >= 0 && endTs >= 0 && startTs <= endTs)) {
                throw new BadRequestException("Start time and end time must be valid");
            }
        }
    }


    @Override
    public AppUserDto findUserById(UUID tenantId, UUID id) {
        AppUser user = userRepository.findByIdAndTenantId(id, tenantId);
        return user != null ? userMapper.toDto(user) : null;
    }

    @Override
    public String getUserAvatarById(UUID id) {
        AppUser user = userRepository.findById(id).orElse(null);
        assert user != null;
        return user.getAvatar();
    }

    @Override
    public UserProfileResponse getUserProfile(UUID id) {
        AppUser user = checkUserId(id);
        UserProfileResponse userProfile = userMapper.toUserProfile(user);
        if (!user.getAuthority().equals(AuthorityType.SYS_ADMIN)) {
            userProfile.setComponents(AuthorityType.CUSTOMER_USER.toString().equals(userProfile.getAuthority())
                    ? userComponentService.findAllCustomerComponent(userProfile.getId())
                    : userComponentService.findAllTenantComponent());
            UserSubPlan userSubPlan = userSubPlanRepository
                    .findByUserId(user.getTenantId())
                    .orElse(new UserSubPlan());
            userProfile.setPlanName(userSubPlan.getAccountPlan().getName().name());
            userProfile.setPlanExpiredIn(
                    userSubPlan.getExpiredIn() != null ? userSubPlan.getExpiredIn().toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli() : null
            );
        }
        userProfile.setEnabled(userCredentialsService.isEnabled(id));
        return userProfile;
    }

    @Override
    public String handleActiveUser(UUID id, Boolean isActive, AppUserDto currentUser) {
        UserCredential userCredential = userCredentialsRepository.findByUserId(id).get();
        ObjectMapper objectMapper = new ObjectMapper();
        if (isActive && userCredential.isEnabled()) {
            logService.save(LogDto.builder()
                    .entityType(EntityType.USER)
                    .entityId(id)
                    .actionData(objectMapper.valueToTree(new UserActivateRequest(id, true)))
                    .actionType(ActionType.ATTRIBUTES_UPDATED)
                    .actionStatus(ActionStatus.FAILURE)
                    .actionFailureDetails(String.format("User with id [%s] has already activated", id))
                    .build(), currentUser);
            throw new BadRequestException(
                    String.format("User with id [%s] has already activated", id)
            );
        }
        if (!isActive && !userCredential.isEnabled()) {
            logService.save(LogDto.builder()
                    .entityType(EntityType.USER)
                    .entityId(id)
                    .actionData(objectMapper.valueToTree(new UserActivateRequest(id, false)))
                    .actionType(ActionType.ATTRIBUTES_UPDATED)
                    .actionStatus(ActionStatus.FAILURE)
                    .actionFailureDetails(String.format("User with id [%s] has already deactivated", id))
                    .build(), currentUser);
            throw new BadRequestException(
                    String.format("User with id [%s] has already deactivated", id)
            );
        }
        userCredential.setEnabled(isActive);
        String state = Boolean.TRUE.equals(isActive) ? "activate" : "deactivate";
        logService.save(LogDto.builder()
                .entityType(EntityType.USER)
                .entityId(id)
                .actionData(objectMapper.valueToTree(new UserActivateRequest(id, isActive)))
                .actionType(ActionType.ATTRIBUTES_UPDATED)
                .actionStatus(ActionStatus.SUCCESS)
                .build(), currentUser);
        return String.format("User with id [%s] has [%s] successful", id, state);
    }

    @Override
    public AppUserDto findByEmail(String email) {
        AppUser user = userRepository.findByEmail(email);
        return userMapper.toDto(user);
    }

    @Override
    public String syncUsers() {
        userRepository.findAll().forEach(appUser -> {
            userProducer.sendMessage(userMapper.toDto(appUser));
        });
        return "Sync users successfully";
    }

    private void validateSave(AppUserDto userDto, AppUserDto currentUser, ActionType actionType) {
        if (currentUser == null) return;
        if (
                (currentUser.getAuthority().equals(AuthorityType.CUSTOMER_USER.name()) &&
                        actionType.equals(ActionType.CREATED)) ||
                        (currentUser.getAuthority().equals(AuthorityType.TENANT_ADMIN.name()) &&
                                ((userDto.getAuthority().equals(AuthorityType.TENANT_ADMIN.name()) &&
                                        actionType.equals(ActionType.CREATED)) ||
                                        userDto.getAuthority().equals(AuthorityType.SYS_ADMIN.name()))) ||
                        (currentUser.getAuthority().equals(AuthorityType.SYS_ADMIN.name()) &&
                                actionType.equals(ActionType.CREATED) &&
                                userDto.getAuthority().equals(AuthorityType.SYS_ADMIN.name()))
        ) {
            throw new BadRequestException("You do not have permission to create or update SYS_ADMIN/TENANT_ADMIN");
        }
    }

    private void saveNewTenantBasicPlan(ActionType actionType, AppUser user) {
        if (actionType.equals(ActionType.CREATED)) {
            AccountPlan basicPlan = accountPlanRepository.findByName(AccountPlanType.BASIC).get();
            UserSubPlan userSubPlan = new UserSubPlan(
                    user,
                    basicPlan,
                    null
            );
            userSubPlanRepository.saveAndFlush(userSubPlan);
        }
    }

    private void saveNewCustomerDefaultComponent(
            ActionType actionType,
            AppUser user,
            AppUserDto currentUser
    ) {
        if (actionType.equals(ActionType.CREATED)
                && user.getAuthority().equals(AuthorityType.CUSTOMER_USER)
                && user.getRole().equals(RoleType.CUSTOMER)) {
            userComponentService.save(
                    userMapper.toDto(user),
                    componentMapper.toDto(
                            componentRepository.findByName("contact")
                    ),
                    List.of("READ"),
                    currentUser
            );
        }
    }

    private void checkIfEmailExist(String email, AppUserDto currentUser, Object actionData) {
        AppUser user = userRepository.findByEmail(email);
        if (user != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            logService.save(LogDto.builder()
                    .entityType(EntityType.USER)
                    .actionData(objectMapper.valueToTree(actionData))
                    .actionType(ActionType.CREATED)
                    .actionFailureDetails(String.format("User with email [%s] is already exist", email))
                    .actionStatus(ActionStatus.FAILURE)
                    .build(), currentUser);
            throw new BadRequestException(
                    String.format("User with email [%s] is already exist", email)
            );
        }
    }

    private void signUpAvailable(SignUpRequest signUpRequest) {
        boolean signUpAvailable = Boolean.parseBoolean(
                dataKvRepository.findById(AppInfoType.SIGNUP_AVAILABLE.name()).orElse(null).getValue());
        if (!signUpAvailable) {
            ObjectMapper objectMapper = new ObjectMapper();
            logService.save(LogDto.builder()
                    .entityType(EntityType.USER)
                    .actionStatus(ActionStatus.FAILURE)
                    .actionFailureDetails("Service is turn off sign up available")
                    .actionType(ActionType.CREATED)
                    .actionData(objectMapper.valueToTree(signUpRequest))
                    .build(), null);
            throw new BadRequestException("Service is turn off sign up available");
        }
    }

    private AppUser checkUserId(UUID id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("User with id [%s] is not found", id))
        );
    }

    private LocalDateTime convertTimestampToDateTime(Long timestamp) {
        return timestamp != null ?
                LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
                : null;
    }

    private String removeAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        temp = pattern.matcher(temp).replaceAll("");
        return temp.replaceAll("đ", "d");
    }
}
