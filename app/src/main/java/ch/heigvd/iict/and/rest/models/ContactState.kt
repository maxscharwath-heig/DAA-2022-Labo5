package ch.heigvd.iict.and.rest.models

enum class ContactState {
    SYNCED, // contact is synced with the server
    UPDATED, // contact has been updated locally
    CREATED, // contact has been created locally
    DELETED // contact has been deleted locally
}