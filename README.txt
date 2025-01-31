=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 1200 Game Project README
PennKey: 74968467
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. I/O:
  Exiting the game will save the entire game state including positions and score to a text file.
  This was achieved using BufferedWriter in the function saveGame().
  Reloading the game using loadGame() reads the file,
  using multiple helper functions to parse,
  and sets the game state to the values stored in the file.
  Exceptions are thrown and handled if the formatting is not proper.

  2. Inheritance/subtyping:
  The abstract class GameObject is extended by the class Bird and abstract class generatedObject,
  which is extended by Obstacle and Coin.
  GameObject has functions intersects, isOutOfBounds, move, and clip.
  Bird overrides the draw function in GameObject.
  GeneratedObject overrides the move and clip functions.
  Both obstacle and coin have different implementations of game object and override the draw function.
  Obstacle has a unique field called id that tracks a pair of obstacles that come together.

  3. Collections:
  An ArrayList of type Obstacle stores active obstacles that appear on the screen.
  If the obstacle moves off the screen,
  it will be removed from the list by iterating through the list and checking if it isOutOfBounds.
  At certain time intervals, a new obstacle with a random appearance will be generated and added to the list.
  The ArrayList of Coin works similarly but is removed from the list immediately when the bird intersects a coin.
  An ArrayList is a good choice over a set for preserving the order of insertion
  as they will also be removed in the same order, making removal more efficient.

  4. JUnit testing:
  There are at least 10 tests that test a variety of the game logic and different methods.
  I have ones that test general cases such as simply updating the position of birds
  and obstacles and edge cases when they move/attempt to move off-screen.
  I also tested creating obstacles/coins, moving obstacles, and updating scores.

===============================
=: File Structure Screenshot :=
===============================
- Include a screenshot of your project's file structure. This should include
  all of the files in your project, and the folders they are in. You can
  upload this screenshot in your homework submission to gradescope, named
  "file_structure.png".

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.

GameObj is the parent class of all objects in the game and has a key feature of being movable.
It is extended by the class Bird, which manages the playable character,
and GeneratedObj, another abstract class that groups together objects that move on their own independently
and are also generated randomly at certain time intervals.
Obstacle manages the behavior of obstacles (rectangles on the screen),
from generating them to creating a random appearance and moving them.
Coin manages the generation of coins similarly to obstacles, but it has a fixed appearance.
GameDisplay manages the main interactable game JFrame,
linking together the interactions between the player Bird, obstacles, and coins
and updating the score accordingly on a JLabel.
It also contains methods to pause, reset, save, and load the game.
RunFlappyBird manages the final UI appearance and implements the gameDisplay label and all the button components.

----------------------------

- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?

Correctly generating the objects and updating the score was quite hard.
Initially, I made one obstacle a combination of 2 rectangles,
but that made calculating the intersection with the bird difficult
as the gap is also included with the object.
Thus, I changed the implementation to just a singular rectangle,
and with each random generation of an obstacle, it adds two rectangles to the ArrayList,
both sharing the same unique id.
The concept of an id is something I came up with after struggling to update the score,
which incremented continuously due to tick trying to update the score again
even if the obstacle’s score has already been counted.
I wanted to update the score immediately after the bird passed the obstacle,
so now, if the last seen obstacle’s id is the same as the current one,
the score is not incremented.
This ensures that each obstacle only contributes to the score exactly once.

----------------------------

- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?

Overall, my design is pretty good because I already tried to refactor many helper functions.
However, it would be beneficial to further refactor writing and parsing the saved files.
I have a good separation of functionality due to
the separated subclasses and files, each serving unique purposes.
For example, the actual game logic and behavior in GameDisplay is separated
from the viewable UI components in RunFlappyBird.
The private states are generally well encapsulated, with nearly all variables being private
and methods being private if they can.
I also use the get methods when accessing the variables in other classes.

========================
=: External Resources :=
========================

- Cite any external resources (images, tutorials, etc.) that you may have used
  while implementing your game.

Music generation: https://sfxr.me/





