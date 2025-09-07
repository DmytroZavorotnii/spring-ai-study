package zavorotnii.dmytro.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
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

    @Autowired
    private VectorStore vectorStore;

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder){
        return builder
                .defaultAdvisors(
                        getHistoryAdvisor(),
                        getRagAdvisor()
                ).build();
    }

    private Advisor getHistoryAdvisor() {
        return MessageChatMemoryAdvisor
                .builder(getChatMemory())
                .build();
    }

    private Advisor getRagAdvisor(){
        return QuestionAnswerAdvisor
                .builder(vectorStore)
                .searchRequest(SearchRequest
                        .builder()
                        .topK(4)
                        .build())
                .build();
    }

    private ChatMemory getChatMemory() {
        return PostgresChatMemory
                .builder()
                .maxMessages(repeatLastN)
                .chatMemoryRepository(chatRepository)
                .build();
    }
}
