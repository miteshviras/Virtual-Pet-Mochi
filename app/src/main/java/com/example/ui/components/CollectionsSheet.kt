package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CollectionCategory
import com.example.data.CollectionItem
import com.example.data.MasterItem
import com.example.data.MetaRegistry
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsSheet(
  unlockedCollections: Set<String>,
  masterInventory: List<MasterItem>,
  highScoresMap: Map<String, Int>,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var selectedCategory by remember { mutableStateOf(CollectionCategory.FOOD) }

  val catalog = MetaRegistry.getCollectionCatalog(unlockedCollections, masterInventory, highScoresMap)
  val categoryCatalog = catalog.filter { it.category == selectedCategory }
  val totalInCat = categoryCatalog.size
  val unlockedInCat = categoryCatalog.count { it.isUnlocked }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color.White
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp)
        .testTag("collections_sheet")
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFFEC4899)))),
            contentAlignment = Alignment.Center
          ) {
            Text("📚", fontSize = 20.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Mochi Codex & Collections",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = SleekTextDark
            )
            Text(
              text = "Unlocked $unlockedInCat / $totalInCat items in ${selectedCategory.displayName}",
              fontSize = 11.sp,
              color = SleekTextMuted
            )
          }
        }

        IconButton(
          onClick = onDismiss,
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color(0xFFF1F5F9))
            .testTag("close_collections")
        ) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Category Tabs
      LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(CollectionCategory.values()) { cat ->
          val isSelected = selectedCategory == cat
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(if (isSelected) SleekPrimary else Color(0xFFF1F5F9))
              .clickable { selectedCategory = cat }
              .padding(horizontal = 12.dp, vertical = 8.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(cat.icon, fontSize = 12.sp)
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = cat.displayName,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color(0xFF64748B)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(340.dp)
      ) {
        items(categoryCatalog) { item ->
          CollectionGridCard(item = item)
        }
      }
    }
  }
}

@Composable
fun CollectionGridCard(item: CollectionItem) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(if (item.isUnlocked) Color(0xFFF8FAFC) else Color(0xFFF1F5F9))
      .border(
        1.dp,
        if (item.isUnlocked) Color(0xFFE2E8F0) else Color(0xFFCBD5E1),
        RoundedCornerShape(16.dp)
      )
      .padding(10.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      if (item.isUnlocked) {
        Text(item.icon, fontSize = 28.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = item.name,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = SleekTextDark,
          maxLines = 1
        )
        Text(
          text = item.unlockSource,
          fontSize = 9.sp,
          color = SleekTextMuted
        )
      } else {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color(0xFFE2E8F0)),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "??? Locked",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF94A3B8)
        )
        Text(
          text = item.unlockSource,
          fontSize = 9.sp,
          color = Color(0xFF94A3B8)
        )
      }
    }
  }
}
