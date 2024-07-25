package Alom.voiceChat.controller;


import Alom.voiceChat.service.chat.ChatServiceMain;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final ChatServiceMain;


    @GetMapping("/")
    public String goChatRoom(Model model, @AuthenticationPrincipal Principa)
}
