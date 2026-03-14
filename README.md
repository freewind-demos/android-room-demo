# android-room-demo

## 简介

本 demo 展示 Android 中 Room 数据库的基本用法。Room 是 Android 官方推荐的 SQLite 抽象层，提供了更简洁的 API 和编译时检查。

## 基本原理

Room 是 Android 架构组件的一部分，提供了一层 SQLite 抽象，让你可以更方便地访问 SQLite 数据库，同时享受编译时检查的便利。

Room 的三个核心组件：
1. **Entity（实体）**：对应数据库中的表
2. **DAO（数据访问对象）**：定义访问数据库的方法
3. **Database（数据库）**：管理数据库实例和版本

## 启动和使用

### 环境要求
- Android Studio 3.0+
- JDK 1.8+
- Android SDK 28

### 安装和运行
1. 用 Android Studio 打开此项目
2. 连接 Android 设备或启动模拟器
3. 点击 Run 运行项目

## 教程

### 什么是 Room？

Room 是 Google 官方提供的 SQLite 抽象库，它是 Android Jetpack 的一部分。相比于直接使用 SQLite，Room 提供了：

- 编译时 SQL 检查
- 注解处理器自动生成代码
- 更简洁的 API
- 更好的与架构组件集成

### Entity（实体）

实体类对应数据库中的表：

```kotlin
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "age")
    val age: Int
)
```

常用注解：
- @Entity：标记类为实体
- @PrimaryKey：标记主键
- @ColumnInfo：指定列信息
- @Ignore：忽略字段

### DAO（数据访问对象）

DAO 是定义数据库操作方法的接口：

```kotlin
@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): List<User>

    @Insert
    fun insert(user: User): Long

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)
}
```

常用注解：
- @Query：自定义 SQL 查询
- @Insert：插入数据
- @Update：更新数据
- @Delete：删除数据

### Database（数据库）

数据库类管理数据库实例：

```kotlin
@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

// 创建数据库
val database = Room.databaseBuilder(
    applicationContext,
    AppDatabase::class.java,
    "my_database"
).build()
```

### 基本操作

**插入数据：**
```kotlin
val user = User(name = "张三", age = 25)
val id = userDao.insert(user)
```

**查询数据：**
```kotlin
val users = userDao.getAllUsers()
```

**更新数据：**
```kotlin
val updatedUser = user.copy(age = 35)
userDao.update(updatedUser)
```

**删除数据：**
```kotlin
userDao.delete(user)
```

### 注意事项

1. **线程安全**：Room 操作应该在后台线程执行，不能在主线程进行耗时操作
2. **Kotlin 协程**：推荐使用 Kotlin 协程 + LiveData/Flow 来处理异步操作
3. **迁移**：数据库升级时需要处理迁移，可以使用 fallbackToDestructiveMigration() 简单处理
4. **注解处理器**：Room 使用注解处理器，需要在 build.gradle 中添加 kapt 插件
