package zavorotnii.dmytro.chat.memory;

import lombok.Builder;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import zavorotnii.dmytro.model.Chat;
import zavorotnii.dmytro.model.ChatEntry;
import zavorotnii.dmytro.repository.ChatRepository;

import java.util.Comparator;
import java.util.List;

@Builder
public class PostgresChatMemory implements ChatMemory {
    private ChatRepository chatMemoryRepository;
    private int maxMessages;

    @Override
    public void add(String conversationId, List<Message> messages) {
        Chat chat = chatMemoryRepository.findByIdWithHistory(conversationId).orElseThrow();
        messages.stream().map(ChatEntry::toChatEntry).forEach(chat::addEntry);
        chatMemoryRepository.save(chat);
    }

    @Override
    public List<Message> get(String conversationId) {
        Chat chat = chatMemoryRepository.findByIdWithHistory(conversationId).orElseThrow();
        return chat.getHistory().stream()
                .sorted(Comparator.comparing(ChatEntry::getCreatedAt).reversed())
                .map(ChatEntry::toMessage)
                .limit(maxMessages)
                .toList();
    }

    @Override
    public void clear(String conversationId) {
        // not implemented
    }
}
