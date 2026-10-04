package com.example.hhanda2_rapidrecall

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import java.sql.Timestamp
import kotlin.random.Random

/*This is the class Game. It is responsible for the majority of the code and it has all the functions to handle the main flow of the code.
It has a collection of Attempt objects which can be used for logs and summaries.
It has a function to generate the sequence that needs to be guessed.
It has a function to compare the user input with the generated sequence, give a result and add the Attempt object to the attempts collection.
And it has a function to create the summary of the attempts.*/
class Game {

    private val _attempts = mutableStateListOf<Attempt>()
    val attempts: List<Attempt>
        get() = _attempts

    var gen_seq by mutableStateOf("")  // For generated sequence
    var input by mutableStateOf("")  // For user input
    var seq_len by mutableIntStateOf(1)  // For sequence length
    var result by mutableStateOf("")  // For result of user input (Correct or Incorrect)
    var timestamp by mutableStateOf(Timestamp(System.currentTimeMillis()))  // For current timestamp - https://developer.android.com/reference/java/sql/Timestamp

    fun startGame(){
        // Generates a sequence of user=-inputted seq_len
        gen_seq = ""  // Putting sequence as a String prevents leading zeroes from being omitted
        for (i in 1..seq_len){
            val digit = Random.nextInt(0,10)
            gen_seq += digit
        }
        input = ""
        result = ""
    }

    fun enterTry(input: String): Attempt{
        // Accepts user input. Compares with generated sequence. Creates Attempt object and adds it to the collection. Returns the attempt object.
        this.input = input
        timestamp = Timestamp(System.currentTimeMillis())
        if(this.input == gen_seq)
            result = "Correct"
        else
            result = "Incorrect"
        val attempt = Attempt(seq_len, this.input, gen_seq, result, timestamp)
        _attempts.add(attempt)
        return attempt
    }

    fun summarize(): Summary{
        // Creates summary of attempts and then returns a Summary object
        val total_attempts = _attempts.size
        var total_correct = 0
        for (attempt in _attempts) {
            if (attempt.result == "Correct") {
                total_correct++
            }
        }
        val accuracy = if (total_attempts == 0) 0.0 else (total_correct * 100.0) / total_attempts
        return Summary(total_attempts, total_correct, accuracy)
    }
}