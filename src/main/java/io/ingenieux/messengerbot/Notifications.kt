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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class IdContainer(
        @JsonProperty("id")
        var id: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class OptinRef(
        @JsonProperty("ref")
        var ref: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class NotificationDelivery(
        @JsonProperty("watermark")
        var watermark: Date,

        @JsonProperty("seq")
        var seq: Long,

        @JsonProperty("mids")
        var mids: Array<String>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class NotificationPostback(
        @JsonProperty("postback")
        var payload: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MessagingEntry(
        @JsonProperty("sender")
        var sender: IdContainer,

        @JsonProperty("recipient")
        var recipient: IdContainer,

        @JsonProperty("timestamp")
        var timestamp: Date?,

        @JsonProperty("optin")
        var optin: OptinRef?,

        @JsonProperty("message")
        var message: Message?,

        @JsonProperty("delivery")
        var delivery: NotificationDelivery?,

        @JsonProperty("postback")
        var postback: NotificationPostback?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PayloadType(
        var url: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageAttachment(
        var type: String,

        var payload: PayloadType
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Message(
        @JsonProperty("mid")
        var mid: String,

        @JsonProperty("seq")
        var seq: Long,

        @JsonProperty("text")
        var text: String,

        @JsonProperty("attachments")
        var attachments: Array<MessageAttachment>?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class NotificationEntry(
        @JsonProperty("id")
        var pageId: String?,

        @JsonProperty("time")
        var time: Date?,

        @JsonProperty("messaging")
        var messagingEntries: Array<MessagingEntry>?
        )

@JsonIgnoreProperties(ignoreUnknown = true)
data class Notification(
        @JsonProperty("object")
        var notificationObject: String,

        @JsonProperty("entry")
        var entries: Array<NotificationEntry>?
)