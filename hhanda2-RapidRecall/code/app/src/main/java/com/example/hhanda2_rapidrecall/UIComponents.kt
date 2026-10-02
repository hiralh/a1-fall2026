package com.example.hhanda2_rapidrecall

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun RapidRecallApp(game: Game, onExit: () -> Unit, modifier: Modifier = Modifier) {

    // https://www.geeksforgeeks.org/kotlin/kotlin-when-expression/
    // Pages - landing, setup, ready, showing, answering, result, log, summary
    var screen by remember { mutableStateOf("landing") }
    var shownDigit by remember { mutableStateOf("") }
    var userInput by remember { mutableStateOf("") }

    // Ready, Set, Go! page -> then digits one at a time -> then answer page
    LaunchedEffect(screen) {
        if (screen == "ready") {
            delay(1500.milliseconds)
            screen = "showing"
        } else if (screen == "showing") {
            delay(500.milliseconds)
            for (c in game.gen_seq) {
                shownDigit = c.toString()
                delay(800.milliseconds)
                shownDigit = ""
                delay(300.milliseconds)
            }
            screen = "answering"
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(20.dp)) {

        when (screen) {

            // landing page
            "landing" -> {
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Rapid Recall", fontSize = 40.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(80.dp))
                    Button(onClick = { screen = "setup" }, modifier = Modifier.width(250.dp).height(60.dp)){
                        Text("START GAME")
                    }
                    Spacer(Modifier.height(20.dp))
                    Button(onClick = { screen = "log" }, modifier = Modifier.width(250.dp).height(60.dp)){
                        Text("ATTEMPT LOG")
                    }
                    Spacer(Modifier.height(20.dp))
                    Button(onClick = { screen = "summary" }, modifier = Modifier.width(250.dp).height(60.dp)){
                        Text("ATTEMPT SUMMARY")
                    }
                }
                BottomRightButton("EXIT") { onExit() }
            }

            // choosing sequence length page
            "setup" -> {
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("${game.seq_len}", fontSize = 80.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(20.dp))
                    Row {
                        Button(onClick = { if (game.seq_len > 1) game.seq_len = game.seq_len - 1 }) {
                            Text("-")
                        }
                        Spacer(Modifier.width(24.dp))
                        Button(onClick = { if (game.seq_len < 10) game.seq_len = game.seq_len + 1 }) {
                            Text("+")
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    Button(onClick = {
                        game.startGame()
                        userInput = ""
                        shownDigit = ""
                        screen = "ready"
                    }) { Text("START") }
                }
                BottomRightButton("BACK") { screen = "landing" }
            }

            // ready, set, go! page
            "ready" -> {
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Ready, Set, Go!", fontSize = 36.sp, fontWeight = FontWeight.Bold)
                }
            }

            // showing digits one at a time
            "showing" -> {
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(shownDigit, fontSize = 120.sp, fontWeight = FontWeight.Bold)
                }
            }

            // user answer page
            "answering" -> {
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Enter the ${game.seq_len} digits", fontSize = 20.sp)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = userInput,
                        onValueChange = { new ->
                            if (new.length <= game.seq_len && new.all { it.isDigit() }) {
                                userInput = new
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(Modifier.height(16.dp))
                    // ENTER only appears once something has been typed
                    if (userInput.isNotEmpty()) {
                        Button(onClick = {
                            game.enterTry(userInput)
                            screen = "result"
                        }, modifier = Modifier.width(250.dp).height(60.dp)){
                            Text("ENTER")
                        }
                    }
                }
            }

            // result and feedback page
            "result" -> {
                val last = game.attempts.lastOrNull()
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (last != null) {
                        Text(last.result.uppercase(), fontSize = 44.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(24.dp))
                        Text("Generated: ${last.gen_seq}", fontSize = 22.sp)
                        Text("Your input: ${last.input}", fontSize = 22.sp)
                    }
                    Spacer(Modifier.height(40.dp))
                    Row {
                        Button(onClick = { screen = "landing" },modifier = Modifier.width(250.dp).height(60.dp)){
                            Text("HOME")
                        }
                        Spacer(Modifier.width(24.dp))
                        Button(onClick = { screen = "setup" }, modifier = Modifier.width(250.dp).height(60.dp)){
                            Text("PLAY AGAIN")
                        }
                    }
                }
            }

            // attempt log page
            "log" -> {
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                // BACK at the top right, above the column titles
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(onClick = { screen = "landing" }) { Text("BACK") }
                }
                Spacer(Modifier.height(12.dp))

                // Column titles
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("No.", Modifier.weight(0.6f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Length", Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Sequence", Modifier.weight(1.6f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Guess", Modifier.weight(1.6f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Result", Modifier.weight(1.3f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Timestamp", Modifier.weight(2.2f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(Modifier.height(8.dp))

                // Scrollable list of attempts
                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    itemsIndexed(game.attempts) { index, a ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                            Text("${index + 1}", Modifier.weight(0.6f), fontSize = 12.sp)
                            Text("${a.seq_len}", Modifier.weight(1f), fontSize = 12.sp)
                            Text(a.gen_seq, Modifier.weight(1.6f), fontSize = 12.sp)
                            Text(a.input, Modifier.weight(1.6f), fontSize = 12.sp)
                            Text(a.result, Modifier.weight(1.3f), fontSize = 12.sp)
                            Text(format.format(a.timestamp), Modifier.weight(2.2f), fontSize = 12.sp)
                        }
                    }
                }
            }

            // attempt summary page
            "summary" -> {
                val summary = game.summarize()
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("SUMMARY", fontSize = 40.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(100.dp))
                    Text("Total Attempts: ${summary.total_attempts}", fontSize = 22.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Text("Total Correct Attempts: ${summary.total_correct}", fontSize = 22.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Text("Accuracy: ${String.format(Locale.US, "%.1f", summary.accuracy)}%", fontSize = 22.sp, textAlign = TextAlign.Center)
                }
                BottomRightButton("BACK") { screen = "landing" }
            }
        }
    }
}

@Composable
fun BottomRightButton(label: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Button(onClick = onClick, modifier = Modifier.width(100.dp).height(60.dp)) { Text(label) }
    }
}