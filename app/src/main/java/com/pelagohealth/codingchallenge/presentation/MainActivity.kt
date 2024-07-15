package com.pelagohealth.codingchallenge

import android.os.Bundle
import android.provider.CalendarContract.Colors
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.text.style.TextDecoration
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pelagohealth.codingchallenge.presentation.MainViewModel
import com.pelagohealth.codingchallenge.presentation.PendingFact
import com.pelagohealth.codingchallenge.ui.theme.PelagoCodingChallengeTheme
import com.pelagohealth.codingchallenge.ui.theme.Purple80
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

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

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val viewModel: MainViewModel = viewModel()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hello!",
            modifier = modifier
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.fetchNewFact() }) {
            Text("More facts!")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier.padding(8.dp)) {
            when (val pendingFact = viewModel.fact.collectAsState().value) {
                is PendingFact.Loading -> {
                    CircularProgressIndicator()
                }

                is PendingFact.Complete -> {
                    Text(text = pendingFact.fact.text)
                    Spacer(modifier = Modifier.height(8.dp))
                    ClickableText(text = buildAnnotatedString {
                        pushStringAnnotation("url", pendingFact.fact.url)
                        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline, color = Purple80)) {
                            append("Source")
                        }
                        pop()
                    }, onClick = { viewModel.onClickedSource(pendingFact.fact.url) },
                        modifier = Modifier.align(Alignment.End))
                }

                PendingFact.Failed -> {
                    Text(text = "Fact load failed")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PelagoCodingChallengeTheme {
        MainScreen()
    }
}