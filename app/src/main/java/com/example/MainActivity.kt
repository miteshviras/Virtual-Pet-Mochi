package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.MochiViewModel
import com.example.data.RoomType
import com.example.ui.components.CareActionsPanel
import com.example.ui.components.CustomizationSheet
import com.example.ui.components.DailyRewardDialog
import com.example.ui.components.MiniGameHubSheet
import com.example.ui.components.MochiPetView
import com.example.ui.components.RoomViewport
import com.example.ui.components.ShopAndInventorySheet
import com.example.ui.components.SleekNavBar
import com.example.ui.components.SleekNeedsDashboard
import com.example.ui.components.SleekQuickActions
import com.example.ui.components.SleekTopBar
import com.example.ui.theme.MochiTheme
import com.example.ui.theme.SleekBg

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MochiTheme {
        MochiAppScreen()
      }
    }
  }
}

@Composable
fun MochiAppScreen(
  viewModel: MochiViewModel = viewModel()
) {
  val petState by viewModel.petState.collectAsState()
  val currentRoom by viewModel.currentRoom.collectAsState()
  val currentMood by viewModel.currentMood.collectAsState()
  val inventory by viewModel.inventory.collectAsState()
  val masterInventory by viewModel.masterInventory.collectAsState()
  val selectedCategory by viewModel.inventoryCategory.collectAsState()
  val searchQuery by viewModel.inventorySearch.collectAsState()
  val selectedSort by viewModel.inventorySort.collectAsState()
  val notificationMsg by viewModel.notificationMessage.collectAsState()
  val activeGameId by viewModel.activeMiniGame.collectAsState()
  val cooldowns by viewModel.actionCooldowns.collectAsState()
  val floatingPopups by viewModel.floatingPopups.collectAsState()
  val transactions by viewModel.transactions.collectAsState()
  val dailyStreakDay by viewModel.dailyStreakDay.collectAsState()
  val hasClaimedDailyReward by viewModel.hasClaimedDailyReward.collectAsState()
  val showDailyRewardDialog by viewModel.showDailyRewardDialog.collectAsState()

  var showShopSheet by remember { mutableStateOf(false) }
  var showGameSheet by remember { mutableStateOf(false) }
  var showCustomizationSheet by remember { mutableStateOf(false) }

  val derivedEmotion = viewModel.getDerivedEmotion()

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = SleekBg,
    bottomBar = {
      SleekNavBar(
        currentRoom = currentRoom,
        onRoomSelect = { room -> viewModel.changeRoom(room) }
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .statusBarsPadding()
        .background(SleekBg)
    ) {
      // 1. Top Status Bar (Level, XP, Coins, Dress-Up, Daily Bonus)
      SleekTopBar(
        level = petState.level,
        xp = petState.xp,
        maxXp = petState.maxXp,
        coins = petState.coins,
        onOpenShop = { showShopSheet = true },
        onOpenDailyRewards = { viewModel.setShowDailyRewardDialog(true) },
        onOpenCustomization = { showCustomizationSheet = true }
      )

      // 2. Needs Dashboard
      SleekNeedsDashboard(pet = petState, mood = currentMood)


      Spacer(modifier = Modifier.height(12.dp))

      // 3. Hero Room Viewport with Camera transitions, Interactive Props & Floating Stat Popups
      RoomViewport(
        pet = petState,
        currentRoom = currentRoom,
        emotion = derivedEmotion,
        notificationMsg = notificationMsg,
        popups = floatingPopups,
        onPetClick = { viewModel.playWithPet() },
        onPropInteract = { prop -> viewModel.interactWithProp(prop) },
        onRoomSwipe = { newRoom -> viewModel.changeRoom(newRoom) },
        modifier = Modifier.weight(1f)
      )

      Spacer(modifier = Modifier.height(6.dp))

      // 4. Care Actions Panel (Feed, Water, Bath, Brush, Sleep, Play, Medicine, Cuddle, Praise)
      CareActionsPanel(
        currentRoom = currentRoom,
        cooldowns = cooldowns,
        onExecuteAction = { action -> viewModel.executeCareAction(action) }
      )

      Spacer(modifier = Modifier.height(6.dp))

      // 5. Sleek Quick Actions Bar (Shop, Mini-Games)
      SleekQuickActions(
        currentRoom = currentRoom,
        onLeftAction = {
          when (currentRoom) {
            RoomType.KITCHEN -> {
              val food = inventory.firstOrNull { it.count > 0 }
              if (food != null) viewModel.feedPet(food) else showShopSheet = true
            }
            RoomType.BATH -> viewModel.bathePet()
            RoomType.PLAY -> showShopSheet = true
            RoomType.SLEEP -> viewModel.toggleSleep()
            RoomType.GARDEN -> viewModel.addXpAndCoins(10, 5)
          }
        },
        onCenterAction = {
          when (currentRoom) {
            RoomType.PLAY -> showGameSheet = true
            RoomType.KITCHEN -> {
              val food = inventory.firstOrNull { it.count > 0 }
              if (food != null) viewModel.feedPet(food) else showShopSheet = true
            }
            RoomType.BATH -> viewModel.bathePet()
            RoomType.SLEEP -> viewModel.toggleSleep()
            RoomType.GARDEN -> showGameSheet = true
          }
        },
        onRightAction = {
          showShopSheet = true
        }
      )
    }

    // Modal Bottom Sheets
    if (showShopSheet) {
      ShopAndInventorySheet(
        coins = petState.coins,
        userLevel = petState.level,
        masterItems = masterInventory,
        equippedHat = petState.equippedHat,
        transactions = transactions,
        selectedCategory = selectedCategory,
        searchQuery = searchQuery,
        selectedSort = selectedSort,
        onDismiss = { showShopSheet = false },
        onCategorySelected = { cat -> viewModel.setInventoryCategory(cat) },
        onSearchQueryChanged = { q -> viewModel.setInventorySearch(q) },
        onSortOptionSelected = { sort -> viewModel.setInventorySort(sort) },
        onBuyItem = { item, qty -> viewModel.buyMasterItem(item, qty) },
        onBuyBundle = { bundle -> viewModel.buyBundle(bundle) },
        onUseItem = { item -> viewModel.useMasterItem(item) },
        onSellItem = { item, qty -> viewModel.sellMasterItem(item, qty) },
        onEquipHat = { hatId -> viewModel.equipHat(hatId) }
      )
    }

    if (showDailyRewardDialog) {
      com.example.ui.components.DailyRewardDialog(
        currentStreakDay = dailyStreakDay,
        hasClaimedToday = hasClaimedDailyReward,
        onDismiss = { viewModel.setShowDailyRewardDialog(false) },
        onClaimReward = { viewModel.claimDailyReward() }
      )
    }

    if (showGameSheet) {
      MiniGameHubSheet(
        activeGameId = activeGameId,
        onDismiss = {
          showGameSheet = false
          viewModel.closeMiniGame()
        },
        onOpenGame = { gameId -> viewModel.openMiniGame(gameId) },
        onGameWin = { coins, xp ->
          viewModel.addXpAndCoins(xp, coins)
          viewModel.closeMiniGame()
          showGameSheet = false
        }
      )
    }

    if (showCustomizationSheet) {
      CustomizationSheet(
        pet = petState,
        masterItems = masterInventory,
        userLevel = petState.level,
        userCoins = petState.coins,
        onDismiss = { showCustomizationSheet = false },
        onEquipSlot = { slot, itemId -> viewModel.equipCosmeticSlot(slot, itemId) },
        onUnequipSlot = { slot -> viewModel.unequipCosmeticSlot(slot) },
        onUpdateAppearance = { category, optionId -> viewModel.updateAppearanceOption(category, optionId) },
        onResetAppearance = { viewModel.resetCustomizationAppearance() },
        onSaveCustomization = { newPet -> viewModel.saveCustomizationSettings(newPet) }
      )
    }
  }
}
