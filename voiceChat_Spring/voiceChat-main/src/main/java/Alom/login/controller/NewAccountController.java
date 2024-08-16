package Alom.login.controller;

import Alom.login.dto.NewAccountDto;
import Alom.login.domain.user.User;
import Alom.login.repository.user.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static Alom.login.util.GetCurrentUserId.getCurrentUserId;

@Controller
@RequestMapping("/create")
public class NewAccountController {
    private final UserRepository userRepository;
    public NewAccountController(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @GetMapping("/create/user")
    public String NewAccountInformationForm(NewAccountDto newAccountDto){
        return "create/user-form";
    }
    @PostMapping("/create/user")
    public String NewAccountInformationSave(
            @AuthenticationPrincipal OAuth2User oAuth2User,
            @RequestParam("name") String name,
            @RequestParam("nickname") String nickname,
            @RequestParam("ment") String ment,
            @RequestParam("icon") MultipartFile icon){
        String id = getCurrentUserId();
        String uploadDir = "profileImage/";
        String fileName = id + "_" + icon.getOriginalFilename();
        try {
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdir();
            }
            File file = new File(uploadDir + fileName);
            icon.transferTo(file);
            String iconUrl = uploadDir + fileName;
            User user = userRepository.findByProviderId(id);
            user.setUserName(name);
            user.setUserNickname(nickname);
            user.setUserMent(ment);
            user.setUserIconPath(iconUrl);
            userRepository.save(user);
        }catch (IOException e){
            e.printStackTrace();
            return "redirect:/create/user?error";
        }

        return "redirect:/";
    }
}
