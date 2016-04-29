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

import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import java.util.*

/**
 * Based on CYO:
 *
 * http://danielstern.github.io/cyo/
 */
class StoryParser {
    data class StoryEntry(
            val id: String,
            var title: String?,
            var text: String,
            var callToAction: String?,
            var choices: MutableMap<String, String>?
    )

    fun parseStory(path: String = "story.xml"): Map<String, StoryEntry> {
        val result: MutableMap<String, StoryEntry> = LinkedHashMap();

        val inputStream = javaClass.classLoader.getResourceAsStream(path)!!

        val doc = SAXBuilder().build(inputStream);

        doc.rootElement.getChildren("page").forEach { page ->
            val id = page.getAttribute("id").value!!;

            val storyEntry = StoryEntry(id = id,
                    title = "",
                    text = "",
                    callToAction = "E agora?",
                    choices = LinkedHashMap())

            page.getChildren().forEach { c ->
                when (c.name) {
                    "h2" -> onH2(id, c, storyEntry)
//                  "h3" -> onH3(id, c, contentBuilder)
                    "p" -> onPara(id, c, storyEntry)
                    "button" -> onButton(id, c, storyEntry)
                }
            }

            result[id] = storyEntry
        }

        return result;
    }

    private fun onH2(id: String, c: Element, e: StoryEntry) {
        e.title = "## ${c.textTrim}"
    }

    private fun onButton(id: String, c: Element, e: StoryEntry) {
        val choiceValue = c.getAttributeValue("choice")!!

        e.choices!![choiceValue] = c.textTrim
    }

    private fun onPara(id: String, c: Element, e: StoryEntry) {
        e.text += "${c.textTrim}\n"
    }
}