package com.example.hhanda2_rapidrecall

import java.sql.Timestamp

/*This class is called Attempt. It is responsible for storing the required information of an attempt so that it can be displayed as logs and used for summaries.
Attempt objects will be created in the Game class to do so.*/
data class Attempt (
    private val seq_len: Int,
    private val input: Int,
    private val gen_seq: Int,
    private val result: String,
    private val timestamp: Timestamp
)