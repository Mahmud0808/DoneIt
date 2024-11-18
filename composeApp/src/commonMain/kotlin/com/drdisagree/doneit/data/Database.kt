package com.drdisagree.doneit.data

import com.drdisagree.doneit.domain.RequestState
import com.drdisagree.doneit.domain.ToDoTask
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Database {

    private var realm: Realm? = null

    init {
        configureRealm()
    }

    private fun configureRealm() {
        if (realm == null || realm!!.isClosed()) {
            val configuration = RealmConfiguration.Builder(schema = setOf(ToDoTask::class))
                .compactOnLaunch()
                .build()
            realm = Realm.open(configuration)
        }
    }

    private val realmDb: Realm
        get() = realm ?: throw Exception("Realm is not configured.")

    fun readActiveTasks(): Flow<RequestState<List<ToDoTask>>> {
        return realmDb.query<ToDoTask>(query = "completed == $0", false)
            .asFlow()
            .map { result ->
                RequestState.Success(
                    data = result.list.sortedByDescending { task -> task.favorite }
                )
            }
    }

    fun readCompletedTasks(): Flow<RequestState<List<ToDoTask>>> {
        return realmDb.query<ToDoTask>(query = "completed == $0", true)
            .asFlow()
            .map { result ->
                RequestState.Success(data = result.list)
            }
    }

    suspend fun addTask(task: ToDoTask) {
        realmDb.write {
            copyToRealm(task)
        }
    }

    suspend fun updateTask(task: ToDoTask) {
        realmDb.write {
            try {
                query<ToDoTask>("_id == $0", task._id)
                    .first()
                    .find()
                    ?.let { toUpdate ->
                        findLatest(toUpdate)?.apply {
                            title = task.title
                            description = task.description
                        }
                    }
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    suspend fun setCompleted(task: ToDoTask, taskCompleted: Boolean) {
        realmDb.write {
            try {
                query<ToDoTask>("_id == $0", task._id)
                    .first()
                    .find()
                    ?.let { toUpdate ->
                        findLatest(toUpdate)?.apply {
                            completed = taskCompleted
                        }
                    }
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    suspend fun setFavorite(task: ToDoTask, isFavorite: Boolean) {
        realmDb.write {
            try {
                query<ToDoTask>("_id == $0", task._id)
                    .first()
                    .find()
                    ?.let { toUpdate ->
                        findLatest(toUpdate)?.apply {
                            favorite = isFavorite
                        }
                    }
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    suspend fun deleteTask(task: ToDoTask) {
        realmDb.write {
            try {
                query<ToDoTask>("_id == $0", task._id)
                    .first()
                    .find()
                    ?.let { toUpdate ->
                        findLatest(toUpdate)?.apply {
                            delete(this)
                        }
                    }
            } catch (e: Exception) {
                println(e)
            }
        }
    }
}