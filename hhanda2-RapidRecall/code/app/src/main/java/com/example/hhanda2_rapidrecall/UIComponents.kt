package com.example.hhanda2_rapidrecall

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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

    // Pages - la8nding, setup, ready, showing, answering, result, log, summary
    var screen by remember { mutableStateOf("landing") }
    var shownDigit by remember { mutableStateOf("") }
    var userInput by remember { mutableStateOf("") }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val image1 = painterResource(R.drawable.lpbg)
        // bg image
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = image1,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.2f
        )

        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            // landing page
            if (screen == "landing") {
                Column(modifier = Modifier.weight(1f).fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Rapid Recall", fontSize = 40.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(80.dp))
                    Button(
                        onClick = { screen = "setup" },
                        modifier = Modifier.width(250.dp).height(60.dp)
                    ) {
                        Text("START GAME")
                    }
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = { screen = "log" },
                        modifier = Modifier.width(250.dp).height(60.dp)
                    ) {
                        Text("ATTEMPT LOG")
                    }
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = { screen = "summary" },
                        modifier = Modifier.width(250.dp).height(60.dp)
                    ) {
                        Text("ATTEMPT SUMMARY")
                    }
                }
                BottomRightButton("EXIT") { onExit() }
            }

            // choosing sequence length page
            if (screen == "setup") {
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
            if (screen == "ready") {
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Ready, Set, Go!", fontSize = 36.sp, fontWeight = FontWeight.Bold)
                }
            }

            // delays between pages/actions
            // to understand how I can show one digit at a time on one page with delayes in between, I used Gemini AI with the prompt "How do I show one character on one page at a time with delays in between. In Kotlin". https://gemini.google.com/app/e50c974b87e76080?hl=en-CA
            LaunchedEffect(screen) {
                if (screen == "ready") {
                    delay(1500.milliseconds)
                    screen = "showing"
                } else if (screen == "showing") {
                    delay(500.milliseconds)
                    for (i in game.gen_seq) {
                        shownDigit = i.toString()
                        delay(800.milliseconds)
                        shownDigit = ""
                        delay(300.milliseconds)
                    }
                    screen = "answering"
                }
            }

            // showing digits one at a time
            if (screen == "showing") {
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(shownDigit, fontSize = 120.sp, fontWeight = FontWeight.Bold)
                }
            }

            // user answer page
            if (screen == "answering") {
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
                        }, modifier = Modifier.width(250.dp).height(60.dp)) {
                            Text("ENTER")
                        }
                    }
                }
            }

            // result and feedback page
            if (screen == "result") {
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
                        Button(
                            onClick = { screen = "landing" },
                            modifier = Modifier.width(150.dp).height(60.dp)
                        ) {
                            Text("HOME")
                        }
                        Spacer(Modifier.width(24.dp))
                        Button(
                            onClick = { screen = "setup" },
                            modifier = Modifier.width(150.dp).height(60.dp)
                        ) {
                            Text("PLAY AGAIN")
                        }
                    }
                }
            }

            // attempt log page
            if (screen == "log") {
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                // Column titles
                Row(modifier = Modifier.fillMaxWidth().padding(start = 5.dp, top = 8.dp, end = 5.dp, bottom = 8.dp)){
                    Text("No.", Modifier.weight(0.6f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Length", Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Sequence", Modifier.weight(1.6f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Guess", Modifier.weight(1.6f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Result", Modifier.weight(1.3f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Timestamp", Modifier.weight(2.2f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(Modifier.height(4.dp))
                // Scrollable list of attempts
                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth().padding(15.dp)){
                    // https://www.geeksforgeeks.org/kotlin/lazy-composables-in-android-jetpack-compose-columns-rows-grids/
                    itemsIndexed(game.attempts) { index, a ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                            Text("${index + 1}", Modifier.weight(0.6f), fontSize = 15.sp)
                            Text("${a.seq_len}", Modifier.weight(1f), fontSize = 15.sp)
                            Text(a.gen_seq, Modifier.weight(1.6f), fontSize = 15.sp)
                            Text(a.input, Modifier.weight(1.6f), fontSize = 15.sp)
                            Text(a.result, Modifier.weight(1.3f), fontSize = 15.sp)
                            Text(format.format(a.timestamp), Modifier.weight(1.6f), fontSize = 13.sp)
                        }
                    }
                }
                BottomRightButton("BACK") { screen = "landing" }
            }

            // attempt summary page
            if (screen == "summary") {
                val summary = game.summarize()
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("SUMMARY", fontSize = 40.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(100.dp))
                    Text(
                        "Total Attempts: ${summary.total_attempts}",
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Total Correct Attempts: ${summary.total_correct}",
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Accuracy: ${String.format(Locale.US, "%.1f", summary.accuracy)}%",
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )
                }
                BottomRightButton("BACK") { screen = "landing" }
            }
        }
    }
}

@Composable
fun BottomRightButton(label: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.End) {
        Button(onClick = onClick, modifier = Modifier.width(100.dp).height(60.dp)) { Text(label) }
    }
}