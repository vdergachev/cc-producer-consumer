package com.cube.procon.producer.web.rest;

import static org.springframework.http.HttpStatus.ACCEPTED;

import com.cube.procon.api.UserInputDto;
import com.cube.procon.producer.service.Publisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserResource {

    private final Publisher publisher;

    @PostMapping(value = "/input")
    @ResponseStatus(ACCEPTED)
    public void input(@Valid @RequestBody final UserInputDto dto) {
        log.info("User input request {} is accepted ", dto);
        publisher.send(dto.getFirstName(), dto.getLastName());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<?, ?> handler(final MethodArgumentNotValidException ex) {
        final var errors = new HashMap<String, String>();
        ex.getBindingResult().getAllErrors().forEach(error ->
                errors.put(((FieldError) error).getField(), error.getDefaultMessage())
        );
        return errors;
    }
}
