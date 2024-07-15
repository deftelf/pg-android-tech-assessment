package com.pelagohealth.codingchallenge.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pelagohealth.codingchallenge.data.repository.FactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val factsRepository: FactRepository,
) : ViewModel() {

    val fact: MutableStateFlow<PendingFact> = MutableStateFlow(PendingFact.Loading)
    val events: Channel<Event> = Channel()

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

    fun onClickedSource(url: String) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            viewModelScope.launch {
                events.send(Event.StartActivity(browserIntent))
            }
        } catch (e: Exception) {
            // TODO show error dialog
        }
    }

    sealed class Event {
        data class StartActivity(val intent: Intent) : Event()
    }
}