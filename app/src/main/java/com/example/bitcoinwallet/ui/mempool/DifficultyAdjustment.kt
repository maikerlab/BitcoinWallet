package com.example.bitcoinwallet.ui.mempool

@kotlinx.serialization.Serializable
data class DifficultyAdjustment(
    val progressPercent: Float,
    val difficultyChange: Float,
    val estimatedRetargetDate: Double,
    val remainingBlocks: UInt,
    val remainingTime: Double,
    val previousRetarget: Float
    )