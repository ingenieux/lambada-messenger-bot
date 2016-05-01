/*
 * Copyright (c) 2016 ingenieux Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.ingenieux.messengerbot

import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.mashape.unirest.http.Unirest
import io.ingenieux.lambada.runtime.ApiGateway
import io.ingenieux.lambada.runtime.LambadaFunction
import io.ingenieux.lambada.runtime.model.PassthroughRequest
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset

class Bot {
    val MAPPER = ObjectMapper()

    val LOGGER: Logger = LoggerFactory.getLogger(Bot::class.java)

    @LambadaFunction(name = "fb_validateBot",
            memorySize = 512,
            timeout = 180,
            api = arrayOf(
                    ApiGateway(path = "/bot",
                            method = ApiGateway.MethodType.GET
                    )
            ))
    fun validateBot(i: InputStream, o: OutputStream, c: Context) {
        val request = PassthroughRequest.getRequest(MAPPER, i);

        LOGGER.info("request: {}", MAPPER.writeValueAsString(request));

        if ("subscribe".equals(request.params.queryString["hub.mode"])) {
            val result = request.params.queryString["hub.challenge"]!!

            o.write(result.toByteArray())

            return
        }
    }

    @LambadaFunction(name = "fb_doMessage",
            memorySize = 512,
            timeout = 180,
            api = arrayOf(
                    ApiGateway(path = "/bot")
            ))
    fun doMessage(i: InputStream, o: OutputStream, c: Context) {
        var request = PassthroughRequest.getRequest(MAPPER, Notification::class.java, i)

        LOGGER.info("Request: {}", MAPPER.writeValueAsString(request));

        request.body.entries?.forEach { entry ->
            entry.messagingEntries?.forEach {
                handleMessage(request, it)
            }
        }
    }

    val storyMap = StoryParser().parseStory();

    fun handleMessage(req: PassthroughRequest<Notification>, m: MessagingEntry) {
        if (null != m.delivery)
            return

        var command = (if (null != m.postback)
            m.postback!!.payload
        else if (null != m.message) m.message!!.text
        else "")!!.trim()

        if ("restart".equals(command))
            command = "intro"

        if (StringUtils.isBlank(command)) {
            sendMessage(req, m.sender, "Amiguinho, nao fode. Digite /restart para comecar.")
            return
        }

        if (storyMap.containsKey(command)) {
            val resp = storyMap[command]!!

            sendMessage(req, m.sender, resp)
        } else {
            sendMessage(req, m.sender, "Que porra eh essa?!?")
        }
    }

    fun sendMessage(req: PassthroughRequest<Notification>, sender: IdContainer, s: StoryParser.StoryEntry) {
        sendMessage(req, sender, s.text)

        val payload = MAPPER.createObjectNode()

        payload
                .putObject("recipient")
                .put("id", sender.id)

        val attachmentNode =
                payload.putObject("message")
                        .putObject("attachment")

        attachmentNode.put("type", "template")

        val templatePayloadNode =
                attachmentNode.putObject("payload")

        templatePayloadNode.put("template_type", "generic")

        val elementsNode = templatePayloadNode.putArray("elements")

        val newElementNode = elementsNode.objectNode()

        newElementNode.put("title", s.callToAction!!)

        val buttonsArrayNode = MAPPER.createArrayNode()

        s.choices?.forEach {
            val buttonsNode = MAPPER.createObjectNode()

            buttonsNode.put("type", "postback")
            buttonsNode.put("payload", it.key)
            buttonsNode.put("title", it.value)

            buttonsArrayNode.add(buttonsNode)
        }

        newElementNode.set("buttons", buttonsArrayNode)

        elementsNode.add(newElementNode)

        val payloadAsString = MAPPER.writeValueAsString(payload)

        val response =
                Unirest
                        .post("https://graph.facebook.com/v2.6/me/messages")
                        .header("Content-Type", "application/json")
                        .queryString("access_token", req.stageVariables["TOKEN"]!!)
                        .body(payloadAsString)
                        .asString()

        LOGGER.info("response: {}", response)

        assert(200 == response.status)
    }

    fun sendMessage(req: PassthroughRequest<Notification>, sender: IdContainer, s: String) {
        val payload = MAPPER.createObjectNode()

        payload
                .putObject("recipient")
                .put("id", sender.id)

        payload
                .putObject("message")
                .put("text", s)

        val payloadAsString = MAPPER.writeValueAsString(payload)

        val response =
                Unirest
                        .post("https://graph.facebook.com/v2.6/me/messages")
                        .header("Content-Type", "application/json")
                        .queryString("access_token", req.stageVariables["TOKEN"]!!)
                        .body(payloadAsString)
                        .asString()

        LOGGER.info("response: ${response} (status=${response.status}, body=${response.body})")

        assert(200 == response.status)
    }

    fun handleTextMessage(message: Message) {
        val text = message.text
    }
}
