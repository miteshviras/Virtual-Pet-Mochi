package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CosmeticSlot
import com.example.data.CustomizationRegistry
import com.example.data.MasterItem
import com.example.data.PetEmotion
import com.example.data.PetEntity
import com.example.data.RoomType
import com.example.ui.theme.CoinAmber
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted

enum class CustomizationTabCategory(val title: String, val icon: String) {
  SKIN_PATTERN("Skin & Pattern", "🎨"),
  FACIAL("Eyes & Mouth", "👀"),
  HEADWEAR("Headwear", "👑"),
  CLOTHING("Clothing", "👕"),
  ACCESSORIES("Accessories", "✨")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CustomizationSheet(
  pet: PetEntity,
  masterItems: List<MasterItem>,
  userLevel: Int,
  userCoins: Int,
  onDismiss: () -> Unit,
  onEquipSlot: (CosmeticSlot, String) -> Unit,
  onUnequipSlot: (CosmeticSlot) -> Unit,
  onUpdateAppearance: (String, String) -> Unit,
  onResetAppearance: () -> Unit,
  onSaveCustomization: (PetEntity) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var selectedTab by remember { mutableStateOf(CustomizationTabCategory.SKIN_PATTERN) }
  var previewPetState by remember(pet) { mutableStateOf(pet) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color.White
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
      // Header Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Pet Studio & Dress-Up 👗✨",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = SleekTextDark
          )
          Text(
            text = "Level $userLevel • Coins: ✨ $userCoins",
            fontSize = 12.sp,
            color = CoinAmber,
            fontWeight = FontWeight.SemiBold
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(Color(0xFFF1F5F9))
              .clickable {
                previewPetState = pet
                onResetAppearance()
              }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(16.dp), tint = SleekTextDark)
              Spacer(modifier = Modifier.width(4.dp))
              Text("Reset", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SleekTextDark)
            }
          }

          Spacer(modifier = Modifier.width(8.dp))

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_customization_sheet")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = SleekTextDark)
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Mochi Interactive Live Preview Container
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
          .clip(RoundedCornerShape(24.dp))
          .background(
            Brush.verticalGradient(
              listOf(Color(0xFFEFF6FF), Color(0xFFE0E7FF))
            )
          )
          .border(2.dp, Color.White, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
      ) {
        MochiPetView(
          pet = previewPetState,
          room = RoomType.PLAY,
          emotion = PetEmotion.HAPPY,
          notificationMsg = null,
          onPetClick = { },
          modifier = Modifier.fillMaxWidth()
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Category Tabs
      TabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = Color(0xFFF1F5F9),
        contentColor = SleekPrimary,
        modifier = Modifier.clip(RoundedCornerShape(16.dp))
      ) {
        CustomizationTabCategory.values().forEach { category ->
          Tab(
            selected = selectedTab == category,
            onClick = {
              selectedTab = category
              com.example.util.SoundManager.playCategorySwitchSound()
            },
            text = {
              Text(
                text = "${category.icon} ${category.title.split(" ").first()}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Grid content for active category
      Box(modifier = Modifier.height(280.dp)) {
        when (selectedTab) {
          CustomizationTabCategory.SKIN_PATTERN -> {
            Column {
              Text("Skin Colors 🎨", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SleekTextMuted)
              Spacer(modifier = Modifier.height(6.dp))
              LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(CustomizationRegistry.skinColors) { skin ->
                  val isSelected = previewPetState.equippedColor == skin.id
                  val isLocked = userLevel < skin.unlockLevel

                  Box(
                    modifier = Modifier
                      .size(60.dp)
                      .clip(CircleShape)
                      .background(
                        Brush.linearGradient(skin.colors.map { Color(it) })
                      )
                      .border(
                        if (isSelected) 3.dp else 1.dp,
                        if (isSelected) SleekPrimary else Color.White,
                        CircleShape
                      )
                      .clickable {
                        if (!isLocked) {
                          previewPetState = previewPetState.copy(equippedColor = skin.id)
                          onUpdateAppearance("skin", skin.id)
                        }
                      },
                    contentAlignment = Alignment.Center
                  ) {
                    if (isLocked) {
                      Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color.White, modifier = Modifier.size(20.dp))
                    } else if (isSelected) {
                      Icon(Icons.Default.Check, contentDescription = "Selected", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              Text("Body Patterns 🌌", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SleekTextMuted)
              Spacer(modifier = Modifier.height(6.dp))
              LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(CustomizationRegistry.bodyPatterns) { pattern ->
                  val isSelected = previewPetState.bodyPattern == pattern.id
                  val isLocked = userLevel < pattern.unlockLevel

                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(16.dp))
                      .background(if (isSelected) SleekPrimary else Color(0xFFF8FAFC))
                      .border(1.dp, if (isSelected) SleekPrimary else Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                      .clickable {
                        if (!isLocked) {
                          previewPetState = previewPetState.copy(bodyPattern = pattern.id)
                          onUpdateAppearance("pattern", pattern.id)
                        }
                      }
                      .padding(horizontal = 12.dp, vertical = 8.dp)
                  ) {
                    Text(
                      text = "${pattern.icon} ${pattern.displayName}",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isSelected) Color.White else SleekTextDark
                    )
                  }
                }
              }
            }
          }

          CustomizationTabCategory.FACIAL -> {
            Column {
              Text("Eye Styles 👀", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SleekTextMuted)
              Spacer(modifier = Modifier.height(6.dp))
              LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(CustomizationRegistry.eyeStyles) { eye ->
                  val isSelected = previewPetState.eyeStyle == eye.id
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(16.dp))
                      .background(if (isSelected) SleekPrimary else Color(0xFFF8FAFC))
                      .border(1.dp, if (isSelected) SleekPrimary else Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                      .clickable {
                        previewPetState = previewPetState.copy(eyeStyle = eye.id)
                        onUpdateAppearance("eyestyle", eye.id)
                      }
                      .padding(horizontal = 12.dp, vertical = 8.dp)
                  ) {
                    Text(
                      text = "${eye.icon} ${eye.displayName}",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isSelected) Color.White else SleekTextDark
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              Text("Eye Colors 🟣", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SleekTextMuted)
              Spacer(modifier = Modifier.height(6.dp))
              LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(CustomizationRegistry.eyeColors) { eyeColorOpt ->
                  val isSelected = previewPetState.eyeColor == eyeColorOpt.id
                  Box(
                    modifier = Modifier
                      .size(44.dp)
                      .clip(CircleShape)
                      .background(Color(eyeColorOpt.colorHex))
                      .border(
                        if (isSelected) 3.dp else 1.dp,
                        if (isSelected) SleekPrimary else Color.White,
                        CircleShape
                      )
                      .clickable {
                        previewPetState = previewPetState.copy(eyeColor = eyeColorOpt.id)
                        onUpdateAppearance("eyecolor", eyeColorOpt.id)
                      },
                    contentAlignment = Alignment.Center
                  ) {
                    if (isSelected) {
                      Icon(Icons.Default.Check, contentDescription = "Selected", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              Text("Mouth Expressions 😊", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SleekTextMuted)
              Spacer(modifier = Modifier.height(6.dp))
              LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(CustomizationRegistry.mouthStyles) { mouth ->
                  val isSelected = previewPetState.mouthStyle == mouth.id
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(16.dp))
                      .background(if (isSelected) SleekPrimary else Color(0xFFF8FAFC))
                      .border(1.dp, if (isSelected) SleekPrimary else Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                      .clickable {
                        previewPetState = previewPetState.copy(mouthStyle = mouth.id)
                        onUpdateAppearance("mouth", mouth.id)
                      }
                      .padding(horizontal = 12.dp, vertical = 8.dp)
                  ) {
                    Text(
                      text = "${mouth.icon} ${mouth.displayName}",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isSelected) Color.White else SleekTextDark
                    )
                  }
                }
              }
            }
          }

          CustomizationTabCategory.HEADWEAR,
          CustomizationTabCategory.CLOTHING,
          CustomizationTabCategory.ACCESSORIES -> {
            val targetSlots = when (selectedTab) {
              CustomizationTabCategory.HEADWEAR -> listOf(CosmeticSlot.HAT, CosmeticSlot.HAIR, CosmeticSlot.GLASSES, CosmeticSlot.FACE)
              CustomizationTabCategory.CLOTHING -> listOf(CosmeticSlot.SHIRT, CosmeticSlot.JACKET, CosmeticSlot.PANTS, CosmeticSlot.SHOES)
              else -> listOf(CosmeticSlot.NECK, CosmeticSlot.BACK, CosmeticSlot.HAND, CosmeticSlot.TAIL)
            }

            val matchingItems = masterItems.filter { item ->
              val slot = CustomizationRegistry.getSlotForItem(item)
              slot != null && slot in targetSlots
            }

            if (matchingItems.isEmpty()) {
              Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No cosmetics unlocked in this category yet! Check Shop 🛒", color = SleekTextMuted, fontSize = 13.sp)
              }
            } else {
              LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
              ) {
                items(matchingItems) { item ->
                  val slot = CustomizationRegistry.getSlotForItem(item) ?: CosmeticSlot.HAT
                  val isCurrentlyEquipped = when (slot) {
                    CosmeticSlot.HAT -> previewPetState.equippedHat == item.id
                    CosmeticSlot.HAIR -> previewPetState.equippedHair == item.id
                    CosmeticSlot.GLASSES -> previewPetState.equippedGlasses == item.id
                    CosmeticSlot.FACE -> previewPetState.equippedFace == item.id
                    CosmeticSlot.NECK -> previewPetState.equippedNeck == item.id
                    CosmeticSlot.SHIRT -> previewPetState.equippedShirt == item.id
                    CosmeticSlot.JACKET -> previewPetState.equippedJacket == item.id
                    CosmeticSlot.PANTS -> previewPetState.equippedPants == item.id
                    CosmeticSlot.SHOES -> previewPetState.equippedShoes == item.id
                    CosmeticSlot.TAIL -> previewPetState.equippedTail == item.id
                    CosmeticSlot.BACK -> previewPetState.equippedBack == item.id
                    CosmeticSlot.HAND -> previewPetState.equippedHand == item.id
                  }
                  val isOwned = item.count > 0 || isCurrentlyEquipped
                  val isLocked = userLevel < item.unlockLevel

                  Card(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(16.dp))
                      .clickable {
                        if (isCurrentlyEquipped) {
                          onUnequipSlot(slot)
                          previewPetState = updatePreviewSlot(previewPetState, slot, "none")
                        } else if (isOwned && !isLocked) {
                          onEquipSlot(slot, item.id)
                          previewPetState = updatePreviewSlot(previewPetState, slot, item.id)
                        }
                      },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                      containerColor = if (isCurrentlyEquipped) Color(0xFFECFDF5) else Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                      1.5.dp,
                      if (isCurrentlyEquipped) Color(0xFF10B981) else Color(0xFFE2E8F0)
                    )
                  ) {
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      Box(
                        modifier = Modifier
                          .size(44.dp)
                          .clip(CircleShape)
                          .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                      ) {
                        Text(text = item.icon, fontSize = 24.sp)
                      }

                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = item.displayName,
                          fontSize = 12.sp,
                          fontWeight = FontWeight.Bold,
                          color = SleekTextDark,
                          maxLines = 1
                        )
                        Text(
                          text = slot.displayName,
                          fontSize = 10.sp,
                          color = SleekTextMuted
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        if (isCurrentlyEquipped) {
                          Text("Equipped ✓", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                        } else if (isLocked) {
                          Text("Lvl ${item.unlockLevel} 🔒", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                        } else if (isOwned) {
                          Text("Owned 🎒", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SleekPrimary)
                        } else {
                          Text("✨ ${item.purchasePrice}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CoinAmber)
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Bottom Save & Confirm Button
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(CircleShape)
          .background(SleekPrimary)
          .clickable {
            onSaveCustomization(previewPetState)
            onDismiss()
          }
          .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "Save Customization Style ✨👑",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    }
  }
}

private fun updatePreviewSlot(pet: PetEntity, slot: CosmeticSlot, itemId: String): PetEntity {
  return when (slot) {
    CosmeticSlot.HAT -> pet.copy(equippedHat = itemId)
    CosmeticSlot.HAIR -> pet.copy(equippedHair = itemId)
    CosmeticSlot.GLASSES -> pet.copy(equippedGlasses = itemId)
    CosmeticSlot.FACE -> pet.copy(equippedFace = itemId)
    CosmeticSlot.NECK -> pet.copy(equippedNeck = itemId)
    CosmeticSlot.SHIRT -> pet.copy(equippedShirt = itemId)
    CosmeticSlot.JACKET -> pet.copy(equippedJacket = itemId)
    CosmeticSlot.PANTS -> pet.copy(equippedPants = itemId)
    CosmeticSlot.SHOES -> pet.copy(equippedShoes = itemId)
    CosmeticSlot.TAIL -> pet.copy(equippedTail = itemId)
    CosmeticSlot.BACK -> pet.copy(equippedBack = itemId)
    CosmeticSlot.HAND -> pet.copy(equippedHand = itemId)
  }
}
