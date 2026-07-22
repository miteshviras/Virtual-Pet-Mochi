package com.example.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MochiViewModel(application: Application) : AndroidViewModel(application) {

  private val db = PetDatabase.getDatabase(application)
  private val dao = db.petDao()

  private val _petState = MutableStateFlow(PetEntity())
  val petState: StateFlow<PetEntity> = _petState.asStateFlow()

  private val _currentRoom = MutableStateFlow(RoomType.PLAY)
  val currentRoom: StateFlow<RoomType> = _currentRoom.asStateFlow()

  private val _inventory = MutableStateFlow<List<InventoryItem>>(emptyList())
  val inventory: StateFlow<List<InventoryItem>> = _inventory.asStateFlow()

  private val _masterInventory = MutableStateFlow<List<MasterItem>>(emptyList())
  val masterInventory: StateFlow<List<MasterItem>> = _masterInventory.asStateFlow()

  private val _inventoryCategory = MutableStateFlow(ItemMainCategory.ALL)
  val inventoryCategory: StateFlow<ItemMainCategory> = _inventoryCategory.asStateFlow()

  private val _inventorySearch = MutableStateFlow("")
  val inventorySearch: StateFlow<String> = _inventorySearch.asStateFlow()

  private val _inventorySort = MutableStateFlow(InventorySortOption.BY_NAME)
  val inventorySort: StateFlow<InventorySortOption> = _inventorySort.asStateFlow()

  private val _transactions = MutableStateFlow<List<EconomyTransaction>>(emptyList())
  val transactions: StateFlow<List<EconomyTransaction>> = _transactions.asStateFlow()

  private val _dailyStreakDay = MutableStateFlow(1)
  val dailyStreakDay: StateFlow<Int> = _dailyStreakDay.asStateFlow()

  private val _hasClaimedDailyReward = MutableStateFlow(false)
  val hasClaimedDailyReward: StateFlow<Boolean> = _hasClaimedDailyReward.asStateFlow()

  private val _showDailyRewardDialog = MutableStateFlow(false)
  val showDailyRewardDialog: StateFlow<Boolean> = _showDailyRewardDialog.asStateFlow()

  private val _showGuideDialog = MutableStateFlow(false)
  val showGuideDialog: StateFlow<Boolean> = _showGuideDialog.asStateFlow()

  private val _notificationMessage = MutableStateFlow<String?>("Time for a bath! 🧼")
  val notificationMessage: StateFlow<String?> = _notificationMessage.asStateFlow()

  private val _isSleeping = MutableStateFlow(false)
  val isSleeping: StateFlow<Boolean> = _isSleeping.asStateFlow()

  private val _activeMiniGame = MutableStateFlow<String?>(null)
  val activeMiniGame: StateFlow<String?> = _activeMiniGame.asStateFlow()

  private val _currentMood = MutableStateFlow(PetMoodState.HAPPY)
  val currentMood: StateFlow<PetMoodState> = _currentMood.asStateFlow()

  private val _actionCooldowns = MutableStateFlow<Map<String, Long>>(emptyMap())
  val actionCooldowns: StateFlow<Map<String, Long>> = _actionCooldowns.asStateFlow()

  private val _floatingPopups = MutableStateFlow<List<FloatingStatPopup>>(emptyList())
  val floatingPopups: StateFlow<List<FloatingStatPopup>> = _floatingPopups.asStateFlow()

  private val _isDecorationMode = MutableStateFlow(false)
  val isDecorationMode: StateFlow<Boolean> = _isDecorationMode.asStateFlow()

  private val _selectedFurnitureToPlace = MutableStateFlow<MasterItem?>(null)
  val selectedFurnitureToPlace: StateFlow<MasterItem?> = _selectedFurnitureToPlace.asStateFlow()

  private val _selectedPlacedFurniture = MutableStateFlow<PlacedFurniture?>(null)
  val selectedPlacedFurniture: StateFlow<PlacedFurniture?> = _selectedPlacedFurniture.asStateFlow()

  private val _placedFurnitureMap = MutableStateFlow<Map<RoomType, List<PlacedFurniture>>>(emptyMap())
  val placedFurnitureMap: StateFlow<Map<RoomType, List<PlacedFurniture>>> = _placedFurnitureMap.asStateFlow()

  private val _roomWallpapers = MutableStateFlow<Map<RoomType, String>>(emptyMap())
  val roomWallpapers: StateFlow<Map<RoomType, String>> = _roomWallpapers.asStateFlow()

  private val _roomFloors = MutableStateFlow<Map<RoomType, String>>(emptyMap())
  val roomFloors: StateFlow<Map<RoomType, String>> = _roomFloors.asStateFlow()

  private val _highScoresMap = MutableStateFlow<Map<String, Int>>(emptyMap())
  val highScoresMap: StateFlow<Map<String, Int>> = _highScoresMap.asStateFlow()

  private val _activeActivity = MutableStateFlow<ActivityDefinition?>(null)
  val activeActivity: StateFlow<ActivityDefinition?> = _activeActivity.asStateFlow()

  private val _gameSession = MutableStateFlow<GameSession?>(null)
  val gameSession: StateFlow<GameSession?> = _gameSession.asStateFlow()

  // --- PHASE 9: PROGRESSION & META STATEFLOWS ---
  private val _achievementsMap = MutableStateFlow<Map<String, AchievementProgress>>(emptyMap())
  val achievementsMap: StateFlow<Map<String, AchievementProgress>> = _achievementsMap.asStateFlow()

  private val _dailyQuests = MutableStateFlow<List<DailyQuest>>(emptyList())
  val dailyQuests: StateFlow<List<DailyQuest>> = _dailyQuests.asStateFlow()

  private val _unlockedCollections = MutableStateFlow<Set<String>>(emptySet())
  val unlockedCollections: StateFlow<Set<String>> = _unlockedCollections.asStateFlow()

  private val _playerMetaStats = MutableStateFlow(PlayerStatsMeta())
  val playerMetaStats: StateFlow<PlayerStatsMeta> = _playerMetaStats.asStateFlow()

  private val _levelUpDialogState = MutableStateFlow<Int?>(null)
  val levelUpDialogState: StateFlow<Int?> = _levelUpDialogState.asStateFlow()

  private val _achievementUnlockedBanner = MutableStateFlow<AchievementDefinition?>(null)
  val achievementUnlockedBanner: StateFlow<AchievementDefinition?> = _achievementUnlockedBanner.asStateFlow()

  init {
    loadOrCreatePet()
    initializeInventory()
    startStatsDecayLoop()
  }

  private fun loadOrCreatePet() {
    viewModelScope.launch {
      dao.getPetStats().collect { pet ->
        if (pet != null) {
          val processedPet = applyOfflineSimulation(pet)
          _petState.value = processedPet
          _currentMood.value = calculateMood(processedPet, _isSleeping.value)
          try {
            _currentRoom.value = RoomType.valueOf(processedPet.lastVisitedRoom)
          } catch (e: Exception) {
            _currentRoom.value = RoomType.PLAY
          }
          _roomWallpapers.value = mapOf(
            RoomType.SLEEP to processedPet.bedroomWallpaper,
            RoomType.KITCHEN to processedPet.kitchenWallpaper,
            RoomType.BATH to processedPet.bathroomWallpaper,
            RoomType.PLAY to processedPet.playroomWallpaper,
            RoomType.GARDEN to processedPet.gardenWallpaper
          )
          _roomFloors.value = mapOf(
            RoomType.SLEEP to processedPet.bedroomFloor,
            RoomType.KITCHEN to processedPet.kitchenFloor,
            RoomType.BATH to processedPet.bathroomFloor,
            RoomType.PLAY to processedPet.playroomFloor,
            RoomType.GARDEN to processedPet.gardenFloor
          )
          _placedFurnitureMap.value = deserializePlacedFurniture(processedPet.placedFurnitureJson)
          _highScoresMap.value = deserializeHighScores(processedPet.highScoresJson)
          _achievementsMap.value = deserializeAchievements(processedPet.achievementsJson)
          _dailyQuests.value = deserializeDailyQuests(processedPet.dailyQuestsJson, processedPet.lastDailyResetDate)
          _unlockedCollections.value = deserializeCollections(processedPet.unlockedCollectionsJson)
          _playerMetaStats.value = PlayerStatsMeta(
            lifetimeCoinsEarned = processedPet.lifetimeCoinsEarned,
            lifetimeCoinsSpent = processedPet.lifetimeCoinsSpent,
            totalFoodsFed = processedPet.totalFoodsFed,
            totalBathsGiven = processedPet.totalBathsGiven,
            totalCareActionsPerformed = processedPet.totalCareActionsPerformed,
            totalFurniturePlaced = processedPet.totalFurniturePlaced,
            totalCustomizationsEquipped = processedPet.totalCustomizationsEquipped,
            totalActivitiesPlayed = processedPet.totalActivitiesPlayed,
            totalMiniGameCoinsEarned = processedPet.totalMiniGameCoinsEarned
          )
          updateNotificationForPet(processedPet)
        } else {
          val defaultPet = PetEntity()
          dao.updatePetStats(defaultPet)
          _petState.value = defaultPet
          _currentMood.value = calculateMood(defaultPet, false)
          _roomWallpapers.value = mapOf(
            RoomType.SLEEP to defaultPet.bedroomWallpaper,
            RoomType.KITCHEN to defaultPet.kitchenWallpaper,
            RoomType.BATH to defaultPet.bathroomWallpaper,
            RoomType.PLAY to defaultPet.playroomWallpaper,
            RoomType.GARDEN to defaultPet.gardenWallpaper
          )
          _roomFloors.value = mapOf(
            RoomType.SLEEP to defaultPet.bedroomFloor,
            RoomType.KITCHEN to defaultPet.kitchenFloor,
            RoomType.BATH to defaultPet.bathroomFloor,
            RoomType.PLAY to defaultPet.playroomFloor,
            RoomType.GARDEN to defaultPet.gardenFloor
          )
          _placedFurnitureMap.value = deserializePlacedFurniture("")
        }
      }
    }
  }

  private fun applyOfflineSimulation(pet: PetEntity): PetEntity {
    val now = System.currentTimeMillis()
    val elapsedMillis = now - pet.lastUpdateTime
    if (elapsedMillis <= 30000L) return pet.copy(lastUpdateTime = now)

    val maxOfflineMillis = 12 * 60 * 60 * 1000L
    val simulatedMillis = elapsedMillis.coerceAtMost(maxOfflineMillis)
    val intervals = (simulatedMillis / 900000L).toInt().coerceAtLeast(1)

    var hunger = pet.hunger
    var clean = pet.cleanliness
    var energy = pet.energy
    var joy = pet.happiness
    var health = pet.health
    var friendship = pet.friendship

    repeat(intervals) {
      hunger = (hunger - 1.2f).coerceAtLeast(10f)
      clean = (clean - 1.0f).coerceAtLeast(10f)
      energy = (energy - 0.8f).coerceAtLeast(15f)
      joy = (joy - 0.8f).coerceAtLeast(15f)

      if (hunger < 20f || clean < 20f) {
        health = (health - 0.5f).coerceAtLeast(30f)
      } else {
        health = (health + 0.5f).coerceAtMost(100f)
      }
    }

    if (elapsedMillis > 24 * 60 * 60 * 1000L) {
      friendship = (friendship - 2f).coerceAtLeast(30f)
    }

    val simulatedPet = pet.copy(
      hunger = hunger,
      cleanliness = clean,
      energy = energy,
      happiness = joy,
      health = health,
      friendship = friendship,
      lastUpdateTime = now
    )

    val hoursOffline = (elapsedMillis / (1000 * 60 * 60)).toInt()
    if (hoursOffline >= 1) {
      showTemporaryNotification("Welcome back! Mochi missed you during the last $hoursOffline hrs! ✨")
    }

    viewModelScope.launch { dao.updatePetStats(simulatedPet) }
    return simulatedPet
  }

  fun calculateMood(pet: PetEntity, isSleeping: Boolean): PetMoodState {
    return when {
      pet.health < 30f -> PetMoodState.SICK
      pet.hunger < 25f -> PetMoodState.HUNGRY
      pet.cleanliness < 25f -> PetMoodState.DIRTY
      pet.energy < 25f || isSleeping -> PetMoodState.SLEEPY
      pet.happiness < 30f -> PetMoodState.LONELY
      pet.happiness > 85f && pet.energy > 50f -> PetMoodState.EXCITED
      pet.happiness > 60f -> PetMoodState.HAPPY
      else -> PetMoodState.CALM
    }
  }


  private fun initializeInventory() {
    val initialMasterList = ItemDatabaseRegistry.masterItemList.map { item ->
      when (item.id) {
        "item_apple" -> item.copy(count = 3)
        "item_milk" -> item.copy(count = 2)
        "item_soap" -> item.copy(count = 2)
        "item_potion" -> item.copy(count = 1)
        "toy_ball" -> item.copy(count = 1)
        "hat_party" -> item.copy(count = 1)
        else -> item
      }
    }
    _masterInventory.value = initialMasterList

    // Also populate legacy inventory list for backward compatibility
    _inventory.value = initialMasterList.map { it.toInventoryItem() }
  }

  fun setInventoryCategory(category: ItemMainCategory) {
    _inventoryCategory.value = category
  }

  fun setInventorySearch(query: String) {
    _inventorySearch.value = query
  }

  fun setInventorySort(sortOption: InventorySortOption) {
    _inventorySort.value = sortOption
  }

  fun buyMasterItem(item: MasterItem, quantity: Int = 1) {
    val current = _petState.value
    if (current.level < item.unlockLevel) {
      com.example.util.SoundManager.playErrorSound()
      showTemporaryNotification("Requires Level ${item.unlockLevel} to purchase! 🔒")
      return
    }

    val totalCost = item.purchasePrice * quantity
    if (current.coins >= totalCost) {
      val existing = _masterInventory.value.find { it.id == item.id }
      val currentCount = existing?.count ?: 0
      if (currentCount + quantity > item.stackLimit) {
        com.example.util.SoundManager.playErrorSound()
        showTemporaryNotification("Max stack capacity reached (${item.stackLimit})! 📦")
        return
      }

      val newCoins = current.coins - totalCost
      val updatedPet = current.copy(coins = newCoins)
      _petState.value = updatedPet
      viewModelScope.launch { dao.updatePetStats(updatedPet) }

      _masterInventory.value = _masterInventory.value.map {
        if (it.id == item.id) it.copy(count = it.count + quantity) else it
      }
      _inventory.value = _masterInventory.value.map { it.toInventoryItem() }

      val tx = EconomyTransaction(
        type = TransactionType.PURCHASE,
        amount = -totalCost,
        title = "Bought ${quantity}x ${item.displayName}",
        balanceAfter = newCoins
      )
      _transactions.value = listOf(tx) + _transactions.value

      com.example.util.SoundManager.playPurchaseSound()
      triggerFloatingPopup("Bought ${quantity}x ${item.displayName}! 🎉", 0xFF10B981)
      showTemporaryNotification("Bought ${quantity}x ${item.displayName}! ✨")
    } else {
      com.example.util.SoundManager.playErrorSound()
      showTemporaryNotification("Not enough coins! Need ✨$totalCost 🪙")
    }
  }

  fun buyBundle(bundle: ShopBundle) {
    val current = _petState.value
    if (current.coins < bundle.bundlePrice) {
      com.example.util.SoundManager.playErrorSound()
      showTemporaryNotification("Not enough coins for ${bundle.title}! Need ✨${bundle.bundlePrice} 🪙")
      return
    }

    val newCoins = current.coins - bundle.bundlePrice
    val updatedPet = current.copy(coins = newCoins)
    _petState.value = updatedPet
    viewModelScope.launch { dao.updatePetStats(updatedPet) }

    // Add all bundle items to inventory
    _masterInventory.value = _masterInventory.value.map { master ->
      val bundleItem = bundle.items.find { it.first == master.id }
      if (bundleItem != null) {
        master.copy(count = (master.count + bundleItem.second).coerceAtMost(master.stackLimit))
      } else {
        master
      }
    }
    _inventory.value = _masterInventory.value.map { it.toInventoryItem() }

    val tx = EconomyTransaction(
      type = TransactionType.PURCHASE,
      amount = -bundle.bundlePrice,
      title = "Bundle: ${bundle.title}",
      balanceAfter = newCoins
    )
    _transactions.value = listOf(tx) + _transactions.value

    com.example.util.SoundManager.playRewardSound()
    triggerFloatingPopup("Unlocked ${bundle.title}! 🎁", 0xFF8B5CF6)
    showTemporaryNotification("Successfully claimed ${bundle.title}! 🎉✨")
  }

  fun sellMasterItem(item: MasterItem, quantity: Int = 1) {
    if (item.count <= 0) return
    val actualSellCount = quantity.coerceAtMost(item.count)

    val current = _petState.value
    val totalGain = item.sellPrice * actualSellCount
    val newCoins = current.coins + totalGain
    val updatedPet = current.copy(coins = newCoins)
    _petState.value = updatedPet
    viewModelScope.launch { dao.updatePetStats(updatedPet) }

    _masterInventory.value = _masterInventory.value.map {
      if (it.id == item.id) it.copy(count = (it.count - actualSellCount).coerceAtLeast(0)) else it
    }
    _inventory.value = _masterInventory.value.map { it.toInventoryItem() }

    val tx = EconomyTransaction(
      type = TransactionType.SELL,
      amount = totalGain,
      title = "Sold ${actualSellCount}x ${item.displayName}",
      balanceAfter = newCoins
    )
    _transactions.value = listOf(tx) + _transactions.value

    com.example.util.SoundManager.playCoinSound()
    triggerFloatingPopup("+$totalGain Coins 🪙", 0xFFEAB308)
    showTemporaryNotification("Sold ${actualSellCount}x ${item.displayName} for $totalGain coins! 🪙")
  }

  fun useMasterItem(item: MasterItem) {
    if (item.count <= 0) {
      showTemporaryNotification("You don't have any ${item.displayName} left! 🎒")
      return
    }

    val current = _petState.value
    var hunger = current.hunger
    var energy = current.energy
    var joy = current.happiness
    var clean = current.cleanliness
    var health = current.health
    var friendship = current.friendship

    item.statModifiers.forEach { mod ->
      when (mod.statType) {
        StatType.HUNGER -> hunger = (hunger + mod.boostAmount).coerceAtMost(100f)
        StatType.ENERGY -> energy = (energy + mod.boostAmount).coerceAtMost(100f)
        StatType.HAPPINESS -> joy = (joy + mod.boostAmount).coerceAtMost(100f)
        StatType.CLEANLINESS -> clean = (clean + mod.boostAmount).coerceAtMost(100f)
        StatType.HEALTH -> health = (health + mod.boostAmount).coerceAtMost(100f)
        StatType.FRIENDSHIP -> friendship = (friendship + mod.boostAmount).coerceAtMost(100f)
        StatType.XP -> addXpAndCoins(xpGained = mod.boostAmount.toInt(), coinsGained = 0)
      }
    }

    val updated = current.copy(
      hunger = hunger,
      energy = energy,
      happiness = joy,
      cleanliness = clean,
      health = health,
      friendship = friendship
    )
    _petState.value = updated
    _currentMood.value = calculateMood(updated, _isSleeping.value)
    viewModelScope.launch { dao.updatePetStats(updated) }

    _masterInventory.value = _masterInventory.value.map {
      if (it.id == item.id) it.copy(count = (it.count - 1).coerceAtLeast(0)) else it
    }
    _inventory.value = _masterInventory.value.map { it.toInventoryItem() }

    com.example.util.SoundManager.playRewardSound()
    triggerFloatingPopup("Used ${item.displayName}! ✨", 0xFF10B981)
    showTemporaryNotification("Used ${item.displayName}! Mochi feels great! ✨")
  }

  fun claimDailyReward() {
    if (_hasClaimedDailyReward.value) {
      showTemporaryNotification("Daily reward already claimed today! Check back tomorrow 📅")
      return
    }

    val dayIndex = (_dailyStreakDay.value - 1).coerceIn(0, 6)
    val reward = DailyRewardRegistry.sevenDayRewards[dayIndex]

    addXpAndCoins(xpGained = reward.rewardXp, coinsGained = reward.rewardCoins)

    // Grant bonus item if present
    reward.rewardItem?.let { item ->
      _masterInventory.value = _masterInventory.value.map {
        if (it.id == item.id) it.copy(count = (it.count + 1).coerceAtMost(it.stackLimit)) else it
      }
      _inventory.value = _masterInventory.value.map { it.toInventoryItem() }
    }

    _hasClaimedDailyReward.value = true
    _dailyStreakDay.value = if (_dailyStreakDay.value >= 7) 1 else _dailyStreakDay.value + 1

    val currentCoins = _petState.value.coins
    val tx = EconomyTransaction(
      type = TransactionType.DAILY_BONUS,
      amount = reward.rewardCoins,
      title = "Day ${reward.day} Care Bonus",
      balanceAfter = currentCoins
    )
    _transactions.value = listOf(tx) + _transactions.value

    com.example.util.SoundManager.playRewardSound()
    triggerFloatingPopup("Day ${reward.day} Reward Claimed! 🎁", 0xFFEC4899)
    showTemporaryNotification("Claimed Day ${reward.day} Bonus: +${reward.rewardCoins} Coins! 🎉")
  }

  fun setShowDailyRewardDialog(show: Boolean) {
    _showDailyRewardDialog.value = show
  }


  private fun startStatsDecayLoop() {
    viewModelScope.launch {
      while (true) {
        delay(8000) // Decay stats every 8 seconds for a lively offline experience
        val current = _petState.value
        val isSleep = _isSleeping.value

        val newHunger = (current.hunger - 1.2f).coerceAtLeast(0f)
        val newClean = (current.cleanliness - 1.0f).coerceAtLeast(0f)
        val newEnergy = if (isSleep) {
          (current.energy + 4f).coerceAtMost(100f)
        } else {
          (current.energy - 0.8f).coerceAtLeast(0f)
        }
        val newJoy = (current.happiness - 0.8f).coerceAtLeast(0f)

        // Health recovers when basic needs are satisfied, drops if starved/dirty
        val newHealth = if (newHunger < 20f || newClean < 20f) {
          (current.health - 0.8f).coerceAtLeast(10f)
        } else {
          (current.health + 0.5f).coerceAtMost(100f)
        }

        val updated = current.copy(
          hunger = newHunger,
          cleanliness = newClean,
          energy = newEnergy,
          happiness = newJoy,
          health = newHealth,
          lastUpdateTime = System.currentTimeMillis()
        )
        _petState.value = updated
        _currentMood.value = calculateMood(updated, isSleep)
        dao.updatePetStats(updated)
        updateNotificationForPet(updated)
      }
    }
  }

  private fun updateNotificationForPet(pet: PetEntity) {
    _notificationMessage.value = when (_currentMood.value) {
      PetMoodState.SICK -> "Mochi is feeling unwell! Give potion or rest 🧪"
      PetMoodState.HUNGRY -> "Mochi's tummy is rumbling! 🍎"
      PetMoodState.DIRTY -> "Time for a warm bubble bath! 🧼"
      PetMoodState.SLEEPY -> "Mochi is sleepy... Zzz 🛏️"
      PetMoodState.LONELY -> "Mochi wants to play mini-games! ⚽"
      PetMoodState.EXCITED -> "Mochi is super excited and happy! 🥳"
      PetMoodState.HAPPY -> "Mochi is happy and healthy! ✨"
      PetMoodState.CALM -> "Mochi is relaxing peacefully. 🌸"
    }
  }


  fun changeRoom(room: RoomType) {
    _currentRoom.value = room
    val current = _petState.value
    val updated = current.copy(lastVisitedRoom = room.name)
    _petState.value = updated
    viewModelScope.launch { dao.updatePetStats(updated) }
  }

  fun interactWithProp(prop: InteractiveProp) {
    val current = _petState.value
    var newHunger = current.hunger
    var newEnergy = current.energy
    var newJoy = current.happiness
    var newClean = current.cleanliness
    var newFriendship = current.friendship

    when (prop.statBonusType) {
      StatType.HUNGER -> newHunger = (newHunger + prop.statBonusAmount).coerceAtMost(100f)
      StatType.ENERGY -> newEnergy = (newEnergy + prop.statBonusAmount).coerceAtMost(100f)
      StatType.HAPPINESS -> newJoy = (newJoy + prop.statBonusAmount).coerceAtMost(100f)
      StatType.CLEANLINESS -> newClean = (newClean + prop.statBonusAmount).coerceAtMost(100f)
      StatType.FRIENDSHIP -> newFriendship = (newFriendship + prop.statBonusAmount).coerceAtMost(100f)
      else -> {}
    }

    addXpAndCoins(
      xpGained = if (prop.statBonusType == StatType.XP) prop.statBonusAmount.toInt() else 5,
      coinsGained = prop.coinReward
    )

    val updated = current.copy(
      hunger = newHunger,
      energy = newEnergy,
      happiness = newJoy,
      cleanliness = newClean,
      friendship = newFriendship
    )
    _petState.value = updated
    viewModelScope.launch { dao.updatePetStats(updated) }

    showTemporaryNotification(prop.actionFeedback)
  }

  fun feedPet(item: InventoryItem) {
    if (item.count <= 0) return
    val current = _petState.value
    val newHunger = (current.hunger + item.statBoostAmount).coerceAtMost(100f)
    addXpAndCoins(xpGained = 10, coinsGained = 5)
    recordFeeding()

    val updated = current.copy(hunger = newHunger)
    _petState.value = updated
    viewModelScope.launch { dao.updatePetStats(updated) }

    consumeInventoryItem(item.id)
    showTemporaryNotification("Yum! Mochi loved the ${item.name}! 😋")
  }

  fun bathePet() {
    val current = _petState.value
    val newClean = (current.cleanliness + 35f).coerceAtMost(100f)
    val newJoy = (current.happiness + 10f).coerceAtMost(100f)
    addXpAndCoins(xpGained = 12, coinsGained = 8)
    recordBath()

    val updated = current.copy(cleanliness = newClean, happiness = newJoy)
    _petState.value = updated
    viewModelScope.launch { dao.updatePetStats(updated) }
    showTemporaryNotification("Squeaky clean! 🛁✨")
  }

  fun playWithPet() {
    val current = _petState.value
    val newJoy = (current.happiness + 30f).coerceAtMost(100f)
    val newEnergy = (current.energy - 10f).coerceAtLeast(0f)
    addXpAndCoins(xpGained = 15, coinsGained = 10)

    val updated = current.copy(happiness = newJoy, energy = newEnergy)
    _petState.value = updated
    viewModelScope.launch { dao.updatePetStats(updated) }
    showTemporaryNotification("Wheee! So much fun! ⚽🎉")
  }

  fun toggleSleep() {
    _isSleeping.value = !_isSleeping.value
    val sleeping = _isSleeping.value
    if (sleeping) {
      showTemporaryNotification("Shh... Mochi is sleeping 😴")
      triggerFloatingPopup("Zzz... Sleeping 🛏️", 0xFF6366F1)
    } else {
      showTemporaryNotification("Good morning Mochi! ☀️")
      triggerFloatingPopup("Awake & Energized! ☀️", 0xFFF59E0B)
    }
  }

  fun executeCareAction(action: CareAction) {
    val now = System.currentTimeMillis()
    val cooldownEnd = _actionCooldowns.value[action.id] ?: 0L
    if (now < cooldownEnd) {
      val remainingSec = ((cooldownEnd - now) / 1000).toInt() + 1
      showTemporaryNotification("Wait ${remainingSec}s to ${action.name.lowercase()} again ⏳")
      return
    }

    // Set cooldown
    _actionCooldowns.value = _actionCooldowns.value + (action.id to (now + action.cooldownSec * 1000L))

    val current = _petState.value
    var hunger = current.hunger
    var energy = current.energy
    var joy = current.happiness
    var clean = current.cleanliness
    var health = current.health
    var friendship = current.friendship
    var xpGain = 10
    var coinGain = 5

    when (action.type) {
      CareActionType.FEED -> {
        hunger = (hunger + 25f).coerceAtMost(100f)
        friendship = (friendship + 5f).coerceAtMost(100f)
        com.example.util.SoundManager.playEatingSound()
        com.example.util.SoundManager.playPetVoice(com.example.util.PetVoiceType.YUMMY)
        triggerFloatingPopup("+25 Hunger 🍎", 0xFFF97316)
        triggerFloatingPopup("+5 Friendship 💕", 0xFFE11D48)
        showTemporaryNotification("Yum! Mochi loved the meal! 🍎")
      }
      CareActionType.WATER -> {
        hunger = (hunger + 15f).coerceAtMost(100f)
        energy = (energy + 10f).coerceAtMost(100f)
        com.example.util.SoundManager.playDrinkingSound()
        com.example.util.SoundManager.playPetVoice(com.example.util.PetVoiceType.YUMMY)
        triggerFloatingPopup("+15 Water 💧", 0xFF3B82F6)
        triggerFloatingPopup("+10 Energy ⚡", 0xFFEAB308)
        showTemporaryNotification("Gulp gulp! Refreshing water! 💧")
      }
      CareActionType.SLEEP -> {
        com.example.util.SoundManager.playPetVoice(com.example.util.PetVoiceType.YAWN)
        toggleSleep()
        return
      }
      CareActionType.WAKE -> {
        com.example.util.SoundManager.playPetVoice(com.example.util.PetVoiceType.GREETING)
        toggleSleep()
        return
      }
      CareActionType.BATH -> {
        clean = (clean + 35f).coerceAtMost(100f)
        joy = (joy + 10f).coerceAtMost(100f)
        com.example.util.SoundManager.playBathSplashSound()
        com.example.util.SoundManager.playPetVoice(com.example.util.PetVoiceType.PURR)
        triggerFloatingPopup("+35 Cleanliness 🧼", 0xFF06B6D4)
        showTemporaryNotification("Splish splash! All clean & shiny! 🛁✨")
      }
      CareActionType.BRUSH -> {
        clean = (clean + 20f).coerceAtMost(100f)
        friendship = (friendship + 10f).coerceAtMost(100f)
        com.example.util.SoundManager.playBrushSound()
        com.example.util.SoundManager.playPetVoice(com.example.util.PetVoiceType.PURR)
        triggerFloatingPopup("+20 Cleanliness 🪮", 0xFF06B6D4)
        triggerFloatingPopup("+10 Friendship 💕", 0xFFE11D48)
        showTemporaryNotification("Mochi's fur is so soft and fluffy! 🪮✨")
      }
      CareActionType.MEDICINE -> {
        health = (health + 35f).coerceAtMost(100f)
        energy = (energy + 15f).coerceAtMost(100f)
        com.example.util.SoundManager.playMedicineSound()
        com.example.util.SoundManager.playPetVoice(com.example.util.PetVoiceType.PURR)
        triggerFloatingPopup("+35 Health 🧪", 0xFF10B981)
        showTemporaryNotification("Healing vitamin elixir taken! 🧪✨")
      }
      CareActionType.PET_HUG -> {
        friendship = (friendship + 20f).coerceAtMost(100f)
        joy = (joy + 15f).coerceAtMost(100f)
        com.example.util.SoundManager.playCuddleSound()
        com.example.util.SoundManager.playPetVoice(com.example.util.PetVoiceType.GIGGLE)
        triggerFloatingPopup("+20 Friendship 💕", 0xFFE11D48)
        triggerFloatingPopup("+15 Joy 😊", 0xFFF59E0B)
        showTemporaryNotification("Warm cozy cuddle! Mochi loves you! 🫂💕")
      }
      CareActionType.PLAY -> {
        joy = (joy + 30f).coerceAtMost(100f)
        energy = (energy - 10f).coerceAtLeast(0f)
        xpGain = 15
        coinGain = 8
        com.example.util.SoundManager.playPropInteractionSound()
        triggerFloatingPopup("+30 Joy ⚽", 0xFFF59E0B)
        triggerFloatingPopup("+15 XP ⭐", 0xFF8B5CF6)
        showTemporaryNotification("Wheee! Fun playtime! ⚽🎉")
      }
      CareActionType.PRAISE -> {
        joy = (joy + 25f).coerceAtMost(100f)
        friendship = (friendship + 15f).coerceAtMost(100f)
        com.example.util.SoundManager.playPraiseSound()
        triggerFloatingPopup("+25 Joy 🌟", 0xFFF59E0B)
        triggerFloatingPopup("+15 Friendship 💕", 0xFFE11D48)
        showTemporaryNotification("Good job Mochi! Sweet praise! 🌟😊")
      }
    }

    val updated = current.copy(
      hunger = hunger,
      energy = energy,
      happiness = joy,
      cleanliness = clean,
      health = health,
      friendship = friendship
    )
    _petState.value = updated
    _currentMood.value = calculateMood(updated, _isSleeping.value)
    viewModelScope.launch { dao.updatePetStats(updated) }

    addXpAndCoins(xpGained = xpGain, coinsGained = coinGain)
  }

  fun triggerFloatingPopup(text: String, colorHex: Long) {
    val popup = FloatingStatPopup(text = text, colorHex = colorHex)
    _floatingPopups.value = _floatingPopups.value + popup
    viewModelScope.launch {
      delay(2500)
      _floatingPopups.value = _floatingPopups.value.filter { it.id != popup.id }
    }
  }

  fun buyItem(item: InventoryItem) {
    val current = _petState.value
    if (current.coins >= item.price) {
      val newCoins = current.coins - item.price
      val updatedPet = current.copy(coins = newCoins)
      _petState.value = updatedPet
      viewModelScope.launch { dao.updatePetStats(updatedPet) }

      _inventory.value = _inventory.value.map {
        if (it.id == item.id) it.copy(count = it.count + 1) else it
      }
      showTemporaryNotification("Bought ${item.name}! 🎉")
    } else {
      showTemporaryNotification("Not enough coins! Play mini-games to earn coins 🪙")
    }
  }

  fun equipCosmeticSlot(slot: CosmeticSlot, itemId: String) {
    val current = _petState.value
    val updated = when (slot) {
      CosmeticSlot.HAT -> current.copy(equippedHat = itemId)
      CosmeticSlot.HAIR -> current.copy(equippedHair = itemId)
      CosmeticSlot.GLASSES -> current.copy(equippedGlasses = itemId)
      CosmeticSlot.FACE -> current.copy(equippedFace = itemId)
      CosmeticSlot.NECK -> current.copy(equippedNeck = itemId)
      CosmeticSlot.SHIRT -> current.copy(equippedShirt = itemId)
      CosmeticSlot.JACKET -> current.copy(equippedJacket = itemId)
      CosmeticSlot.PANTS -> current.copy(equippedPants = itemId)
      CosmeticSlot.SHOES -> current.copy(equippedShoes = itemId)
      CosmeticSlot.TAIL -> current.copy(equippedTail = itemId)
      CosmeticSlot.BACK -> current.copy(equippedBack = itemId)
      CosmeticSlot.HAND -> current.copy(equippedHand = itemId)
    }
    _petState.value = updated
    viewModelScope.launch { dao.updatePetStats(updated) }

    com.example.util.SoundManager.playEquipSound()
    triggerFloatingPopup("Equipped ${slot.displayName}! ✨", 0xFF10B981)
    showTemporaryNotification("Equipped ${slot.displayName}! Mochi looks fantastic! 👑✨")
  }

  fun unequipCosmeticSlot(slot: CosmeticSlot) {
    val current = _petState.value
    val updated = when (slot) {
      CosmeticSlot.HAT -> current.copy(equippedHat = "none")
      CosmeticSlot.HAIR -> current.copy(equippedHair = "none")
      CosmeticSlot.GLASSES -> current.copy(equippedGlasses = "none")
      CosmeticSlot.FACE -> current.copy(equippedFace = "none")
      CosmeticSlot.NECK -> current.copy(equippedNeck = "none")
      CosmeticSlot.SHIRT -> current.copy(equippedShirt = "none")
      CosmeticSlot.JACKET -> current.copy(equippedJacket = "none")
      CosmeticSlot.PANTS -> current.copy(equippedPants = "none")
      CosmeticSlot.SHOES -> current.copy(equippedShoes = "none")
      CosmeticSlot.TAIL -> current.copy(equippedTail = "none")
      CosmeticSlot.BACK -> current.copy(equippedBack = "none")
      CosmeticSlot.HAND -> current.copy(equippedHand = "none")
    }
    _petState.value = updated
    viewModelScope.launch { dao.updatePetStats(updated) }

    com.example.util.SoundManager.playRemoveSound()
    showTemporaryNotification("Removed ${slot.displayName}! 🧹")
  }

  fun updateAppearanceOption(category: String, optionId: String) {
    val current = _petState.value
    val updated = when (category.lowercase()) {
      "color", "skin" -> current.copy(equippedColor = optionId)
      "eyestyle", "eyes" -> current.copy(eyeStyle = optionId)
      "eyecolor" -> current.copy(eyeColor = optionId)
      "mouth" -> current.copy(mouthStyle = optionId)
      "pattern", "bodypattern" -> current.copy(bodyPattern = optionId)
      else -> current
    }
    _petState.value = updated
    viewModelScope.launch { dao.updatePetStats(updated) }

    com.example.util.SoundManager.playSelectionSound()
    showTemporaryNotification("Updated ${category.replaceFirstChar { it.uppercase() }}! ✨")
  }

  fun saveCustomizationSettings(newPetState: PetEntity) {
    _petState.value = newPetState
    viewModelScope.launch { dao.updatePetStats(newPetState) }

    com.example.util.SoundManager.playConfirmSound()
    triggerFloatingPopup("Customization Saved! 🎨✨", 0xFF8B5CF6)
    showTemporaryNotification("Customization saved! Mochi looks amazing! 🌸👑")
  }

  fun resetCustomizationAppearance() {
    val current = _petState.value
    val updated = current.copy(
      equippedHat = "none",
      equippedHair = "none",
      equippedGlasses = "none",
      equippedFace = "none",
      equippedNeck = "none",
      equippedShirt = "none",
      equippedJacket = "none",
      equippedPants = "none",
      equippedShoes = "none",
      equippedTail = "none",
      equippedBack = "none",
      equippedHand = "none",
      equippedColor = "pastel_pink",
      eyeStyle = "sparkle_round",
      eyeColor = "berry_violet",
      mouthStyle = "sweet_smile",
      bodyPattern = "none"
    )
    _petState.value = updated
    viewModelScope.launch { dao.updatePetStats(updated) }

    com.example.util.SoundManager.playRemoveSound()
    showTemporaryNotification("Reset Mochi's appearance to default! 🔄")
  }

  fun equipHat(hatId: String) {
    equipCosmeticSlot(CosmeticSlot.HAT, hatId)
  }

  fun addXpAndCoins(xpGained: Int, coinsGained: Int) {
    val current = _petState.value
    var newXp = current.xp + xpGained
    var newLevel = current.level
    var newMaxXp = current.maxXp
    val leveledUp = newXp >= newMaxXp

    if (leveledUp) {
      newXp -= newMaxXp
      newLevel += 1
      newMaxXp = (newMaxXp * 1.25f).toInt()
      _levelUpDialogState.value = newLevel
      com.example.util.SoundManager.playVictorySound()
      triggerFloatingPopup("LEVEL UP! Level $newLevel! 🌟", 0xFFF59E0B)
      showTemporaryNotification("LEVEL UP! Reached Level $newLevel! 🌟")
    }

    if (coinsGained > 0) {
      recordCoinsEarned(coinsGained)
    }

    val updated = current.copy(
      xp = newXp,
      level = newLevel,
      maxXp = newMaxXp,
      coins = current.coins + coinsGained
    )
    _petState.value = updated
    viewModelScope.launch { dao.updatePetStats(updated) }
  }

  fun openMiniGame(gameName: String) {
    _activeMiniGame.value = gameName
  }

  fun closeMiniGame() {
    _activeMiniGame.value = null
  }

  private fun consumeInventoryItem(id: String) {
    _inventory.value = _inventory.value.map {
      if (it.id == id) it.copy(count = (it.count - 1).coerceAtLeast(0)) else it
    }
  }

  private fun showTemporaryNotification(msg: String) {
    _notificationMessage.value = msg
    viewModelScope.launch {
      delay(4000)
      updateNotificationForPet(_petState.value)
    }
  }

  fun getDerivedEmotion(): PetEmotion {
    if (_isSleeping.value) return PetEmotion.SLEEPING
    return when (_currentMood.value) {
      PetMoodState.SICK -> PetEmotion.SICK
      PetMoodState.HUNGRY -> PetEmotion.HUNGRY
      PetMoodState.DIRTY -> PetEmotion.DIRTY
      PetMoodState.SLEEPY -> PetEmotion.TIRED
      PetMoodState.LONELY -> PetEmotion.LONELY
      PetMoodState.EXCITED -> PetEmotion.EXCITED
      PetMoodState.HAPPY -> PetEmotion.HAPPY
      PetMoodState.CALM -> PetEmotion.PLAYFUL
    }
  }

  // --- HOME DECORATION & FURNITURE PLACEMENT SYSTEM ---

  fun toggleDecorationMode(enabled: Boolean) {
    _isDecorationMode.value = enabled
    if (!enabled) {
      _selectedFurnitureToPlace.value = null
      _selectedPlacedFurniture.value = null
    }
    com.example.util.SoundManager.playSelectionSound()
  }

  fun selectFurnitureForPlacement(item: MasterItem?) {
    _selectedFurnitureToPlace.value = item
    _selectedPlacedFurniture.value = null
    if (item != null) com.example.util.SoundManager.playSelectionSound()
  }

  fun selectPlacedFurniture(furniture: PlacedFurniture?) {
    _selectedPlacedFurniture.value = furniture
    _selectedFurnitureToPlace.value = null
    if (furniture != null) com.example.util.SoundManager.playSelectionSound()
  }

  fun placeFurniture(room: RoomType, item: MasterItem, xFraction: Float = 0.5f, yFraction: Float = 0.5f) {
    val currentRoomList = _placedFurnitureMap.value[room] ?: emptyList()
    val newInstanceId = "furn_${System.currentTimeMillis()}_${(100..999).random()}"
    val newPlaced = PlacedFurniture(
      instanceId = newInstanceId,
      itemId = item.id,
      displayName = item.displayName,
      icon = item.icon,
      roomType = room,
      xFraction = xFraction.coerceIn(0.12f, 0.88f),
      yFraction = yFraction.coerceIn(0.20f, 0.85f),
      rotationDegrees = 0,
      category = FurnitureCategory.STORAGE
    )

    val updatedRoomList = currentRoomList + newPlaced
    val updatedMap = _placedFurnitureMap.value + (room to updatedRoomList)
    _placedFurnitureMap.value = updatedMap

    saveRoomDecorationsToDb(updatedMap)
    com.example.util.SoundManager.playPlacementSound()
    triggerFloatingPopup("Placed ${item.displayName}! 🛋️", 0xFF10B981)
    showTemporaryNotification("Placed ${item.displayName} in ${room.displayName}! ✨")
    _selectedFurnitureToPlace.value = null
  }

  fun moveFurniture(room: RoomType, instanceId: String, newX: Float, newY: Float) {
    val currentRoomList = _placedFurnitureMap.value[room] ?: return
    val updatedList = currentRoomList.map {
      if (it.instanceId == instanceId) {
        it.copy(
          xFraction = newX.coerceIn(0.10f, 0.90f),
          yFraction = newY.coerceIn(0.18f, 0.88f)
        )
      } else it
    }
    val updatedMap = _placedFurnitureMap.value + (room to updatedList)
    _placedFurnitureMap.value = updatedMap

    // Also update currently selected item reference if it's the moved item
    _selectedPlacedFurniture.value?.let { sel ->
      if (sel.instanceId == instanceId) {
        _selectedPlacedFurniture.value = sel.copy(
          xFraction = newX.coerceIn(0.10f, 0.90f),
          yFraction = newY.coerceIn(0.18f, 0.88f)
        )
      }
    }

    saveRoomDecorationsToDb(updatedMap)
    com.example.util.SoundManager.playMoveSound()
  }

  fun rotateFurniture(room: RoomType, instanceId: String) {
    val currentRoomList = _placedFurnitureMap.value[room] ?: return
    val updatedList = currentRoomList.map {
      if (it.instanceId == instanceId) {
        val nextRotation = (it.rotationDegrees + 90) % 360
        it.copy(rotationDegrees = nextRotation)
      } else it
    }
    val updatedMap = _placedFurnitureMap.value + (room to updatedList)
    _placedFurnitureMap.value = updatedMap

    _selectedPlacedFurniture.value?.let { sel ->
      if (sel.instanceId == instanceId) {
        _selectedPlacedFurniture.value = sel.copy(rotationDegrees = (sel.rotationDegrees + 90) % 360)
      }
    }

    saveRoomDecorationsToDb(updatedMap)
    com.example.util.SoundManager.playRotateSound()
    triggerFloatingPopup("Rotated Furniture 🔄", 0xFF8B5CF6)
  }

  fun removeFurniture(room: RoomType, instanceId: String) {
    val currentRoomList = _placedFurnitureMap.value[room] ?: return
    val targetItem = currentRoomList.find { it.instanceId == instanceId }
    val updatedList = currentRoomList.filter { it.instanceId != instanceId }
    val updatedMap = _placedFurnitureMap.value + (room to updatedList)
    _placedFurnitureMap.value = updatedMap

    if (_selectedPlacedFurniture.value?.instanceId == instanceId) {
      _selectedPlacedFurniture.value = null
    }

    saveRoomDecorationsToDb(updatedMap)
    com.example.util.SoundManager.playRemoveSound()
    targetItem?.let {
      showTemporaryNotification("Removed ${it.displayName} back to inventory 🎒")
    }
  }

  fun changeWallpaper(room: RoomType, wallpaperId: String) {
    val updatedWallpapers = _roomWallpapers.value + (room to wallpaperId)
    _roomWallpapers.value = updatedWallpapers

    val currentPet = _petState.value
    val updatedPet = when (room) {
      RoomType.SLEEP -> currentPet.copy(bedroomWallpaper = wallpaperId)
      RoomType.KITCHEN -> currentPet.copy(kitchenWallpaper = wallpaperId)
      RoomType.BATH -> currentPet.copy(bathroomWallpaper = wallpaperId)
      RoomType.PLAY -> currentPet.copy(playroomWallpaper = wallpaperId)
      RoomType.GARDEN -> currentPet.copy(gardenWallpaper = wallpaperId)
    }
    _petState.value = updatedPet
    viewModelScope.launch { dao.updatePetStats(updatedPet) }

    com.example.util.SoundManager.playConfirmSound()
    val option = RoomCustomizationRegistry.getWallpaperById(wallpaperId)
    showTemporaryNotification("Changed ${room.displayName} wallpaper to ${option?.displayName ?: "new style"}! 🎨")
  }

  fun changeFloor(room: RoomType, floorId: String) {
    val updatedFloors = _roomFloors.value + (room to floorId)
    _roomFloors.value = updatedFloors

    val currentPet = _petState.value
    val updatedPet = when (room) {
      RoomType.SLEEP -> currentPet.copy(bedroomFloor = floorId)
      RoomType.KITCHEN -> currentPet.copy(kitchenFloor = floorId)
      RoomType.BATH -> currentPet.copy(bathroomFloor = floorId)
      RoomType.PLAY -> currentPet.copy(playroomFloor = floorId)
      RoomType.GARDEN -> currentPet.copy(gardenFloor = floorId)
    }
    _petState.value = updatedPet
    viewModelScope.launch { dao.updatePetStats(updatedPet) }

    com.example.util.SoundManager.playConfirmSound()
    val option = RoomCustomizationRegistry.getFloorById(floorId)
    showTemporaryNotification("Changed ${room.displayName} floor to ${option?.displayName ?: "new style"}! 🪵")
  }

  fun resetRoomDecoration(room: RoomType) {
    val defaultWallpapers = mapOf(
      RoomType.SLEEP to "wp_starry_night",
      RoomType.KITCHEN to "wp_cozy_brick",
      RoomType.BATH to "wp_mint_grid",
      RoomType.PLAY to "wp_pastel_stripes",
      RoomType.GARDEN to "wp_sunny_meadow"
    )
    val defaultFloors = mapOf(
      RoomType.SLEEP to "fl_oak_wood",
      RoomType.KITCHEN to "fl_checkered",
      RoomType.BATH to "fl_marble_tile",
      RoomType.PLAY to "fl_cozy_carpet",
      RoomType.GARDEN to "fl_green_lawn"
    )

    changeWallpaper(room, defaultWallpapers[room] ?: "wp_starry_night")
    changeFloor(room, defaultFloors[room] ?: "fl_oak_wood")

    val updatedMap = _placedFurnitureMap.value + (room to emptyList())
    _placedFurnitureMap.value = updatedMap
    _selectedPlacedFurniture.value = null
    _selectedFurnitureToPlace.value = null

    saveRoomDecorationsToDb(updatedMap)
    com.example.util.SoundManager.playRemoveSound()
    showTemporaryNotification("Reset ${room.displayName} layout to default! 🔄")
  }

  private fun saveRoomDecorationsToDb(map: Map<RoomType, List<PlacedFurniture>>) {
    val jsonString = serializePlacedFurniture(map)
    val currentPet = _petState.value
    val updatedPet = currentPet.copy(placedFurnitureJson = jsonString)
    _petState.value = updatedPet
    viewModelScope.launch { dao.updatePetStats(updatedPet) }
  }

  private fun serializePlacedFurniture(map: Map<RoomType, List<PlacedFurniture>>): String {
    val items = mutableListOf<String>()
    map.forEach { (_, furnitureList) ->
      furnitureList.forEach { f ->
        items.add("${f.instanceId}::${f.itemId}::${f.displayName}::${f.icon}::${f.roomType.name}::${f.xFraction}::${f.yFraction}::${f.rotationDegrees}::${f.category.name}")
      }
    }
    return items.joinToString(";;")
  }

  private fun deserializePlacedFurniture(dataStr: String): Map<RoomType, List<PlacedFurniture>> {
    if (dataStr.isBlank()) {
      // Create initial cozy default furniture placements
      return mapOf(
        RoomType.SLEEP to listOf(
          PlacedFurniture("p1", "furn_bed_canopy", "Royal Canopy Bed", "🛏️", RoomType.SLEEP, 0.22f, 0.65f, 0, FurnitureCategory.BEDS),
          PlacedFurniture("p2", "furn_lamp_lava", "Neon Lava Lamp", "💡", RoomType.SLEEP, 0.78f, 0.45f, 0, FurnitureCategory.LAMPS)
        ),
        RoomType.KITCHEN to listOf(
          PlacedFurniture("p3", "furn_table_glass", "Modern Coffee Table", "🪵", RoomType.KITCHEN, 0.50f, 0.70f, 0, FurnitureCategory.TABLES)
        ),
        RoomType.BATH to listOf(
          PlacedFurniture("p4", "furn_plant_monstera", "Giant Monstera", "🪴", RoomType.BATH, 0.85f, 0.72f, 0, FurnitureCategory.PLANTS)
        ),
        RoomType.PLAY to listOf(
          PlacedFurniture("p5", "furn_shelf_books", "Oak Bookshelf", "📚", RoomType.PLAY, 0.50f, 0.35f, 0, FurnitureCategory.STORAGE),
          PlacedFurniture("p6", "furn_toy_arcade", "Mini Arcade Cabinet", "🕹️", RoomType.PLAY, 0.80f, 0.68f, 0, FurnitureCategory.TOYS)
        ),
        RoomType.GARDEN to listOf(
          PlacedFurniture("p7", "furn_outdoor_fountain", "Magic Stone Fountain", "⛲", RoomType.GARDEN, 0.82f, 0.62f, 0, FurnitureCategory.OUTDOOR)
        )
      )
    }

    val resultMap = mutableMapOf<RoomType, MutableList<PlacedFurniture>>()
    RoomType.values().forEach { resultMap[it] = mutableListOf() }

    try {
      val tokens = dataStr.split(";;")
      tokens.forEach { token ->
        val parts = token.split("::")
        if (parts.size >= 9) {
          val instanceId = parts[0]
          val itemId = parts[1]
          val displayName = parts[2]
          val icon = parts[3]
          val roomType = RoomType.valueOf(parts[4])
          val xFraction = parts[5].toFloatOrNull() ?: 0.5f
          val yFraction = parts[6].toFloatOrNull() ?: 0.5f
          val rotation = parts[7].toIntOrNull() ?: 0
          val cat = try { FurnitureCategory.valueOf(parts[8]) } catch (e: Exception) { FurnitureCategory.STORAGE }

          val pf = PlacedFurniture(instanceId, itemId, displayName, icon, roomType, xFraction, yFraction, rotation, cat)
          resultMap[roomType]?.add(pf)
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }

    return resultMap
  }

  // --- PHASE 8: ACTIVITY HUB & MINI-GAME FRAMEWORK ENGINE ---

  fun startActivitySession(activityId: String, difficulty: GameDifficulty = GameDifficulty.NORMAL) {
    val activityDef = ActivityRegistry.getActivityById(activityId) ?: return
    val currentHighScore = _highScoresMap.value[activityId] ?: 0

    _activeActivity.value = activityDef
    _gameSession.value = GameSession(
      activityId = activityId,
      activityName = activityDef.displayName,
      icon = activityDef.icon,
      difficulty = difficulty,
      currentScore = 0,
      highScore = currentHighScore,
      combo = 0,
      maxCombo = 0,
      multiplier = 1.0f,
      accuracy = 100f,
      timeRemainingSeconds = activityDef.durationSeconds,
      maxTimeSeconds = activityDef.durationSeconds,
      status = GameSessionStatus.RUNNING
    )

    com.example.util.SoundManager.playStartGameSound()
    triggerFloatingPopup("Started ${activityDef.displayName}! 🎮", 0xFF3B82F6)
  }

  fun pauseGameSession() {
    val current = _gameSession.value ?: return
    if (current.status == GameSessionStatus.RUNNING) {
      _gameSession.value = current.copy(status = GameSessionStatus.PAUSED)
      com.example.util.SoundManager.playPauseSound()
    }
  }

  fun resumeGameSession() {
    val current = _gameSession.value ?: return
    if (current.status == GameSessionStatus.PAUSED) {
      _gameSession.value = current.copy(status = GameSessionStatus.RUNNING)
      com.example.util.SoundManager.playResumeSound()
    }
  }

  fun updateGameScore(pointsDelta: Int, hitSuccess: Boolean) {
    val current = _gameSession.value ?: return
    if (current.status != GameSessionStatus.RUNNING) return

    val newCombo = if (hitSuccess) current.combo + 1 else 0
    val newMaxCombo = maxOf(current.maxCombo, newCombo)

    // Dynamic multiplier based on combo streak
    val newMultiplier = when {
      newCombo >= 20 -> 3.0f
      newCombo >= 10 -> 2.0f
      newCombo >= 5 -> 1.5f
      else -> 1.0f
    }

    val multipliedPoints = (pointsDelta * newMultiplier * current.difficulty.rewardMultiplier).toInt()
    val newScore = maxOf(0, current.currentScore + multipliedPoints)

    _gameSession.value = current.copy(
      currentScore = newScore,
      combo = newCombo,
      maxCombo = newMaxCombo,
      multiplier = newMultiplier
    )

    if (hitSuccess && newCombo > 1 && newCombo % 5 == 0) {
      com.example.util.SoundManager.playComboSound()
      triggerFloatingPopup("${newCombo}x Combo! 🔥", 0xFFF59E0B)
    }
  }

  fun completeGameSession(finalScore: Int, accuracy: Float = 100f) {
    val current = _gameSession.value ?: return
    val activityDef = _activeActivity.value ?: return

    val isNewHigh = finalScore > current.highScore
    val newHighScore = if (isNewHigh) finalScore else current.highScore

    // Calculate stars: 3 stars >= 100 pts, 2 stars >= 50 pts, 1 star > 0
    val stars = when {
      finalScore >= 120 -> 3
      finalScore >= 60 -> 2
      finalScore > 0 -> 1
      else -> 0
    }

    // Calculate rewards
    val rawCoins = (activityDef.baseCoinReward * (finalScore / 50f + 1f) * current.difficulty.rewardMultiplier).toInt()
    val coinsEarned = rawCoins.coerceAtLeast(activityDef.baseCoinReward)
    val rawXp = (activityDef.baseXpReward * current.difficulty.rewardMultiplier).toInt()
    val xpEarned = rawXp.coerceAtLeast(activityDef.baseXpReward)

    _gameSession.value = current.copy(
      currentScore = finalScore,
      highScore = newHighScore,
      accuracy = accuracy,
      coinsEarned = coinsEarned,
      xpEarned = xpEarned,
      starsEarned = stars,
      status = GameSessionStatus.COMPLETED,
      isNewHighScore = isNewHigh
    )

    // Save High Score map
    val updatedHighScores = _highScoresMap.value + (activityDef.id to newHighScore)
    _highScoresMap.value = updatedHighScores

    val currentPet = _petState.value
    val updatedPet = currentPet.copy(
      coins = currentPet.coins + coinsEarned,
      xp = currentPet.xp + xpEarned,
      happiness = (currentPet.happiness + 20f).coerceAtMost(100f),
      highScoresJson = serializeHighScores(updatedHighScores),
      totalActivitiesPlayed = currentPet.totalActivitiesPlayed + 1,
      totalMiniGameCoinsEarned = currentPet.totalMiniGameCoinsEarned + coinsEarned
    )
    _petState.value = updatedPet
    viewModelScope.launch { dao.updatePetStats(updatedPet) }

    recordMiniGamePlayed(coinsEarned)

    com.example.util.SoundManager.playVictorySound()
    triggerFloatingPopup("Victory! +$coinsEarned Coins ✨", 0xFF10B981)
  }

  fun failGameSession() {
    val current = _gameSession.value ?: return
    _gameSession.value = current.copy(status = GameSessionStatus.FAILED)
    com.example.util.SoundManager.playDefeatSound()
  }

  fun restartGameSession() {
    val activityDef = _activeActivity.value ?: return
    startActivitySession(activityDef.id, _gameSession.value?.difficulty ?: GameDifficulty.NORMAL)
  }

  fun exitGameSession() {
    _gameSession.value = null
    _activeActivity.value = null
    _activeMiniGame.value = null
    com.example.util.SoundManager.playSelectionSound()
  }

  // --- PHASE 9: PLAYER PROGRESSION & META SYSTEMS ENGINE ---

  fun recordFeeding() {
    recordCareAction(CareActionType.FEED)
  }

  fun recordBath() {
    recordCareAction(CareActionType.BATH)
  }

  fun recordCareAction(actionType: CareActionType) {
    val meta = _playerMetaStats.value
    val newCareCount = meta.totalCareActionsPerformed + 1
    var newFoods = meta.totalFoodsFed
    var newBaths = meta.totalBathsGiven

    if (actionType == CareActionType.FEED) {
      newFoods += 1
      updateDailyQuestProgress(DailyQuestType.FEED, 1)
      updateAchievementProgress("ach_feed_10", 1)
      updateAchievementProgress("ach_feed_50", 1)
    } else if (actionType == CareActionType.BATH) {
      newBaths += 1
      updateDailyQuestProgress(DailyQuestType.BATH, 1)
      updateAchievementProgress("ach_bath_10", 1)
    }

    updateAchievementProgress("ach_care_100", 1)

    val updatedMeta = meta.copy(
      totalCareActionsPerformed = newCareCount,
      totalFoodsFed = newFoods,
      totalBathsGiven = newBaths
    )
    _playerMetaStats.value = updatedMeta
    saveMetaStatsToPet(updatedMeta)
  }

  fun recordCoinsEarned(amount: Int) {
    if (amount <= 0) return
    val meta = _playerMetaStats.value
    val newTotal = meta.lifetimeCoinsEarned + amount
    updateDailyQuestProgress(DailyQuestType.EARN_COINS, amount)
    updateAchievementProgress("ach_coins_5000", amount)

    val updatedMeta = meta.copy(lifetimeCoinsEarned = newTotal)
    _playerMetaStats.value = updatedMeta
    saveMetaStatsToPet(updatedMeta)
  }

  fun recordCoinsSpent(amount: Int) {
    if (amount <= 0) return
    val meta = _playerMetaStats.value
    val newTotal = meta.lifetimeCoinsSpent + amount
    updateAchievementProgress("ach_spend_2000", amount)

    val updatedMeta = meta.copy(lifetimeCoinsSpent = newTotal)
    _playerMetaStats.value = updatedMeta
    saveMetaStatsToPet(updatedMeta)
  }

  fun recordFurniturePlaced() {
    val meta = _playerMetaStats.value
    val newCount = meta.totalFurniturePlaced + 1
    updateDailyQuestProgress(DailyQuestType.DECORATE, 1)
    updateAchievementProgress("ach_decor_5", 1)
    updateAchievementProgress("ach_decor_15", 1)

    val updatedMeta = meta.copy(totalFurniturePlaced = newCount)
    _playerMetaStats.value = updatedMeta
    saveMetaStatsToPet(updatedMeta)
  }

  fun recordCustomizationEquipped() {
    val meta = _playerMetaStats.value
    val newCount = meta.totalCustomizationsEquipped + 1
    updateAchievementProgress("ach_fashion_5", 1)

    val updatedMeta = meta.copy(totalCustomizationsEquipped = newCount)
    _playerMetaStats.value = updatedMeta
    saveMetaStatsToPet(updatedMeta)
  }

  fun recordMiniGamePlayed(coinsEarned: Int) {
    val meta = _playerMetaStats.value
    val newGames = meta.totalActivitiesPlayed + 1
    val newGameCoins = meta.totalMiniGameCoinsEarned + coinsEarned

    updateDailyQuestProgress(DailyQuestType.PLAY, 1)
    updateAchievementProgress("ach_games_10", 1)
    updateAchievementProgress("ach_games_50", 1)
    if (coinsEarned > 0) {
      updateAchievementProgress("ach_game_coins_1000", coinsEarned)
      recordCoinsEarned(coinsEarned)
    }

    val updatedMeta = meta.copy(
      totalActivitiesPlayed = newGames,
      totalMiniGameCoinsEarned = newGameCoins
    )
    _playerMetaStats.value = updatedMeta
    saveMetaStatsToPet(updatedMeta)
  }

  fun updateDailyQuestProgress(type: DailyQuestType, count: Int) {
    val updatedQuests = _dailyQuests.value.map { quest ->
      if (quest.type == type && !quest.isClaimed) {
        val newProg = (quest.currentProgress + count).coerceAtMost(quest.targetProgress)
        quest.copy(currentProgress = newProg)
      } else quest
    }
    _dailyQuests.value = updatedQuests
    saveDailyQuestsToPet(updatedQuests)
  }

  fun updateAchievementProgress(achievementId: String, delta: Int) {
    val def = MetaRegistry.achievements.find { it.id == achievementId } ?: return
    val currentProg = _achievementsMap.value[achievementId] ?: AchievementProgress(achievementId = achievementId)
    if (currentProg.currentProgress >= def.maxProgress) return // Already completed

    val newProg = (currentProg.currentProgress + delta).coerceAtMost(def.maxProgress)
    val updatedProg = currentProg.copy(currentProgress = newProg)

    val newMap = _achievementsMap.value + (achievementId to updatedProg)
    _achievementsMap.value = newMap

    if (newProg >= def.maxProgress && currentProg.currentProgress < def.maxProgress) {
      // Just unlocked achievement!
      com.example.util.SoundManager.playVictorySound()
      _achievementUnlockedBanner.value = def
      triggerFloatingPopup("Achievement Unlocked: ${def.title}! 🏆", 0xFFF59E0B)
    }

    saveAchievementsToPet(newMap)
  }

  fun claimAchievementReward(achievementId: String) {
    val def = MetaRegistry.achievements.find { it.id == achievementId } ?: return
    val currentProg = _achievementsMap.value[achievementId] ?: return
    if (currentProg.currentProgress < def.maxProgress || currentProg.isClaimed) return

    val updatedProg = currentProg.copy(isClaimed = true)
    val newMap = _achievementsMap.value + (achievementId to updatedProg)
    _achievementsMap.value = newMap
    saveAchievementsToPet(newMap)

    addXpAndCoins(xpGained = def.rewardXp, coinsGained = def.rewardCoins)
    com.example.util.SoundManager.playRewardSound()
    triggerFloatingPopup("Claimed Trophy Reward! +${def.rewardCoins} Coins 🏆", 0xFF10B981)
  }

  fun claimDailyQuestReward(questId: String) {
    val quest = _dailyQuests.value.find { it.id == questId } ?: return
    if (quest.currentProgress < quest.targetProgress || quest.isClaimed) return

    val updatedQuests = _dailyQuests.value.map {
      if (it.id == questId) it.copy(isClaimed = true) else it
    }
    _dailyQuests.value = updatedQuests
    saveDailyQuestsToPet(updatedQuests)

    addXpAndCoins(xpGained = quest.rewardXp, coinsGained = quest.rewardCoins)
    com.example.util.SoundManager.playRewardSound()
    com.example.util.SoundManager.playPetVoice(com.example.util.PetVoiceType.GIGGLE)
    triggerFloatingPopup("Mission Completed! +${quest.rewardCoins} Coins 📜", 0xFF10B981)
  }

  fun unlockCollectionItem(itemId: String) {
    if (_unlockedCollections.value.contains(itemId)) return
    val newSet = _unlockedCollections.value + itemId
    _unlockedCollections.value = newSet

    val pet = _petState.value
    val updated = pet.copy(unlockedCollectionsJson = newSet.joinToString(","))
    _petState.value = updated
    viewModelScope.launch { dao.updatePetStats(updated) }
  }

  fun dismissLevelUpDialog() {
    _levelUpDialogState.value = null
  }

  fun dismissAchievementBanner() {
    _achievementUnlockedBanner.value = null
  }

  private fun saveMetaStatsToPet(meta: PlayerStatsMeta) {
    val current = _petState.value
    val updated = current.copy(
      lifetimeCoinsEarned = meta.lifetimeCoinsEarned,
      lifetimeCoinsSpent = meta.lifetimeCoinsSpent,
      totalFoodsFed = meta.totalFoodsFed,
      totalBathsGiven = meta.totalBathsGiven,
      totalCareActionsPerformed = meta.totalCareActionsPerformed,
      totalFurniturePlaced = meta.totalFurniturePlaced,
      totalCustomizationsEquipped = meta.totalCustomizationsEquipped,
      totalActivitiesPlayed = meta.totalActivitiesPlayed,
      totalMiniGameCoinsEarned = meta.totalMiniGameCoinsEarned
    )
    _petState.value = updated
    viewModelScope.launch { dao.updatePetStats(updated) }
  }

  private fun saveAchievementsToPet(map: Map<String, AchievementProgress>) {
    val serialized = map.values.joinToString(";;") { "${it.achievementId}::${it.currentProgress}::${it.isClaimed}" }
    val current = _petState.value
    val updated = current.copy(achievementsJson = serialized)
    _petState.value = updated
    viewModelScope.launch { dao.updatePetStats(updated) }
  }

  private fun deserializeAchievements(jsonStr: String): Map<String, AchievementProgress> {
    if (jsonStr.isBlank()) return emptyMap()
    val map = mutableMapOf<String, AchievementProgress>()
    try {
      jsonStr.split(";;").forEach { token ->
        val parts = token.split("::")
        if (parts.size >= 3) {
          val id = parts[0]
          val prog = parts[1].toIntOrNull() ?: 0
          val claimed = parts[2].toBooleanStrictOrNull() ?: false
          map[id] = AchievementProgress(id, prog, claimed)
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return map
  }

  private fun saveDailyQuestsToPet(quests: List<DailyQuest>) {
    val serialized = quests.joinToString(";;") { "${it.id}::${it.currentProgress}::${it.isClaimed}" }
    val todayDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
    val current = _petState.value
    val updated = current.copy(dailyQuestsJson = serialized, lastDailyResetDate = todayDate)
    _petState.value = updated
    viewModelScope.launch { dao.updatePetStats(updated) }
  }

  private fun deserializeDailyQuests(jsonStr: String, lastResetDate: String): List<DailyQuest> {
    val todayDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
    val defaultQuests = MetaRegistry.getDefaultDailyQuests()

    if (lastResetDate != todayDate || jsonStr.isBlank()) {
      return defaultQuests
    }

    val progressMap = mutableMapOf<String, Pair<Int, Boolean>>()
    try {
      jsonStr.split(";;").forEach { token ->
        val parts = token.split("::")
        if (parts.size >= 3) {
          val id = parts[0]
          val prog = parts[1].toIntOrNull() ?: 0
          val claimed = parts[2].toBooleanStrictOrNull() ?: false
          progressMap[id] = Pair(prog, claimed)
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }

    return defaultQuests.map { q ->
      val saved = progressMap[q.id]
      if (saved != null) q.copy(currentProgress = saved.first, isClaimed = saved.second) else q
    }
  }

  private fun deserializeCollections(jsonStr: String): Set<String> {
    if (jsonStr.isBlank()) return setOf("wp_starry_night", "fl_oak_wood", "snack_apple", "snack_cake")
    return jsonStr.split(",").toSet()
  }

  fun setShowGuideDialog(show: Boolean) {
    _showGuideDialog.value = show
  }

  fun completeOnboarding(petName: String) {
    val cleanName = petName.trim().ifEmpty { "Mochi" }
    val updated = _petState.value.copy(
      name = cleanName,
      level = 1,
      xp = 0,
      maxXp = 100,
      coins = 0,
      lifetimeCoinsEarned = 0,
      lifetimeCoinsSpent = 0,
      isOnboardingCompleted = true
    )
    _petState.value = updated
    _showGuideDialog.value = true
    viewModelScope.launch {
      dao.updatePetStats(updated)
    }
  }

  fun resetOnboarding() {
    val updated = _petState.value.copy(isOnboardingCompleted = false)
    _petState.value = updated
    viewModelScope.launch {
      dao.updatePetStats(updated)
    }
  }

  private fun serializeHighScores(scoresMap: Map<String, Int>): String {
    return scoresMap.entries.joinToString(";;") { "${it.key}::${it.value}" }
  }

  private fun deserializeHighScores(dataStr: String): Map<String, Int> {
    if (dataStr.isBlank()) return mapOf("bubble" to 80, "fruit" to 120, "memory" to 90)
    val map = mutableMapOf<String, Int>()
    try {
      val tokens = dataStr.split(";;")
      tokens.forEach { token ->
        val parts = token.split("::")
        if (parts.size >= 2) {
          val id = parts[0]
          val score = parts[1].toIntOrNull() ?: 0
          map[id] = score
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return map
  }

}

