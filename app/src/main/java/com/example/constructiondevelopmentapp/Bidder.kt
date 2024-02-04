package com.example.constructiondevelopmentapp

class Bidder (val name: String,
              val amount: String,
              val bid : String)
{
    // Add a default (no-argument) constructor
    constructor() : this("", "","")
}