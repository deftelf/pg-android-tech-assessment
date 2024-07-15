package com.pelagohealth.codingchallenge.presentation

import com.pelagohealth.codingchallenge.domain.model.Fact

sealed class PendingFact {
    data class Complete(val fact: Fact) : PendingFact()
    data object Loading : PendingFact()
    data object Failed : PendingFact()
}