package jpabook.jpashop.controller.DTO;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberForm { //DTO
    @NotEmpty(message = "회원 이름은 필수입니다.")
    private String name;
//    @NotEmpty(message = ""
    private String city;
    private String street;
    private String zipcode;
}
