# Jordle
## Introduction
Jordle is a simple game created with JavaFX that resembles the popular word guessing game "Wordle".
In Jordle, player will have 6 attempts to guess the mystery word. This five-letter word is related to Java concepts such as "class" or "final".

Jordle was created as a homework for CS1331 - Introduction to Object-Oriented Programming, which was offered at Georgia Tech.

## Instructions
Player will type their guess and hit enter to submit their guesses. They can use backspace to delete letters as they type. 
There is a restart button where player can click on to restart the game.

The rules of the game follows "Wordle": 
* A correct letter turns green
* A correct letter in the wrong place turns yellow
* An incorrect letter turns gray

## Demonstration
### Starting game screen
![image](https://user-images.githubusercontent.com/117612624/211126525-1df1cc6d-660a-43f3-bbfa-893a5cba6f32.png)
### Guessed correct word
![2](https://user-images.githubusercontent.com/117612624/211126713-2a364e2a-39c1-49dc-85bf-2f5fcda002b7.png)
### Invalid input prompt
![3](https://user-images.githubusercontent.com/117612624/211126945-44cb6e1f-0a23-4c14-8ab1-c70639eec887.png)
### Game over
![4](https://user-images.githubusercontent.com/117612624/211127008-245f750f-7c03-4c59-8de1-be1903544eed.png)

## Installation
* Clone this repository using the command: `git clone https://github.com/akialter/jordle.git`
* `lib` folder was provided that contained necessary libraries for the application.
* Run `javac --module-path [path_to_lib]/lib/ --add-modules=javafx.controls Jordle.java` on terminal to compile the program.
* Modify `[path_to_lib]` as the path to `lib` folder in your system.
* Execute `java --module-path [path_to_lib]/lib/ --add-modules=javafx.controls Jordle.java` to run the program.
