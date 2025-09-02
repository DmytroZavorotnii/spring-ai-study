package zavorotnii.dmytro.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zavorotnii.dmytro.chat.memory.PostgresChatMemory;
import zavorotnii.dmytro.repository.ChatRepository;

@Configuration
public class AiOllamaConfig {
    @Autowired
    private ChatRepository chatRepository;

    @Value("${spring.ai.ollama.chat.options.repeat-last-n}")
    private Integer repeatLastN;

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder){
        return builder
                .defaultAdvisors(getAdvisor())
                .build();
    }

    private Advisor getAdvisor() {
        return MessageChatMemoryAdvisor.builder(getChatMemory()).build();
    }

    private ChatMemory getChatMemory() {
        return PostgresChatMemory
                .builder()
                .maxMessages(repeatLastN)
                .chatMemoryRepository(chatRepository)
                .build();
    }

//    private ChatMemory getChatMemory() {
//        return MessageWindowChatMemory
//                .builder()
//                .maxMessages(repeatLastN)
//                .chatMemoryRepository(chatRepository)
//                .build();
//    }
}
