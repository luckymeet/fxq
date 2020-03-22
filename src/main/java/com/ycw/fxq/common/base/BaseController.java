package com.ycw.fxq.common.base;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {

	@GetMapping("/{name}/aaa")
    public String pathVariable2(@PathVariable(value = "name") String name){
        return name;
    }

}
