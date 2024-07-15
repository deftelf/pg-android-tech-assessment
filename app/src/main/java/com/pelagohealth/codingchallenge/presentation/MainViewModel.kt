package com.pelagohealth.codingchallenge.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pelagohealth.codingchallenge.data.repository.FactRepository
import com.pelagohealth.codingchallenge.domain.model.Fact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val factsRepository: FactRepository
) : ViewModel() {

    val fact: MutableStateFlow<PendingFact> = MutableStateFlow(PendingFact.Loading)

    init {
        fetchNewFact()
    }

    fun fetchNewFact() {
        fact.value = PendingFact.Loading
        viewModelScope.launch {
            try {
                val loadedFact = factsRepository.get()
                fact.value = PendingFact.Complete(loadedFact)
            } catch (e: Exception) {
                fact.value = PendingFact.Failed
            }
        }
    }
}