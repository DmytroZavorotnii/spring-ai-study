package zavorotnii.dmytro.service;

import lombok.SneakyThrows;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import zavorotnii.dmytro.model.Chat;
import zavorotnii.dmytro.model.ChatEntry;
import zavorotnii.dmytro.model.Role;
import zavorotnii.dmytro.repository.ChatRepository;

import java.util.List;

@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatClient chatClient;

    public List<Chat> getAllChats() {
//        return chatRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        return chatRepository.findAllWithoutHistory();
    }

    public Chat getChatById(String chatId) {
        return chatRepository.findByIdWithHistory(chatId).orElseThrow();
    }

    public Chat create(String title) {
        Chat chat = Chat
                .builder()
                .title(title)
                .build();
        return chatRepository.save(chat);
    }

    public void deleteById(String chatId) {
        chatRepository.deleteById(chatId);
    }

    @Transactional
    public void proceedInteraction(String chatId, String prompt) {
//        chatServiceProxy.addChatEntry(chatId, prompt, Role.USER);

        String answer = chatClient.prompt().user(prompt).call().content();
//        chatServiceProxy.addChatEntry(chatId, answer, Role.ASSISTANT);
    }

    @Transactional
    public void addChatEntry(String chatId, String prompt, Role role){
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        chat.addEntry(ChatEntry.builder()
                .content(prompt)
                .role(role)
                .build()
        );
    }

    public SseEmitter proceedInteractionWithStreaming(String chatId, String userPrompt) {
//        chatServiceProxy.addChatEntry(chatId, userPrompt, Role.USER);
        SseEmitter emitter = new SseEmitter(0L);
        StringBuilder answer = new StringBuilder();

        chatClient
                .prompt()
                .user(userPrompt)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .chatResponse()
                .subscribe(
                        response -> processToken(response, emitter, answer),
                        emitter::completeWithError
//                        ()-> chatServiceProxy.addChatEntry(chatId, answer.toString(), Role.ASSISTANT)
                );

        return emitter;
    }

    @SneakyThrows
    private static void processToken(ChatResponse response, SseEmitter emitter, StringBuilder builder) {
        AssistantMessage token = response.getResult().getOutput();
        emitter.send(token);
        builder.append(token.getText());
    }
}
