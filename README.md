# Card game / Processing gdx / my first project / AI / functionally finished / legacy code

# Makao

![Image](https://zagorskidev.files.wordpress.com/2017/10/zrzut-ekranu-z-2017-10-05-12-33-26.png?w=723)

# How is it done?

Build as PApplet (Processing 3.0). Main challenge in this project was doing computer play smart. There are some predefined AI tactics (offensive, defensive, mixer etc). For example ofensive player always use fightable cards when it is possible. AI players are choosing randomly their tactics at start of each round. All cards are thrown at center of the table with random shift, so player can usually see which cards was played recently (like in real game). Numbers on AI players “chairs” means numbers of their cards.

# How does it look like?

You can watch short video of application running.

[![Video](https://img.youtube.com/vi/tP1OxoloIYo/0.jpg)](https://youtu.be/tP1OxoloIYo)

# How can I run it?

You can compile src/makao/Makao.java or download runnable JAR here: [runnable JAR](https://drive.google.com/open?id=0B_bwkWjLwn2MT3JfbDhTLXdmdEU).

It requires Java 8 (PApplet doesn’t work with Java 9 yet).

# How can it be improved?

Code could be refactored using some design patterns. There are some code blocks that should be splitted to helper methods, espacially methods determining AI moves are horrific spaghetti. Dialogs (choosing called colour etc.) could be improved, now they are bulky. Even simple Swing dialogs would make huge difference. For now I think Processing isn’t best choice as library used to make such application, especially considering it’s problems with Java 9.
