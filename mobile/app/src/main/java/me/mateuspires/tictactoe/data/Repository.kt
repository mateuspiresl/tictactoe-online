package me.mateuspires.tictactoe.data

interface Repository<T> {

    fun load(): T

    fun save(data: T)
}
