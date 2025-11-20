package com.gleo.labs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "chatbot")
public class ChatBotProperties {

  private String name;
  private String promptTemplate;
  private String userContext;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPromptTemplate() {
    return promptTemplate;
  }

  public void setPromptTemplate(String promptTemplate) {
    this.promptTemplate = promptTemplate;
  }

  public String getUserContext() {
    return userContext;
  }

  public void setUserContext(String userContext) {
    this.userContext = userContext;
  }

}
