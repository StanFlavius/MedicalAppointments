package com.example.medicalappointments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionAdvice {

    private static final String DEFAULT_MESSAGE = "Something went wrong. Please try again later!";
    private static final String BAD_REQUEST_MESSAGE = "Invalid parameters!";

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handle(EntityNotFoundException e) {
        ModelAndView modelAndView = new ModelAndView("err_not_found");

        var error = ErrorBody.builder()
                .message(String.format("%s with ID %s doesn't exist!", e.getEntityType(), e.getEntityId()))
                .build();

        modelAndView.addObject("exception", error);
        return modelAndView;
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView handle(ForbiddenException e) {
        return new ModelAndView("err_access_denied");
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handle(NumberFormatException exception) {
        ModelAndView modelAndView = new ModelAndView("err_number_format");
        modelAndView.addObject("message", exception.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handle(Exception exception) {
        ModelAndView modelAndView = new ModelAndView("err_default");
        modelAndView.addObject("message", DEFAULT_MESSAGE);
        return modelAndView;
    }
}
