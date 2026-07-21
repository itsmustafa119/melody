package com.mustafa.melody.presentation.downloads

import androidx.annotation.StringRes
import com.mustafa.melody.R

enum class DownloadSortOption(
    @param:StringRes val labelResId: Int,
) {
    RECENT(R.string.sort_recent),
    TITLE(R.string.sort_title),
    ARTIST(R.string.sort_artist),
}
