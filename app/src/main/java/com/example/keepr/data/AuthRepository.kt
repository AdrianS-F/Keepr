package com.example.keepr.data

import at.favre.lib.crypto.bcrypt.BCrypt

class AuthRepository(private val db: KeeprDatabase) {

    private val usersDao: UsersDao = db.usersDao()
    private val collectionsDao = db.collectionsDao()
    private val profileDao = db.profileDao()

    suspend fun signUp(
        email: String,
        firstName: String,
        lastName: String,
        rawPassword: String
    ): Result<Long> {
        val e  = email.trim().lowercase()
        val fn = firstName.trim()
        val ln = lastName.trim()

        if (usersDao.emailExists(e)) {
            return Result.failure(IllegalStateException("E-post er allerede i bruk."))
        }

        val hash = BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray())


        val userId = usersDao.insert(
            UserEntity(
                email        = e,
                firstName    = fn,
                lastName     = ln,
                passwordHash = hash
            )
        )

        profileDao.upsert(
            ProfileEntity(
                userId    = userId,
                firstName = fn,
                lastName  = ln,
                avatarUri = null
            )
        )

        return Result.success(userId)
    }

    suspend fun signIn(
        email: String,
        rawPassword: String
    ): Result<UserEntity> {
        val e = email.trim().lowercase()
        val user = usersDao.getByEmail(e)
            ?: return Result.failure(IllegalArgumentException("Feil e-post eller passord."))

        val ok = BCrypt.verifyer().verify(rawPassword.toCharArray(), user.passwordHash).verified
        return if (ok) Result.success(user)
        else Result.failure(IllegalArgumentException("Feil e-post eller passord."))
    }

    suspend fun checkPassword(email: String, rawPassword: String): Boolean {
        val e = email.trim().lowercase()
        val user = usersDao.getByEmail(e) ?: return false
        return BCrypt.verifyer()
            .verify(rawPassword.toCharArray(), user.passwordHash).verified
    }

    @androidx.room.Transaction
    suspend fun deleteUserAndData(userId: Long) {
        collectionsDao.deleteByUser(userId)
        profileDao.deleteByUser(userId)
        usersDao.deleteById(userId)
    }
}
