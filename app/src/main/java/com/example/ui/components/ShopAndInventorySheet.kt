package com.example.ui.components

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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.InventorySortOption
import com.example.data.ItemMainCategory
import com.example.data.ItemRarity
import com.example.data.MasterItem
import com.example.ui.theme.CoinAmber
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ShopAndInventorySheet(
  coins: Int,
  userLevel: Int,
  masterItems: List<MasterItem>,
  equippedHat: String,
  transactions: List<com.example.data.EconomyTransaction> = emptyList(),
  selectedCategory: ItemMainCategory,
  searchQuery: String,
  selectedSort: InventorySortOption,
  onDismiss: () -> Unit,
  onCategorySelected: (ItemMainCategory) -> Unit,
  onSearchQueryChanged: (String) -> Unit,
  onSortOptionSelected: (InventorySortOption) -> Unit,
  onBuyItem: (MasterItem, Int) -> Unit,
  onBuyBundle: (com.example.data.ShopBundle) -> Unit = {},
  onUseItem: (MasterItem) -> Unit,
  onSellItem: (MasterItem, Int) -> Unit,
  onEquipHat: (String) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Store, 1: Backpack, 2: Bundles, 3: History
  var inspectedItem by remember { mutableStateOf<MasterItem?>(null) }
  var quantitySelection by remember { mutableIntStateOf(1) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color.White
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      // Header Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = when (selectedTab) {
              0 -> "Mochi Emporium 🛍️"
              1 -> "Your Backpack 🎒"
              2 -> "Special Value Bundles 🎁"
              else -> "Transaction Ledger 📜"
            },
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = SleekTextDark
          )
          Text(
            text = "Coins: ✨ $coins  |  Level: ⭐ $userLevel",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = CoinAmber
          )
        }

        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("close_sheet_button")
        ) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SleekTextDark)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Tab selector (4 tabs)
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color(0xFFF1F5F9),
        contentColor = SleekPrimary,
        modifier = Modifier.clip(RoundedCornerShape(16.dp))
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = {
            com.example.util.SoundManager.playCategorySwitchSound()
            selectedTab = 0
          },
          text = { Text("Shop 🛒", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = {
            com.example.util.SoundManager.playCategorySwitchSound()
            selectedTab = 1
          },
          text = { Text("Items 🎒", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = selectedTab == 2,
          onClick = {
            com.example.util.SoundManager.playCategorySwitchSound()
            selectedTab = 2
          },
          text = { Text("Bundles 🎁", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = selectedTab == 3,
          onClick = {
            com.example.util.SoundManager.playCategorySwitchSound()
            selectedTab = 3
          },
          text = { Text("Ledger 📜", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      if (selectedTab == 0 || selectedTab == 1) {
        // Search bar (Full width on top row)
        OutlinedTextField(
          value = searchQuery,
          onValueChange = onSearchQueryChanged,
          placeholder = { Text("Search items...", fontSize = 13.sp) },
          leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(18.dp)) },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFF8FAFC),
            focusedContainerColor = Color.White,
            unfocusedBorderColor = Color(0xFFE2E8F0),
            focusedBorderColor = SleekPrimary
          ),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Sort selector chips (Row below search input)
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(InventorySortOption.values()) { option ->
            val isSelected = selectedSort == option
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) SleekPrimary else Color(0xFFF1F5F9))
                .clickable { onSortOptionSelected(option) }
                .padding(horizontal = 10.dp, vertical = 7.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "${option.icon} ${option.label}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else SleekTextMuted
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Category filter horizontal chips
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(ItemMainCategory.values()) { cat ->
            val isCatSelected = selectedCategory == cat
            Box(
              modifier = Modifier
                .clip(CircleShape)
                .background(if (isCatSelected) SleekPrimary else Color(0xFFF1F5F9))
                .border(
                  1.dp,
                  if (isCatSelected) SleekPrimary else Color(0xFFE2E8F0),
                  CircleShape
                )
                .clickable { onCategorySelected(cat) }
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = "${cat.icon} ${cat.displayName}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isCatSelected) Color.White else SleekTextDark
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Displayed items grid
        val rawItems = if (selectedTab == 0) masterItems else masterItems.filter { it.count > 0 || it.isEquippable }
        val filteredList = rawItems.filter { item ->
          (selectedCategory == ItemMainCategory.ALL || item.mainCategory == selectedCategory) &&
              (searchQuery.isBlank() ||
                  item.displayName.contains(searchQuery, ignoreCase = true) ||
                  item.description.contains(searchQuery, ignoreCase = true) ||
                  item.tags.any { it.contains(searchQuery, ignoreCase = true) })
        }.let { list ->
          when (selectedSort) {
            InventorySortOption.BY_NAME -> list.sortedBy { it.displayName }
            InventorySortOption.BY_RARITY -> list.sortedByDescending { it.rarity.ordinal }
            InventorySortOption.BY_PRICE -> list.sortedBy { it.purchasePrice }
            InventorySortOption.BY_QUANTITY -> list.sortedByDescending { it.count }
          }
        }

        if (filteredList.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(300.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (selectedTab == 0) "No items found in store! 🛒" else "No matching items in your backpack! 🎒",
              color = SleekTextMuted,
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium
            )
          }
        } else {
          LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.height(350.dp)
          ) {
            items(filteredList) { item ->
              val isLocked = userLevel < item.unlockLevel
              val isEquipped = item.id == equippedHat

              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .shadow(2.dp, RoundedCornerShape(18.dp))
                  .clip(RoundedCornerShape(18.dp))
                  .clickable {
                    quantitySelection = 1
                    inspectedItem = item
                  },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(
                  1.5.dp,
                  Color(item.rarity.borderHex)
                )
              ) {
                Column(
                  modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(item.rarity.colorHex))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                      Text(
                        text = item.rarity.label,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                      )
                    }

                    if (isLocked) {
                      Text(text = "🔒 Lvl ${item.unlockLevel}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                    } else if (item.count > 0) {
                      Text(text = "x${item.count}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SleekPrimary)
                    }
                  }

                  Spacer(modifier = Modifier.height(6.dp))

                  Box(
                    modifier = Modifier
                      .size(48.dp)
                      .clip(CircleShape)
                      .background(Color(0xFFF8FAFC)),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(text = item.icon, fontSize = 28.sp)
                  }

                  Spacer(modifier = Modifier.height(4.dp))

                  Text(
                    text = item.displayName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )

                  Text(
                    text = item.subcategory,
                    fontSize = 10.sp,
                    color = SleekTextMuted
                  )

                  Spacer(modifier = Modifier.height(6.dp))

                  if (selectedTab == 0) {
                    Box(
                      modifier = Modifier
                        .fillMaxWidth()
                        .testTag("buy_item_${item.id}")
                        .clip(CircleShape)
                        .background(if (coins >= item.purchasePrice && !isLocked) SleekPrimary else Color(0xFFCBD5E1))
                        .clickable { if (coins >= item.purchasePrice && !isLocked) onBuyItem(item, 1) }
                        .padding(vertical = 6.dp),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = if (isLocked) "Lvl ${item.unlockLevel} 🔒" else "✨ ${item.purchasePrice}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                      )
                    }
                  } else {
                    if (item.isEquippable) {
                      Box(
                        modifier = Modifier
                          .fillMaxWidth()
                          .testTag("equip_item_${item.id}")
                          .clip(CircleShape)
                          .background(if (isEquipped) Color(0xFF10B981) else SleekPrimary)
                          .clickable { onEquipHat(if (isEquipped) "none" else item.id) }
                          .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                      ) {
                        Text(
                          text = if (isEquipped) "Equipped ✓" else "Equip 👑",
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color.White
                        )
                      }
                    } else if (item.count > 0) {
                      Box(
                        modifier = Modifier
                          .fillMaxWidth()
                          .testTag("use_item_${item.id}")
                          .clip(CircleShape)
                          .background(SleekPrimary)
                          .clickable { onUseItem(item) }
                          .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                      ) {
                        Text(
                          text = "Use ✨",
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color.White
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }
      } else if (selectedTab == 2) {
        // Bundles tab
        Column(
          modifier = Modifier.height(380.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          com.example.data.ShopBundleRegistry.availableBundles.forEach { bundle ->
            Card(
              shape = RoundedCornerShape(20.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
              border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF8B5CF6)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .padding(14.dp)
                  .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEDE9FE)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(text = bundle.icon, fontSize = 32.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = bundle.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SleekTextDark)
                    if (bundle.isLimited) {
                      Spacer(modifier = Modifier.width(6.dp))
                      Box(
                        modifier = Modifier
                          .clip(CircleShape)
                          .background(Color(0xFFEF4444))
                          .padding(horizontal = 6.dp, vertical = 2.dp)
                      ) {
                        Text(text = "LIMITED 🔥", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                      }
                    }
                  }

                  Text(text = bundle.description, fontSize = 11.sp, color = SleekTextMuted, maxLines = 2)

                  Spacer(modifier = Modifier.height(4.dp))

                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = "✨${bundle.bundlePrice}",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold,
                      color = CoinAmber
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "(${bundle.originalPrice} coins value)",
                      fontSize = 10.sp,
                      color = SleekTextMuted
                    )
                  }
                }

                Box(
                  modifier = Modifier
                    .clip(CircleShape)
                    .background(if (coins >= bundle.bundlePrice) SleekPrimary else Color(0xFFCBD5E1))
                    .clickable { if (coins >= bundle.bundlePrice) onBuyBundle(bundle) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                  Text(text = "Claim 🎁", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
              }
            }
          }
        }
      } else {
        // Transactions Ledger tab
        if (transactions.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(350.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(text = "No recent transactions yet! 📜", color = SleekTextMuted, fontSize = 14.sp)
          }
        } else {
          Column(
            modifier = Modifier.height(350.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            transactions.take(20).forEach { tx ->
              Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Text(text = tx.type.icon, fontSize = 20.sp)
                    Column {
                      Text(text = tx.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SleekTextDark)
                      Text(text = tx.type.label, fontSize = 10.sp, color = SleekTextMuted)
                    }
                  }

                  Column(horizontalAlignment = Alignment.End) {
                    Text(
                      text = if (tx.amount >= 0) "+${tx.amount}🪙" else "${tx.amount}🪙",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (tx.amount >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                    Text(text = "Bal: ${tx.balanceAfter}", fontSize = 10.sp, color = SleekTextMuted)
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Item Inspection Popup Dialog with Quantity Selector
  inspectedItem?.let { item ->
    Dialog(onDismissRequest = { inspectedItem = null }) {
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .clip(CircleShape)
                .background(Color(item.rarity.colorHex))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(text = item.rarity.label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            IconButton(onClick = { inspectedItem = null }) {
              Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SleekTextDark)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Box(
            modifier = Modifier
              .size(72.dp)
              .clip(CircleShape)
              .background(Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
          ) {
            Text(text = item.icon, fontSize = 42.sp)
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = item.displayName,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SleekTextDark
          )

          Text(
            text = "${item.mainCategory.displayName} • ${item.subcategory}",
            fontSize = 12.sp,
            color = SleekTextMuted
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = item.description,
            fontSize = 13.sp,
            color = SleekTextDark,
            modifier = Modifier.padding(horizontal = 8.dp)
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Quantity Selector Pills
          Text(text = "Quantity: $quantitySelection", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SleekTextDark)
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            listOf(1, 3, 5, 10).forEach { qty ->
              Box(
                modifier = Modifier
                  .clip(CircleShape)
                  .background(if (quantitySelection == qty) SleekPrimary else Color(0xFFF1F5F9))
                  .clickable { quantitySelection = qty }
                  .padding(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Text(
                  text = "${qty}x",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (quantitySelection == qty) Color.White else SleekTextDark
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Action Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            if (item.count > 0 && item.isUsable) {
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(CircleShape)
                  .background(SleekPrimary)
                  .clickable {
                    onUseItem(item)
                    inspectedItem = null
                  }
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "Use ✨", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }
            }

            if (item.count > 0) {
              val sellGain = item.sellPrice * quantitySelection
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(CircleShape)
                  .background(Color(0xFFEF4444))
                  .clickable {
                    onSellItem(item, quantitySelection)
                    inspectedItem = null
                  }
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "Sell (+$sellGain🪙)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }
            }

            val buyCost = item.purchasePrice * quantitySelection
            if (coins >= buyCost && userLevel >= item.unlockLevel) {
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(CircleShape)
                  .background(CoinAmber)
                  .clickable {
                    onBuyItem(item, quantitySelection)
                    inspectedItem = null
                  }
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "Buy ✨$buyCost", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
          }
        }
      }
    }
  }
}
