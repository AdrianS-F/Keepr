package com.example.keepr.data

import at.favre.lib.crypto.bcrypt.BCrypt

/**
 * Enkel repo for signup/signin mot Room via UsersDao.
 * Bruker BCrypt for hashing og Result for enkel feilvisning i UI.
 */
class AuthRepository(private val db: KeeprDatabase) {

    private val usersDao: UsersDao = db.usersDao()

    suspend fun signUp(
        email: String,
        firstName: String,
        lastName: String,
        rawPassword: String
    ): Result<Long> {
        val e = email.trim().lowercase()
        val fn = firstName.trim()
        val ln = lastName.trim()

        if (usersDao.emailExists(e)) {
            return Result.failure(IllegalStateException("E-post er allerede i bruk."))
        }

        val hash = BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray())
        val userId = usersDao.insert(
            UserEntity(
                email = e,
                firstName = fn,
                lastName = ln,
                passwordHash = hash
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
}
