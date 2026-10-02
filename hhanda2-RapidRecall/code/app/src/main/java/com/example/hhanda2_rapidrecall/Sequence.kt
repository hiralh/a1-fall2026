package com.example.hhanda2_rapidrecall

class Sequence (private val seq_len: Int){

    fun generateSequence(): Int
    {
        if(seq_len == 5){
            return (10000..99999).random()
        }
        else if(seq_len == 7){
            return (1000000..9999999).random()
        }
        else{
            return (100000000..999999999).random()
        }
    }
}