package kamisadoModel;

public class Board {
        private Square[][] grid;

        public Board() {
                initializeBoard();
        }

        private void initializeBoard() {
                grid = new Square[][] {
                                { new Square(Color.ORANGE, new Coordinates(1, 1)),
                                                new Square(Color.BLUE, new Coordinates(1, 2)),
                                                new Square(Color.PURPLE, new Coordinates(1, 3)),
                                                new Square(Color.PINK, new Coordinates(1, 4)),
                                                new Square(Color.YELLOW, new Coordinates(1, 5)),
                                                new Square(Color.RED, new Coordinates(1, 6)),
                                                new Square(Color.GREEN, new Coordinates(1, 7)),
                                                new Square(Color.BROWN, new Coordinates(1, 8)) },
                                // 1. sor (A1 - H1)
                                { new Square(Color.RED, new Coordinates(2, 1)),
                                                new Square(Color.ORANGE, new Coordinates(2, 2)),
                                                new Square(Color.PINK, new Coordinates(2, 3)),
                                                new Square(Color.GREEN, new Coordinates(2, 4)),
                                                new Square(Color.BLUE, new Coordinates(2, 5)),
                                                new Square(Color.YELLOW, new Coordinates(2, 6)),
                                                new Square(Color.BROWN, new Coordinates(2, 7)),
                                                new Square(Color.PURPLE, new Coordinates(2, 8)) },
                                // 2. sor (A2 - H2)
                                { new Square(Color.GREEN, new Coordinates(3, 1)),
                                                new Square(Color.PINK, new Coordinates(3, 2)),
                                                new Square(Color.ORANGE, new Coordinates(3, 3)),
                                                new Square(Color.RED, new Coordinates(3, 4)),
                                                new Square(Color.PURPLE, new Coordinates(3, 5)),
                                                new Square(Color.BROWN, new Coordinates(3, 6)),
                                                new Square(Color.YELLOW, new Coordinates(3, 7)),
                                                new Square(Color.BLUE, new Coordinates(3, 8)) },
                                // 3. sor (A3 - H3)
                                { new Square(Color.PINK, new Coordinates(4, 1)),
                                                new Square(Color.PURPLE, new Coordinates(4, 2)),
                                                new Square(Color.BLUE, new Coordinates(4, 3)),
                                                new Square(Color.ORANGE, new Coordinates(4, 4)),
                                                new Square(Color.BROWN, new Coordinates(4, 5)),
                                                new Square(Color.GREEN, new Coordinates(4, 6)),
                                                new Square(Color.RED, new Coordinates(4, 7)),
                                                new Square(Color.YELLOW, new Coordinates(4, 8)) },
                                // 4. sor (A4 - H4)
                                { new Square(Color.YELLOW, new Coordinates(5, 1)),
                                                new Square(Color.RED, new Coordinates(5, 2)),
                                                new Square(Color.GREEN, new Coordinates(5, 3)),
                                                new Square(Color.BROWN, new Coordinates(5, 4)),
                                                new Square(Color.ORANGE, new Coordinates(5, 5)),
                                                new Square(Color.BLUE, new Coordinates(5, 6)),
                                                new Square(Color.PURPLE, new Coordinates(5, 7)),
                                                new Square(Color.PINK, new Coordinates(5, 8)) },
                                // 5. sor (A5 - H5)
                                { new Square(Color.BLUE, new Coordinates(6, 1)),
                                                new Square(Color.YELLOW, new Coordinates(6, 2)),
                                                new Square(Color.BROWN, new Coordinates(6, 3)),
                                                new Square(Color.PURPLE, new Coordinates(6, 4)),
                                                new Square(Color.RED, new Coordinates(6, 5)),
                                                new Square(Color.ORANGE, new Coordinates(6, 6)),
                                                new Square(Color.PINK, new Coordinates(6, 7)),
                                                new Square(Color.GREEN, new Coordinates(6, 8)) },
                                // 6. sor (A6 - H6)
                                { new Square(Color.PURPLE, new Coordinates(7, 1)),
                                                new Square(Color.BROWN, new Coordinates(7, 2)),
                                                new Square(Color.YELLOW, new Coordinates(7, 3)),
                                                new Square(Color.BLUE, new Coordinates(7, 4)),
                                                new Square(Color.GREEN, new Coordinates(7, 5)),
                                                new Square(Color.PINK, new Coordinates(7, 6)),
                                                new Square(Color.ORANGE, new Coordinates(7, 7)),
                                                new Square(Color.RED, new Coordinates(7, 8)) },
                                // 7. sor (A7 - H7)
                                { new Square(Color.BROWN, new Coordinates(8, 1)),
                                                new Square(Color.GREEN, new Coordinates(8, 2)),
                                                new Square(Color.RED, new Coordinates(8, 3)),
                                                new Square(Color.YELLOW, new Coordinates(8, 4)),
                                                new Square(Color.PINK, new Coordinates(8, 5)),
                                                new Square(Color.PURPLE, new Coordinates(8, 6)),
                                                new Square(Color.BLUE, new Coordinates(8, 7)),
                                                new Square(Color.ORANGE, new Coordinates(8, 8)) }
                                // 8. sor (A8 - H8)
                };

        }
}
