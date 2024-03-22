# tolkien-ai

A project to develop an AI chatbot capable of answering questions about JRR Tolkien's legendarium.

## Setup

A file called `secrets.yml` must exist at the root of this project with the following config:

```yaml
tolkienai:
  openai:
    key: openai_api_key
  weaviate:
    key: weaviate_api_key

spring:
  data:
    mongodb:
      username: mongo_username
      password: mongo_password
```