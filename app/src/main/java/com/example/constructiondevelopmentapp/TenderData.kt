package com.example.constructiondevelopmentapp

data class TenderData(
    val tenderTitle: String,
    val refNumber: String,
    val province: String,
    val ministry: String,
    val tenderValue: String,
    val industry: String,
    val sector: String,
    val publishedOn: String,
    val closingOn: String,
    val description: String,
    val tid : String
) {
    // Add a default (no-argument) constructor
    constructor() : this("", "", "", "", "", "", "", "", "", "", "")
}
