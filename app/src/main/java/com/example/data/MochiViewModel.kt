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
          updateNotificationForPet(processedPet)
        } else {
          val defaultPet = PetEntity()
          dao.updatePetStats(defaultPet)
          _petState.value = defaultPet
          _currentMood.value = calculateMood(defaultPet, false)
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
        triggerFloatingPopup("+25 Hunger 🍎", 0xFFF97316)
        triggerFloatingPopup("+5 Friendship 💕", 0xFFE11D48)
        showTemporaryNotification("Yum! Mochi loved the meal! 🍎")
      }
      CareActionType.WATER -> {
        hunger = (hunger + 15f).coerceAtMost(100f)
        energy = (energy + 10f).coerceAtMost(100f)
        com.example.util.SoundManager.playDrinkingSound()
        triggerFloatingPopup("+15 Water 💧", 0xFF3B82F6)
        triggerFloatingPopup("+10 Energy ⚡", 0xFFEAB308)
        showTemporaryNotification("Gulp gulp! Refreshing water! 💧")
      }
      CareActionType.SLEEP -> {
        toggleSleep()
        return
      }
      CareActionType.WAKE -> {
        toggleSleep()
        return
      }
      CareActionType.BATH -> {
        clean = (clean + 35f).coerceAtMost(100f)
        joy = (joy + 10f).coerceAtMost(100f)
        com.example.util.SoundManager.playBathSplashSound()
        triggerFloatingPopup("+35 Cleanliness 🧼", 0xFF06B6D4)
        showTemporaryNotification("Splish splash! All clean & shiny! 🛁✨")
      }
      CareActionType.BRUSH -> {
        clean = (clean + 20f).coerceAtMost(100f)
        friendship = (friendship + 10f).coerceAtMost(100f)
        com.example.util.SoundManager.playBrushSound()
        triggerFloatingPopup("+20 Cleanliness 🪮", 0xFF06B6D4)
        triggerFloatingPopup("+10 Friendship 💕", 0xFFE11D48)
        showTemporaryNotification("Mochi's fur is so soft and fluffy! 🪮✨")
      }
      CareActionType.MEDICINE -> {
        health = (health + 35f).coerceAtMost(100f)
        energy = (energy + 15f).coerceAtMost(100f)
        com.example.util.SoundManager.playMedicineSound()
        triggerFloatingPopup("+35 Health 🧪", 0xFF10B981)
        showTemporaryNotification("Healing vitamin elixir taken! 🧪✨")
      }
      CareActionType.PET_HUG -> {
        friendship = (friendship + 20f).coerceAtMost(100f)
        joy = (joy + 15f).coerceAtMost(100f)
        com.example.util.SoundManager.playCuddleSound()
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

    if (newXp >= newMaxXp) {
      newXp -= newMaxXp
      newLevel += 1
      newMaxXp = (newMaxXp * 1.25f).toInt()
      showTemporaryNotification("LEVEL UP! Reached Level $newLevel! 🌟")
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

}
