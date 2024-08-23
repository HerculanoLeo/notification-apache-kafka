package com.herculanoleo.ak.notification.extentions

fun List<String>.inSQL() = this.joinToString(", ") { "'$it'".uppercase() }


