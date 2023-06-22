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
    fun deleteQA_returnsTrue() = runBlocking {
        val qa = QA(id = 1, "How are you", answer = "im' find thank you")
        qaDao.delete(qa)
        val getAllQa = qaDao.loadAllQa();
        assertEquals(getAllQa.size , 0);
    }

    @Test
    fun insertQA_returnsTrue() = runBlocking {
        val qa = QA(id = 1, "How are you", answer = "im' find thank you")
        qaDao.insertAll(qa)
        val getAllQa = qaDao.loadAllQa();
        assertEquals(getAllQa.size, 1);
    }

    @Test
    fun selectAllQA_returnsTrue() = runBlocking {
        val qa = QA(id = 1, "How are you", answer = "im' find thank you")
        val qa2 = QA(id = 2, "How are you", answer = "im' find thank you")
        qaDao.insertAll(qa)
        qaDao.insertAll(qa2)
        val getAllQa = qaDao.loadAllQa();
        assertEquals(getAllQa.size, 2);
    }

//    @Test
//    fun updateQA_returnsTrue() = runBlocking {
//        val qa = QA(id = 1, "How are you?", answer = "im' find thank you1")
//        qaDao.insertAll(qa)
//        val qa2 = QA(id = 1, "How are you?2222", answer = "im' find thank you1")
//        qaDao.updateQa(qa2)
//        val getQa = qaDao.getQaById(qa2.id);
//        assertEquals(getQa.question , "How are you?2222");
//    }

}