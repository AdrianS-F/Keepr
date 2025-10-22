package com.example.keepr.seed

import android.content.Context
import com.example.keepr.data.CollectionEntity
import com.example.keepr.data.ItemEntity
import com.example.keepr.data.KeeprDatabase
import com.example.keepr.data.UserEntity

/**
 * Seeds a demo user + collections + items if the demo user is not found.
 * Call this from MainActivity.onCreate() inside a coroutine.
 */
suspend fun seedDemoIfNeeded(appContext: Context) {
    val db = KeeprDatabase.get(appContext)
    val usersDao = db.usersDao()
    val collectionsDao = db.collectionsDao()
    val itemsDao = db.itemsDao()

    // If the demo user already exists, do nothing (prevents duplicate seeding)
    val existing = usersDao.getByEmail("demo@keepr.app")
    if (existing != null) return

    // 1) Demo user
    val demoUserId = usersDao.insert(
        UserEntity(
            email = "demo@keepr.app",
            firstName = "Demo",
            lastName = "User",
            passwordHash = at.favre.lib.crypto.bcrypt.BCrypt
                .withDefaults()
                .hashToString(12, "demo123".toCharArray())

        )
    )

    // 2) Two demo collections
    val cardsId = collectionsDao.insert(
        CollectionEntity(userId = demoUserId, title = "Pokémon Cards", description = "Base set")
    )
    val coinsId = collectionsDao.insert(
        CollectionEntity(userId = demoUserId, title = "Euro Coins", description = "EU member states")
    )

    // 3) Some items
    itemsDao.insert(ItemEntity(collectionId = cardsId, itemName = "Charizard #4", acquired = false, notes = "Holo"))
    itemsDao.insert(ItemEntity(collectionId = cardsId, itemName = "Blastoise #2", acquired = true))
    itemsDao.insert(ItemEntity(collectionId = cardsId, itemName = "Venusaur #3", acquired = false))

    itemsDao.insert(ItemEntity(collectionId = coinsId, itemName = "€2 Germany 2002", acquired = true))
    itemsDao.insert(ItemEntity(collectionId = coinsId, itemName = "€1 France 2002", acquired = false))
}
