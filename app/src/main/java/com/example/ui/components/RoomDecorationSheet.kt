package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FloorOption
import com.example.data.FurnitureCategory
import com.example.data.MasterItem
import com.example.data.PlacedFurniture
import com.example.data.RoomCustomizationRegistry
import com.example.data.RoomType
import com.example.data.WallpaperOption
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekTextDark

enum class DecorationSheetTab(val title: String, val icon: String) {
  FURNITURE("Furniture", "🛋️"),
  WALLPAPER("Wallpapers", "🎨"),
  FLOORING("Flooring", "🪵")
}

@Composable
fun RoomDecorationSheet(
  currentRoom: RoomType,
  masterInventory: List<MasterItem>,
  currentWallpaperId: String,
  currentFloorId: String,
  selectedPlacedFurniture: PlacedFurniture?,
  onPlaceFurniture: (MasterItem) -> Unit,
  onChangeWallpaper: (String) -> Unit,
  onChangeFloor: (String) -> Unit,
  onRotateFurniture: (String) -> Unit,
  onRemoveFurniture: (String) -> Unit,
  onResetRoom: () -> Unit,
  onCloseSheet: () -> Unit,
  modifier: Modifier = Modifier
) {
  var activeTab by remember { mutableStateOf(DecorationSheetTab.FURNITURE) }
  var selectedCategoryFilter by remember { mutableStateOf<FurnitureCategory?>(null) }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .shadow(16.dp, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
      .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
      .background(Color.White)
      .testTag("room_decoration_sheet"),
    color = Color.White
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Header Bar
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
            Text("🛋️", fontSize = 20.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Decorate ${currentRoom.displayName}",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = SleekTextDark
            )
            Text(
              text = "Personalize your room layout & furniture",
              fontSize = 12.sp,
              color = Color.Gray
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onResetRoom,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color(0xFFF1F5F9))
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Reset Room",
              tint = Color(0xFF64748B),
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          IconButton(
            onClick = onCloseSheet,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color(0xFFF1F5F9))
              .testTag("close_decoration_sheet")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Color(0xFF64748B),
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      // Contextual Selection Inspector Toolbar (When a piece of furniture in room is clicked)
      if (selectedPlacedFurniture != null) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF3E8FF))
            .border(1.5.dp, Color(0xFFC084FC), RoundedCornerShape(16.dp))
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(selectedPlacedFurniture.icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = selectedPlacedFurniture.displayName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF581C87)
              )
              Text(
                text = "Rotated: ${selectedPlacedFurniture.rotationDegrees}°",
                fontSize = 11.sp,
                color = Color(0xFF7E22CE)
              )
            }
          }

          Row {
            Button(
              onClick = { onRotateFurniture(selectedPlacedFurniture.instanceId) },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
              shape = RoundedCornerShape(12.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Icon(Icons.Default.RotateRight, contentDescription = "Rotate", modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Rotate 90°", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Button(
              onClick = { onRemoveFurniture(selectedPlacedFurniture.instanceId) },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
              shape = RoundedCornerShape(12.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Icon(Icons.Default.Delete, contentDescription = "Remove", modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Remove", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Tab Switcher
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xFFF1F5F9))
          .padding(4.dp)
      ) {
        DecorationSheetTab.values().forEach { tab ->
          val isSelected = activeTab == tab
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(16.dp))
              .background(if (isSelected) SleekPrimary else Color.Transparent)
              .clickable { activeTab = tab }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(tab.icon, fontSize = 14.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = tab.title,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color(0xFF64748B)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Tab Content
      when (activeTab) {
        DecorationSheetTab.FURNITURE -> {
          // Category Filter Chips
          val categories = FurnitureCategory.values()
          LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            item {
              val isAll = selectedCategoryFilter == null
              Box(
                modifier = Modifier
                  .clip(CapsuleShape)
                  .background(if (isAll) SleekTextDark else Color(0xFFF1F5F9))
                  .clickable { selectedCategoryFilter = null }
                  .padding(horizontal = 14.dp, vertical = 6.dp)
              ) {
                Text(
                  text = "All Items",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isAll) Color.White else Color(0xFF64748B)
                )
              }
            }
            items(categories) { cat ->
              val isSelected = selectedCategoryFilter == cat
              Box(
                modifier = Modifier
                  .clip(CapsuleShape)
                  .background(if (isSelected) SleekTextDark else Color(0xFFF1F5F9))
                  .clickable { selectedCategoryFilter = cat }
                  .padding(horizontal = 14.dp, vertical = 6.dp)
              ) {
                Text(
                  text = cat.displayName,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) Color.White else Color(0xFF64748B)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Filter placeable furniture items from inventory
          val furnitureItems = masterInventory.filter { item ->
            val isFurniture = item.id.startsWith("furn_") || item.id.startsWith("decor_")
            val matchesCategory = selectedCategoryFilter == null || item.id.contains(selectedCategoryFilter!!.name.lowercase())
            isFurniture && matchesCategory
          }

          if (furnitureItems.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📦", fontSize = 40.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "No furniture in this category!",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = SleekTextDark
                )
                Text(
                  text = "Visit the Shop to buy beds, tables, lamps & decorations!",
                  fontSize = 12.sp,
                  color = Color.Gray,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.padding(horizontal = 24.dp)
                )
              }
            }
          } else {
            LazyVerticalGrid(
              columns = GridCells.Fixed(2),
              modifier = Modifier.height(260.dp),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              items(furnitureItems) { item ->
                FurnitureItemCard(
                  item = item,
                  onPlace = { onPlaceFurniture(item) }
                )
              }
            }
          }
        }

        DecorationSheetTab.WALLPAPER -> {
          val wallpapers = RoomCustomizationRegistry.wallpapers
          LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height(300.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(wallpapers) { wp ->
              WallpaperOptionCard(
                wallpaper = wp,
                isSelected = wp.id == currentWallpaperId,
                onSelect = { onChangeWallpaper(wp.id) }
              )
            }
          }
        }

        DecorationSheetTab.FLOORING -> {
          val floors = RoomCustomizationRegistry.floorings
          LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height(300.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(floors) { fl ->
              FloorOptionCard(
                floor = fl,
                isSelected = fl.id == currentFloorId,
                onSelect = { onChangeFloor(fl.id) }
              )
            }
          }
        }
      }
    }
  }
}

val CapsuleShape = RoundedCornerShape(50)

@Composable
fun FurnitureItemCard(
  item: MasterItem,
  onPlace: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(Color(0xFFF8FAFC))
      .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(18.dp))
      .clickable { onPlace() }
      .padding(12.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(Color.White)
          .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
      ) {
        Text(item.icon, fontSize = 24.sp)
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = item.displayName,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = SleekTextDark,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = if (item.count > 0) "Owned x${item.count}" else "Available in Shop",
          fontSize = 11.sp,
          color = if (item.count > 0) Color(0xFF10B981) else Color(0xFFF59E0B),
          fontWeight = FontWeight.SemiBold
        )
      }
    }
  }
}

@Composable
fun WallpaperOptionCard(
  wallpaper: WallpaperOption,
  isSelected: Boolean,
  onSelect: () -> Unit
) {
  val swatches = listOf(Color(wallpaper.topColorHex), Color(wallpaper.bottomColorHex))

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC))
      .border(
        width = if (isSelected) 2.dp else 1.dp,
        color = if (isSelected) SleekPrimary else Color(0xFFE2E8F0),
        shape = RoundedCornerShape(18.dp)
      )
      .clickable { onSelect() }
      .padding(12.dp)
  ) {
    Column {
      // Swatch preview box
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Brush.linearGradient(swatches)),
        contentAlignment = Alignment.Center
      ) {
        if (isSelected) {
          Box(
            modifier = Modifier
              .clip(CapsuleShape)
              .background(Color.White)
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text("Applied ✓", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SleekPrimary)
          }
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = wallpaper.displayName,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = SleekTextDark,
        maxLines = 1
      )
      Text(
        text = "Pattern: ${wallpaper.patternName}",
        fontSize = 10.sp,
        color = Color.Gray,
        maxLines = 1
      )
    }
  }
}

@Composable
fun FloorOptionCard(
  floor: FloorOption,
  isSelected: Boolean,
  onSelect: () -> Unit
) {
  val floorColor = Color(floor.colorHex)

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC))
      .border(
        width = if (isSelected) 2.dp else 1.dp,
        color = if (isSelected) SleekPrimary else Color(0xFFE2E8F0),
        shape = RoundedCornerShape(18.dp)
      )
      .clickable { onSelect() }
      .padding(12.dp)
  ) {
    Column {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(floorColor),
        contentAlignment = Alignment.Center
      ) {
        Text("🪵", fontSize = 20.sp)
        if (isSelected) {
          Box(
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(6.dp)
              .clip(CapsuleShape)
              .background(Color.White)
              .padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Text("Applied ✓", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SleekPrimary)
          }
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = floor.displayName,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = SleekTextDark,
        maxLines = 1
      )
      Text(
        text = "Style: ${floor.patternType}",
        fontSize = 10.sp,
        color = Color.Gray,
        maxLines = 1
      )
    }
  }
}
