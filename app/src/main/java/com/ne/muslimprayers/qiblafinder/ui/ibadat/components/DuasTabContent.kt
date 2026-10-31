package com.ne.muslimprayers.qiblafinder.ui.ibadat.components

import androidx.compose.foundation.border
import com.ne.muslimprayers.qiblafinder.core.utils.clickableDebounced
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.domain.model.DuaItem
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.ErrorState
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicSearchBar
import com.ne.muslimprayers.qiblafinder.ui.components.LoadingState

@Composable
fun DuasTabContent(
    duas: List<DuaItem>,
    categories: List<String>,
    selectedCategory: String,
    searchQuery: String,
    isLoading: Boolean,
    errorMessage: String?,
    isLandscape: Boolean,
    onRetry: () -> Unit,
    onSearchChange: (String) -> Unit,
    onSelectCategory: (String) -> Unit,
    onCopy: (DuaItem) -> Unit,
    onShare: (DuaItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.sdp)
            .padding(bottom = 8.sdp)
    ) {
        IslamicSearchBar(
            query = searchQuery,
            onQueryChange = onSearchChange,
            placeholder = "Search Duas by category or text...",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.sdp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.sdp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { cat ->
                val isSelected = cat == selectedCategory
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.sdp))
                        .clickableDebounced { onSelectCategory(cat) }
                        .border(
                            width = if (isSelected) 1.sdp else 0.5.sdp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.sdp)
                        ),
                    shape = RoundedCornerShape(20.sdp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                ) {
                    AppText.BodySmall(
                        text = cat,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.sdp, vertical = 6.sdp),
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.sdp))

        if (isLoading && duas.isEmpty()) {
            LoadingState(message = "Loading Fortress of the Muslim Duas...")
        } else if (errorMessage != null && duas.isEmpty()) {
            ErrorState(message = errorMessage, onRetry = onRetry)
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 32.sdp),
                verticalArrangement = Arrangement.spacedBy(12.sdp)
            ) {
                items(duas) { item ->
                    DuaItemCard(
                        dua = item,
                        onCopy = { onCopy(item) },
                        onShare = { onShare(item) }
                    )
                }
            }
        }
    }
}
