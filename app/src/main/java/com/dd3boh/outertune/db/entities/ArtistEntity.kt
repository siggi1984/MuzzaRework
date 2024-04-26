package com.dd3boh.outertune.db.entities

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zionhuang.innertube.YouTube
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.apache.commons.lang3.RandomStringUtils
import java.time.LocalDateTime

@Immutable
@Entity(tableName = "artist")
data class ArtistEntity(
    @PrimaryKey val id: String,
    val name: String,
    val thumbnailUrl: String? = null,
    val channelId: String? = null,
    val lastUpdateTime: LocalDateTime = LocalDateTime.now(),
    val bookmarkedAt: LocalDateTime? = null,
) {
    val isYouTubeArtist: Boolean
        get() = id.startsWith("UC")

    val isLocalArtist: Boolean
        get() = id.startsWith("LA")

    fun localToggleLike() = copy(
        bookmarkedAt = if (bookmarkedAt != null) null else LocalDateTime.now(),
    )

    fun toggleLike() = localToggleLike().also {
        CoroutineScope(Dispatchers.IO).launch {
            if (channelId == null)
                YouTube.subscribeChannel(YouTube.getChannelId(id), bookmarkedAt == null)
            else
                YouTube.subscribeChannel(channelId, bookmarkedAt == null)
            this.cancel()
        }
    }

    companion object {
        fun generateArtistId() = "LA" + RandomStringUtils.random(8, true, false)
    }
}