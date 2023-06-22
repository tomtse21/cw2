package com.mobile.cw2.uitl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.Flow

class QAViewModel(
    private val qaDao: QADao
): ViewModel() {
//    fun qaByParent(id: Int): QA = qaDao.getQaById(id)
}

class QAViewModelFactory(
    private val qaDao: QADao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QAViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QAViewModel(qaDao) as T
        }
        throw IllegalAccessException("Unknown ViewModel class")
    }
}