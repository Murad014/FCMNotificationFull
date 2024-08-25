package com.pushnotification.pushnotification.controller;


import com.pushnotification.pushnotification.dto.ResponseDto;
import com.pushnotification.pushnotification.dto.UserDto;
import com.pushnotification.pushnotification.dto.UserUpdateDto;
import com.pushnotification.pushnotification.entity.UserEntity;
import com.pushnotification.pushnotification.repository.UserRepository;
import com.pushnotification.pushnotification.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    private static final int CREATE_STATUS_CODE = 201;
    private static final int OK_STATUS_CODE = 200;
    private static final String SELF_MAIN_LINK = "/api/v1/users";
    private final MessageSource messageSource;


    @Autowired
    public UserController(UserService userService, UserRepository userRepository, MessageSource messageSource) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    @PostMapping
    public ResponseEntity<ResponseDto<UserDto>> createUser(@Valid @RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        var responseDto = new ResponseDto<UserDto>();

        String message = messageSource.getMessage(
                "user.created.message",
                null,
                LocaleContextHolder.getLocale());

        responseDto.setMessage(message);
        responseDto.setData(createdUser);
        responseDto.setCode(CREATE_STATUS_CODE);
        responseDto.setPath(SELF_MAIN_LINK);


        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{cif}")
    public ResponseEntity<ResponseDto<UserUpdateDto>> updateUser(@PathVariable("cif") String cif,
                                                                 @Valid @RequestBody UserUpdateDto userDto) {

        UserUpdateDto updatedUser = userService.updateUser(cif, userDto);
        String message = messageSource.getMessage(
                "user.updated.message",
                null,
                LocaleContextHolder.getLocale());

        var responseDto = new ResponseDto<UserUpdateDto>();

        responseDto.setMessage(message);
        responseDto.setData(updatedUser);
        responseDto.setCode(OK_STATUS_CODE);
        responseDto.setPath(SELF_MAIN_LINK.concat("/").concat(cif));

        return new ResponseEntity<>(responseDto, HttpStatus.OK);

    }



    @GetMapping
    public ResponseEntity<List<UserEntity>> getUsers() {
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }

}
