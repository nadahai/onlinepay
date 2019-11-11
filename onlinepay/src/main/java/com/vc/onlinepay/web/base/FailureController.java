package com.vc.onlinepay.web.base;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/failure")
public class FailureController extends BaseController {
    
    @RequestMapping(value = "", produces = "text/html;charset=UTF-8")
    public String  success() {
        return "failure";
    }
}
