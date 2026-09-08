package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.local.entity.ReportEntity
import com.example.data.local.entity.ReportHistoryEntity
import com.example.data.local.entity.SystemSettingsEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Query("SELECT * FROM reports ORDER BY createdAt DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE status = :status ORDER BY createdAt DESC")
    fun getReportsByStatus(status: String): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE code = :code LIMIT 1")
    suspend fun getReportByCode(code: String): ReportEntity?

    @Query("SELECT * FROM reports WHERE code = :code LIMIT 1")
    fun observeReportByCode(code: String): Flow<ReportEntity?>

    @Query("SELECT EXISTS(SELECT 1 FROM reports WHERE code = :code)")
    suspend fun existsByCode(code: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Update
    suspend fun updateReport(report: ReportEntity)

    @Query("DELETE FROM reports")
    suspend fun deleteAllReports()

    @Query("SELECT COUNT(*) FROM reports")
    fun getReportCount(): Flow<Int>

    @Query("SELECT * FROM reports WHERE neighborhood LIKE '%' || :query || '%' OR code LIKE '%' || :query || '%' OR reference LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchReports(query: String): Flow<List<ReportEntity>>
}

@Dao
interface ReportHistoryDao {
    @Query("SELECT * FROM report_history WHERE reportCode = :reportCode ORDER BY changedAt DESC")
    fun getHistoryForReport(reportCode: String): Flow<List<ReportHistoryEntity>>

    @Query("SELECT * FROM report_history WHERE reportCode = :reportCode ORDER BY changedAt DESC")
    suspend fun getHistoryListForReport(reportCode: String): List<ReportHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: ReportHistoryEntity)

    @Query("DELETE FROM report_history")
    suspend fun deleteAllHistory()
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: Long)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}

@Dao
interface SystemSettingsDao {
    @Query("SELECT * FROM system_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<SystemSettingsEntity?>

    @Query("SELECT * FROM system_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsSync(): SystemSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: SystemSettingsEntity)
}
