package com.pelagohealth.codingchallenge.data.repository

import com.pelagohealth.codingchallenge.data.datasource.rest.FactsRestApi
import com.pelagohealth.codingchallenge.domain.model.Fact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Repository providing random facts.
 */
class FactRepository @Inject constructor(
    private val api: FactsRestApi
) {
    
    suspend fun get(): Fact {
        return withContext(Dispatchers.IO) { // TODO inject dispatchers
            api.getFact().let {
                Fact(
                    it.id,
                    it.text,
                    it.sourceUrl
                )
            }
        }
    }

}