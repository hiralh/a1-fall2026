package com.example.hhanda2_rapidrecall

import java.sql.Timestamp

/*Here are two classes, Attempt and Summary. It is responsible for storing the required information of an attempt so that it can be displayed as logs and used for summaries.
It also stores required information for a summary. Since, I only wanted these to be classes which I can instantiate and use in other classes, and I didn't them to have any functions of their own,
I made them data classes like how we made City a data class in Lab-03.*/
data class Attempt (
    val seq_len: Int,
    val input: String,
    val gen_seq: String,
    val result: String,
    val timestamp: Timestamp  // https://developer.android.com/reference/java/sql/Timestamp
)
data class Summary(
    val total_attempts: Int,
    val total_correct: Int,
    val accuracy: Double
)