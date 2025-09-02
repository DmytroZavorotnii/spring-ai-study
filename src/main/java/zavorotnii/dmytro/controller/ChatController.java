package zavorotnii.dmytro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import zavorotnii.dmytro.model.Chat;
import zavorotnii.dmytro.service.ChatService;

@Controller
public class ChatController {
    @Autowired
    private ChatService chatService;

    @GetMapping("/")
    public String mainPage(ModelMap model){
        model.addAttribute("chats", chatService.getAllChats());
        return "chat";
    }

    @GetMapping("/chat/{chatId}")
    public String showChat(@PathVariable String chatId, ModelMap model){
        model.addAttribute("chats", chatService.getAllChats());
        model.addAttribute("chat", chatService.getChatById(chatId));
        return "chat";
    }

    @PostMapping("/chat/new")
    public String createChat(@RequestParam String title){
        Chat chat = chatService.create(title);
        return "redirect:/chat/" + chat.getId();
    }

    @PostMapping("/chat/{chatId}/delete")
    public String deleteChat(@PathVariable String chatId){
        chatService.deleteById(chatId);
        return "redirect:/";
    }

    @PostMapping("/chat/{chatId}/entry")
    public String talkToModel(@PathVariable String chatId, @RequestParam String prompt){
        chatService.proceedInteraction(chatId, prompt);

        return "redirect:/chat/" + chatId;
    }
}
