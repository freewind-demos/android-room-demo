package com.example.demo

import android.arch.persistence.room.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

// 实体类 - 对应数据库表
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "age")
    val age: Int
)

// DAO 接口 - 数据访问对象
@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsers(): List<User>

    @Query("SELECT * FROM users WHERE name = :name")
    fun getUserByName(name: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User): Long

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)

    @Query("DELETE FROM users")
    fun deleteAll()
}

// 数据库类
@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

class MainActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var textViewResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化数据库
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "my_database"
        ).build()

        userDao = database.userDao()
        textViewResult = findViewById(R.id.textViewResult)

        // 插入数据按钮
        findViewById<Button>(R.id.buttonInsert).setOnClickListener {
            insertData()
        }

        // 查询数据按钮
        findViewById<Button>(R.id.buttonQuery).setOnClickListener {
            queryData()
        }

        // 更新数据按钮
        findViewById<Button>(R.id.buttonUpdate).setOnClickListener {
            updateData()
        }

        // 删除数据按钮
        findViewById<Button>(R.id.buttonDelete).setOnClickListener {
            deleteData()
        }
    }

    private fun insertData() {
        // 插入数据
        val user1 = User(name = "张三", age = 25)
        val user2 = User(name = "李四", age = 30)

        // Room 操作是同步的，实际项目中应在后台线程执行
        val id1 = userDao.insert(user1)
        val id2 = userDao.insert(user2)

        textViewResult.text = "插入成功! ID: $id1, $id2"
    }

    private fun queryData() {
        // 查询所有用户
        val users = userDao.getAllUsers()

        val sb = StringBuilder()
        for (user in users) {
            sb.append("ID: ${user.id}, 姓名: ${user.name}, 年龄: ${user.age}\n")
        }

        textViewResult.text = if (sb.isEmpty()) "没有数据" else sb.toString()
    }

    private fun updateData() {
        // 查询李四
        val user = userDao.getUserByName("李四")

        if (user != null) {
            // 更新年龄
            val updatedUser = user.copy(age = 35)
            userDao.update(updatedUser)
            textViewResult.text = "更新成功! ${user.name} 的年龄改为 35"
        } else {
            textViewResult.text = "找不到李四"
        }
    }

    private fun deleteData() {
        // 查询张三并删除
        val user = userDao.getUserByName("张三")

        if (user != null) {
            userDao.delete(user)
            textViewResult.text = "删除成功! 删除了 ${user.name}"
        } else {
            textViewResult.text = "找不到张三"
        }
    }
}
