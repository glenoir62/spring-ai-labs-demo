package com.gleo.labs.service;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

import com.gleo.labs.config.ChatBotProperties;
import java.util.Map;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.stereotype.Service;

@Service
public class OllamaService {

  private final ChatClient chatClient;
  private final PDFVectorStore PDFVectorStore;

  public OllamaService(ChatClient.Builder chatClientBuilder, PDFVectorStore PDFVectorStore,
                       ChatBotProperties chatBotProperties) {
    this.PDFVectorStore = PDFVectorStore;
    InMemoryChatMemory chatMemory = new InMemoryChatMemory();

    Message systemTemplate =
        new SystemPromptTemplate(chatBotProperties.getPromptTemplate()).createMessage(
            Map.of("name", chatBotProperties.getName()));

    this.chatClient = chatClientBuilder
        .defaultSystem(systemTemplate.getText())
        .defaultAdvisors(new PromptChatMemoryAdvisor(chatMemory))
        .build();
  }

  public String call(String message) {
    return chatClient.prompt().user(message).call().chatResponse().getResult().getOutput()
        .getText();
  }

  public String callWithContext(String message, String contextId) {

    ChatResponse
        chatResponse = chatClient.prompt()
        .user(message)
        .advisors(
            advisor ->
                advisor.param(CHAT_MEMORY_CONVERSATION_ID_KEY, contextId)
                    .advisors(
                        QuestionAnswerAdvisor.builder(PDFVectorStore.getVectorStore())
                            .searchRequest(SearchRequest.builder()
                                .query(message)
                                .topK(15)
                                .similarityThreshold(0.3)
                                .build()
                            )
                            .build()))
        .call()
        .chatResponse();

    return chatResponse.getResult().getOutput().getText();
  }


}
