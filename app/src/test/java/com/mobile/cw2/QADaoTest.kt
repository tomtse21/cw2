package com.mobile.cw2

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mobile.cw2.uitl.AppDatabase
import com.mobile.cw2.uitl.QA
import com.mobile.cw2.uitl.QADao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class QADaoTest {
    lateinit var database: AppDatabase
    lateinit var qaDao: QADao

    @Before
    fun setUp(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        qaDao = database.qaDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertQA_returnsTrue() = runBlocking {
        val qa = QA(id = 1, "How are you", answer = "im' find thank you")
        assertEquals( qaDao.insertAll(qa), true);

    }

    @Test
    fun deleteQA_returnsTrue() = runBlocking {
        val qa = QA(id = 1, "How are you", answer = "im' find thank you")
        assertEquals( qaDao.delete(qa), true);
    }

    @Test
    fun updateQA_returnsTrue() = runBlocking {
        val qa = QA(id = 1, "How are you?", answer = "im' find thank you1")
        assertEquals( qaDao.updateQa(qa), true);
    }
}