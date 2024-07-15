package com.pelagohealth.codingchallenge.presentation

import com.pelagohealth.codingchallenge.domain.model.Fact

sealed class PendingFact {
    data object Complete : PendingFact()
    data object Loading : PendingFact()
}