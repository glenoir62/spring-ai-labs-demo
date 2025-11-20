import chainlit as cl
import requests
import uuid

endpoint = 'http://localhost:8080/ai/callWithContext'

@cl.on_chat_start
async def start():
    # Générer un nouvel ID unique à chaque session
    cl.user_session.set("context_id", str(uuid.uuid4()))

@cl.on_message
async def main(message: cl.Message):
    try:
        context_id = cl.user_session.get("context_id")

        params = {
            'message': requests.utils.quote(message.content),
            'contextId': context_id
        }

        response = requests.get(endpoint, params=params, timeout=30)
        response.raise_for_status()

        await cl.Message(content=response.text).send()

    except requests.exceptions.RequestException as e:
        await cl.Message(
            content=f"❌ Erreur : {str(e)}"
        ).send()