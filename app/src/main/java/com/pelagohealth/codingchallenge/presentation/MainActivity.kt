@file:OptIn(ExperimentalMaterial3Api::class)

package com.pelagohealth.codingchallenge

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pelagohealth.codingchallenge.domain.model.Fact
import com.pelagohealth.codingchallenge.presentation.MainViewModel
import com.pelagohealth.codingchallenge.presentation.PendingFact
import com.pelagohealth.codingchallenge.ui.theme.PelagoCodingChallengeTheme
import com.pelagohealth.codingchallenge.ui.theme.Purple80
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.receiveAsFlow().collect {
                    when (it) {
                        is MainViewModel.Event.StartActivity -> {
                            startActivity(it.intent)
                        }

                        MainViewModel.Event.FailedFetch -> {
                            Toast.makeText(
                                this@MainActivity,
                                "Failed to fetch a fact",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
        setContent {
            PelagoCodingChallengeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val viewModel: MainViewModel = viewModel()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Hello!",
            modifier = modifier
        )
        Spacer(modifier = Modifier.height(16.dp))
        val loadingState = viewModel.incomingFactStatus.collectAsState().value
        Button(
            onClick = { viewModel.fetchNewFact() },
            enabled = loadingState !is PendingFact.Loading
        ) {
            Box {
                Text("More facts!")
                if (loadingState is PendingFact.Loading) {
                    CircularProgressIndicator(
                        Modifier
                            .size(16.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        val facts = viewModel.facts.collectAsState().value
        LazyColumn(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(facts.size, key = { facts[it].id }) { index ->
                val fact = facts[index]
                val currentItem by rememberUpdatedState(fact)
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        when (it) {
                            SwipeToDismissBoxValue.StartToEnd, SwipeToDismissBoxValue.EndToStart -> {
                                viewModel.onRemove(currentItem)
                            }
                            SwipeToDismissBoxValue.Settled -> return@rememberSwipeToDismissBoxState false
                        }
                        true
                    },
                    positionalThreshold = { it * .5f }
                )
                SwipeToDismissBox(state = dismissState, backgroundContent = {}) {
                    FactItem(fact, viewModel, Modifier.animateItemPlacement())
                }
            }
        }
    }
}

@Composable
fun FactItem(fact: Fact, viewModel: MainViewModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = fact.text)
        Spacer(modifier = Modifier.height(8.dp))
        ClickableText(text = buildAnnotatedString {
            pushStringAnnotation("url", fact.url)
            withStyle(
                style = SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    color = Purple80
                )
            ) {
                append("Source")
            }
            pop()
        }, onClick = { viewModel.onClickedSource(fact.url) },
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PelagoCodingChallengeTheme {
        MainScreen()
    }
}