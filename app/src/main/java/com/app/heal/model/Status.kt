package com.app.heal.model

enum class Status {
    Valid,
    Invalid,
    InProgress,
    Issue,
    NotUploaded
}

enum class MedStatus {
    Upcoming,
    Missed,
    Taken,
    NotTaken
}

enum class FlagStatus {
    Open,
    Closed,
    InProgress,
    None
}