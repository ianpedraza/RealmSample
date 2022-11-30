package com.ianpedraza.realmsample.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ianpedraza.realmsample.data.Database
import com.ianpedraza.realmsample.data.Person
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainViewModel : ViewModel() {

    private val random = Random(System.currentTimeMillis())

    private val _people: MutableStateFlow<List<Person>> = MutableStateFlow(emptyList())
    val people: StateFlow<List<Person>> = _people

    init {
        listenPeople()
    }

    fun generatePerson() {
        viewModelScope.launch {
            val newPerson = Person().apply {
                name = random.nextLong().toString()
            }

            Database.insert(newPerson)
        }
    }

    private fun listenPeople() {
        viewModelScope.launch {
            Database.listenPeople().onEach { peopleList ->
                _people.value = peopleList
            }.launchIn(viewModelScope)
        }
    }
}
