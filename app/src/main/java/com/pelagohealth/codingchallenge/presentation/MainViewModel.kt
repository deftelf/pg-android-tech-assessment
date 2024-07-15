package com.pelagohealth.codingchallenge.presentation

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pelagohealth.codingchallenge.data.repository.FactRepository
import com.pelagohealth.codingchallenge.domain.model.Fact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val factsRepository: FactRepository,
) : ViewModel() {

    val facts: MutableStateFlow<List<Fact>> = MutableStateFlow(emptyList())
    val incomingFactStatus: MutableStateFlow<PendingFact> = MutableStateFlow(PendingFact.Loading)
    private var factFetchJob: Job? = null
    val events: Channel<Event> = Channel()

    init {
        fetchNewFact()
    }

    fun fetchNewFact() {
        factFetchJob?.cancel()
        incomingFactStatus.value = PendingFact.Loading
        factFetchJob = viewModelScope.launch {
            try {
                add(factsRepository.get())
                incomingFactStatus.value = PendingFact.Complete
            } catch (e: Exception) {
                events.send(Event.FailedFetch)
                incomingFactStatus.value = PendingFact.Complete
            }
        }
    }

    private fun add(fact: Fact) {
        var changedList = facts.value
        if (changedList.size >= MAX_FACT_LIST_COUNT) {
            changedList = changedList.takeLast(MAX_FACT_LIST_COUNT - 1)
        }
        changedList += listOf(fact)
        facts.value = changedList
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

    fun onRemove(fact: Fact) {
        facts.value -= fact
    }

    sealed class Event {
        data class StartActivity(val intent: Intent) : Event()
        data object FailedFetch : Event()
    }

    companion object {
        private const val MAX_FACT_LIST_COUNT = 3
    }
}