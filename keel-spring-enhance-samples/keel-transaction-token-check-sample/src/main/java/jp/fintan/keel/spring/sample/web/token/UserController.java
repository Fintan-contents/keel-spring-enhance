package jp.fintan.keel.spring.sample.web.token;

import jakarta.validation.constraints.NotEmpty;

import jp.fintan.keel.spring.web.token.transaction.InvalidTransactionTokenException;
import jp.fintan.keel.spring.web.token.transaction.TransactionTokenCheck;
import jp.fintan.keel.spring.web.token.transaction.TransactionTokenType;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@TransactionTokenCheck("user")
public class UserController {

    private final JdbcTemplate jdbcTemplate;

    public UserController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @ModelAttribute
    public UserForm userForm() {
        return new UserForm();
    }

    @GetMapping("/")
    public String input() {
        return "input";
    }

    @TransactionTokenCheck(type = TransactionTokenType.BEGIN)
    @PostMapping("/confirm")
    public String confirm(@Validated UserForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "input";
        }
        return "confirm";
    }

    @TransactionTokenCheck
    @Transactional
    @PostMapping("/create")
    public String create(@Validated UserForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "input";
        }

        jdbcTemplate.update("insert into users (name) values (?)", form.name);

        return "redirect:/complete";
    }

    @GetMapping("/complete")
    public String complete() {
        return "complete";
    }

    @ExceptionHandler(InvalidTransactionTokenException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String invalidTransactionTokenExceptionHandler(InvalidTransactionTokenException e) {
        return "error/token-error";
    }

    static class UserForm {
        @NotEmpty
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}