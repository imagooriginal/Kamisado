package kamisadoModel;

public class Board {
        private Square[][] grid;

        public Board() {
                initializeBoard();
        }

        private void initializeBoard() {

                grid = new Square[][] {
                                { new Square(Color.ORANGE, new Coordinates(0, 0)),
                                                new Square(Color.BLUE, new Coordinates(0, 1)),
                                                new Square(Color.PURPLE, new Coordinates(0, 2)),
                                                new Square(Color.PINK, new Coordinates(0, 3)),
                                                new Square(Color.YELLOW, new Coordinates(0, 4)),
                                                new Square(Color.RED, new Coordinates(0, 5)),
                                                new Square(Color.GREEN, new Coordinates(0, 6)),
                                                new Square(Color.BROWN, new Coordinates(0, 7)), },
                                // 1. sor (A1 - H1)
                                { new Square(Color.RED, new Coordinates(1, 0)),
                                                new Square(Color.ORANGE, new Coordinates(1, 1)),
                                                new Square(Color.PINK, new Coordinates(1, 2)),
                                                new Square(Color.GREEN, new Coordinates(1, 3)),
                                                new Square(Color.BLUE, new Coordinates(1, 4)),
                                                new Square(Color.YELLOW, new Coordinates(1, 5)),
                                                new Square(Color.BROWN, new Coordinates(1, 6)),
                                                new Square(Color.PURPLE, new Coordinates(1, 7)) },
                                // 2. sor (A2 - H2)
                                { new Square(Color.GREEN, new Coordinates(2, 0)),
                                                new Square(Color.PINK, new Coordinates(2, 1)),
                                                new Square(Color.ORANGE, new Coordinates(2, 2)),
                                                new Square(Color.RED, new Coordinates(2, 3)),
                                                new Square(Color.PURPLE, new Coordinates(2, 4)),
                                                new Square(Color.BROWN, new Coordinates(2, 5)),
                                                new Square(Color.YELLOW, new Coordinates(2, 6)),
                                                new Square(Color.BLUE, new Coordinates(2, 7)) },
                                // 3. sor (A3 - H3)
                                { new Square(Color.PINK, new Coordinates(3, 0)),
                                                new Square(Color.PURPLE, new Coordinates(3, 1)),
                                                new Square(Color.BLUE, new Coordinates(3, 2)),
                                                new Square(Color.ORANGE, new Coordinates(3, 3)),
                                                new Square(Color.BROWN, new Coordinates(3, 4)),
                                                new Square(Color.GREEN, new Coordinates(3, 5)),
                                                new Square(Color.RED, new Coordinates(3, 6)),
                                                new Square(Color.YELLOW, new Coordinates(3, 7)) },
                                // 4. sor (A4 - H4)
                                { new Square(Color.YELLOW, new Coordinates(4, 0)),
                                                new Square(Color.RED, new Coordinates(4, 1)),
                                                new Square(Color.GREEN, new Coordinates(4, 2)),
                                                new Square(Color.BROWN, new Coordinates(4, 3)),
                                                new Square(Color.ORANGE, new Coordinates(4, 4)),
                                                new Square(Color.BLUE, new Coordinates(4, 5)),
                                                new Square(Color.PURPLE, new Coordinates(4, 6)),
                                                new Square(Color.PINK, new Coordinates(4, 7)) },
                                // 5. sor (A5 - H5)
                                { new Square(Color.BLUE, new Coordinates(5, 0)),
                                                new Square(Color.YELLOW, new Coordinates(5, 1)),
                                                new Square(Color.BROWN, new Coordinates(5, 2)),
                                                new Square(Color.PURPLE, new Coordinates(5, 3)),
                                                new Square(Color.RED, new Coordinates(5, 4)),
                                                new Square(Color.ORANGE, new Coordinates(5, 5)),
                                                new Square(Color.PINK, new Coordinates(5, 6)),
                                                new Square(Color.GREEN, new Coordinates(5, 7)) },
                                // 6. sor (A6 - H6)
                                { new Square(Color.PURPLE, new Coordinates(6, 0)),
                                                new Square(Color.BROWN, new Coordinates(6, 1)),
                                                new Square(Color.YELLOW, new Coordinates(6, 2)),
                                                new Square(Color.BLUE, new Coordinates(6, 3)),
                                                new Square(Color.GREEN, new Coordinates(6, 4)),
                                                new Square(Color.PINK, new Coordinates(6, 5)),
                                                new Square(Color.ORANGE, new Coordinates(6, 6)),
                                                new Square(Color.RED, new Coordinates(6, 7)) },
                                // 7. sor (A7 - H7)
                                { new Square(Color.BROWN, new Coordinates(7, 0)),
                                                new Square(Color.GREEN, new Coordinates(7, 1)),
                                                new Square(Color.RED, new Coordinates(7, 2)),
                                                new Square(Color.YELLOW, new Coordinates(7, 3)),
                                                new Square(Color.PINK, new Coordinates(7, 4)),
                                                new Square(Color.PURPLE, new Coordinates(7, 5)),
                                                new Square(Color.BLUE, new Coordinates(7, 6)),
                                                new Square(Color.ORANGE, new Coordinates(7, 7)) }
                                // 8. sor (A8 - H8)
                };

                initalizePieces();
        }

        public void initalizePieces() {
                for (int i = 0; i < 8; i++) {
                        grid[0][i].setPiece(new Piece(Color.values()[i], grid[0][i], Player.PLAYER1));
                        grid[7][i].setPiece(new Piece(Color.values()[7 - i], grid[7][i], Player.PLAYER2));
                }
        }

        public Square[][] getGrid() {
                return grid;
        }
}
