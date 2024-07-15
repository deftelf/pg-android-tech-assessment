package com.pelagohealth.codingchallenge.presentation

sealed class PendingFact {
    data object Complete : PendingFact()
    data object Loading : PendingFact()
}