package com.mogaco.project.global.errors;

import com.mogaco.project.auth.application.InvalidTokenException;
import com.mogaco.project.auth.application.LoginFailException;
import com.mogaco.project.auth.application.LoginNotFoundException;
import com.mogaco.project.meet.application.MeetingNotFoundException;
import com.mogaco.project.member.application.MemberNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseBody
@ControllerAdvice
public class ControllerErrorAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgument() {
        return new ErrorResponse("Illegal Argument");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MemberNotFoundException.class)
    public ErrorResponse handleMemberNotFound() {
        return new ErrorResponse("Member not found");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidTokenException.class)
    public ErrorResponse handleInvalidAccessToken() {
        return new ErrorResponse("Token Invalid");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(LoginNotFoundException.class)
    public ErrorResponse handleLoginNotFound() {
        return new ErrorResponse("Login user not found");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LoginFailException.class)
    public ErrorResponse handleLoginFailException() {
        return new ErrorResponse("Log-in failed");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MeetingNotFoundException.class)
    public ErrorResponse handleMeetingNotFound() {
        return new ErrorResponse("Member not found");
    }
}
