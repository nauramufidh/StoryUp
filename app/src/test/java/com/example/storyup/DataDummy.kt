package com.example.storyup

import com.example.storyup.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        return List(20) { index ->
            ListStoryItem(
                id = index.toString(),
                name = "Story $index",
                description = "Description for story $index",
                photoUrl = "https://example.com/photo/$index.jpg"
            )
        }
    }
}
