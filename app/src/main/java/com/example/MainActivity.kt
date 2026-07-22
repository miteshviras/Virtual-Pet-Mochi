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

  val isDecorationMode by viewModel.isDecorationMode.collectAsState()
  val placedFurnitureMap by viewModel.placedFurnitureMap.collectAsState()
  val roomWallpapers by viewModel.roomWallpapers.collectAsState()
  val roomFloors by viewModel.roomFloors.collectAsState()
  val selectedPlacedFurniture by viewModel.selectedPlacedFurniture.collectAsState()

  val highScoresMap by viewModel.highScoresMap.collectAsState()
  val activeActivity by viewModel.activeActivity.collectAsState()
  val gameSession by viewModel.gameSession.collectAsState()

  // Phase 9 States
  val achievementsMap by viewModel.achievementsMap.collectAsState()
  val dailyQuests by viewModel.dailyQuests.collectAsState()
  val unlockedCollections by viewModel.unlockedCollections.collectAsState()
  val playerMetaStats by viewModel.playerMetaStats.collectAsState()
  val levelUpDialogState by viewModel.levelUpDialogState.collectAsState()
  val achievementBanner by viewModel.achievementUnlockedBanner.collectAsState()

  var showShopSheet by remember { mutableStateOf(false) }
  var showGameSheet by remember { mutableStateOf(false) }
  var showCustomizationSheet by remember { mutableStateOf(false) }
  var showProfileSheet by remember { mutableStateOf(false) }
  var showAchievementsSheet by remember { mutableStateOf(false) }
  var showDailyTasksSheet by remember { mutableStateOf(false) }
  var showCollectionsSheet by remember { mutableStateOf(false) }

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
      // 1. Achievement Unlocked Banner overlay
      com.example.ui.components.AchievementUnlockedBanner(
        achievement = achievementBanner,
        onDismiss = { viewModel.dismissAchievementBanner() }
      )

      // Particle Overlay for Celebrations
      com.example.ui.components.ParticleEffectOverlay(
        triggerKey = levelUpDialogState ?: achievementBanner?.id,
        type = if (levelUpDialogState != null) com.example.ui.components.ParticleType.CONFETTI else com.example.ui.components.ParticleType.SPARKLES,
        particleCount = 25
      )

      // 2. Top Status Bar
      SleekTopBar(
        level = petState.level,
        xp = petState.xp,
        maxXp = petState.maxXp,
        coins = petState.coins,
        onOpenShop = { showShopSheet = true },
        onOpenDailyRewards = { viewModel.setShowDailyRewardDialog(true) },
        onOpenCustomization = { showCustomizationSheet = true },
        onOpenRoomDecoration = { viewModel.toggleDecorationMode(true) },
        onOpenPlayerProfile = { showProfileSheet = true },
        onOpenAchievements = { showAchievementsSheet = true },
        onOpenDailyTasks = { showDailyTasksSheet = true },
        onOpenCollections = { showCollectionsSheet = true }
      )

      // 2. Needs Dashboard
      SleekNeedsDashboard(pet = petState, mood = currentMood)


      Spacer(modifier = Modifier.height(12.dp))

      // 3. Hero Room Viewport with Camera transitions, Interactive Props, Placed Furniture & Floating Stat Popups
      RoomViewport(
        pet = petState,
        currentRoom = currentRoom,
        emotion = derivedEmotion,
        notificationMsg = notificationMsg,
        popups = floatingPopups,
        isDecorationMode = isDecorationMode,
        placedFurnitureList = placedFurnitureMap[currentRoom] ?: emptyList(),
        selectedPlacedFurniture = selectedPlacedFurniture,
        onSelectPlacedFurniture = { f -> viewModel.selectPlacedFurniture(f) },
        onMovePlacedFurniture = { id, x, y -> viewModel.moveFurniture(currentRoom, id, x, y) },
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
    if (isDecorationMode) {
      com.example.ui.components.RoomDecorationSheet(
        currentRoom = currentRoom,
        masterInventory = masterInventory,
        currentWallpaperId = roomWallpapers[currentRoom] ?: "wp_starry_night",
        currentFloorId = roomFloors[currentRoom] ?: "fl_oak_wood",
        selectedPlacedFurniture = selectedPlacedFurniture,
        onPlaceFurniture = { item -> viewModel.placeFurniture(currentRoom, item) },
        onChangeWallpaper = { wpId -> viewModel.changeWallpaper(currentRoom, wpId) },
        onChangeFloor = { flId -> viewModel.changeFloor(currentRoom, flId) },
        onRotateFurniture = { id -> viewModel.rotateFurniture(currentRoom, id) },
        onRemoveFurniture = { id -> viewModel.removeFurniture(currentRoom, id) },
        onResetRoom = { viewModel.resetRoomDecoration(currentRoom) },
        onCloseSheet = { viewModel.toggleDecorationMode(false) }
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
      com.example.ui.components.ActivityHubSheet(
        playerLevel = petState.level,
        highScoresMap = highScoresMap,
        activeActivity = activeActivity,
        gameSession = gameSession,
        onDismiss = {
          showGameSheet = false
          viewModel.exitGameSession()
        },
        onStartActivity = { actId, diff -> viewModel.startActivitySession(actId, diff) },
        onPauseGame = { viewModel.pauseGameSession() },
        onResumeGame = { viewModel.resumeGameSession() },
        onRestartGame = { viewModel.restartGameSession() },
        onExitGame = { viewModel.exitGameSession() },
        onUpdateScore = { p, success -> viewModel.updateGameScore(p, success) },
        onCompleteGame = { finalScore -> viewModel.completeGameSession(finalScore) }
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

    // Phase 9 Modals
    if (showProfileSheet) {
      com.example.ui.components.PlayerProfileSheet(
        petState = petState,
        metaStats = playerMetaStats,
        onDismiss = { showProfileSheet = false }
      )
    }

    if (showAchievementsSheet) {
      com.example.ui.components.AchievementsSheet(
        achievementsMap = achievementsMap,
        onClaimReward = { id -> viewModel.claimAchievementReward(id) },
        onDismiss = { showAchievementsSheet = false }
      )
    }

    if (showDailyTasksSheet) {
      com.example.ui.components.DailyTasksSheet(
        dailyQuests = dailyQuests,
        onClaimQuest = { id -> viewModel.claimDailyQuestReward(id) },
        onDismiss = { showDailyTasksSheet = false }
      )
    }

    if (showCollectionsSheet) {
      com.example.ui.components.CollectionsSheet(
        unlockedCollections = unlockedCollections,
        masterInventory = masterInventory,
        highScoresMap = highScoresMap,
        onDismiss = { showCollectionsSheet = false }
      )
    }

    levelUpDialogState?.let { lvl ->
      com.example.ui.components.LevelUpCelebrationDialog(
        newLevel = lvl,
        onDismiss = { viewModel.dismissLevelUpDialog() }
      )
    }
  }
}
