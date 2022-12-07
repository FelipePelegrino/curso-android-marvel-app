package com.example.testing.model

import com.example.core.domain.model.Character

class CharacterFactory {

    fun create(hero: Hero) = when (hero) {
        is Hero.ThreeD -> Character(
            name = "3-D Man",
            imageUrl = "https://i.annihil.us/u/prod/marvel/i/mg/c/e0/535fecbbb9784.jpg"
        )
        is Hero.ABomb -> Character(
            name = "A-Bomb (HAS)",
            imageUrl = "https://i.annihil.us/u/prod/marvel/i/mg/3/20/5232158de5b16.jpg"
        )
    }

    sealed class Hero {
        object ThreeD : Hero()
        object ABomb : Hero()
    }
}