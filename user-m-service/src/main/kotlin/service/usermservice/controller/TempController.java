package service.usermservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TempController {

    private Environment env;

    @Autowired
    public TempController(Environment env) {
        this.env = env;
    }

    @GetMapping("welcome")
    public String welcome() {
        return env.getProperty("greeting.message");
    }
}
