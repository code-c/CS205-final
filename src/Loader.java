import java.io.*;
import java.util.ArrayList;

/**
 * Load and save games in progress
 */
public class Loader {

    private File file;

    /**
     * Constructor
     */
    public Loader() {
        this.file = new File(Constants.FILEPATH);
    }

    /**
     * Write string representation of board
     * @param turn
     * @return 1 on success, -1 on failure
     */
    public int save(int turn, ArrayList<Player> players) {
        FileWriter fr;
        try {
            fr = new FileWriter(file);
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < players.size(); i++) {
                // store each player's piece's area and location
                for (int j = 0; j < players.get(i).getPieces().size(); j++) {
                    s.append(players.get(i).getPieces().get(j).getAr()).append("-").
                            append(players.get(i).getPieces().get(j).getRelativeLoc()).append(",");
                }
                s.append(players.get(i).getType()).append("\n");
            }
            fr.write(s.toString());
            fr.write(Integer.toString(turn));
            fr.close();
            return 1;

        } catch (IOException e) {
            System.out.println("Error writing to file");
            return -1;
        }
    }

    /**
     * Load game attributes
     * @return loaded game, or new game on failure to load
     */
    public Game load() {
        ArrayList<Player> players = new ArrayList<>();
        ArrayList<Piece> pieces;
        String[] piecesString;
        String[] pieceString;
        Board board = new Board();
        int curTurn;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;

            // iterate over save file creating pieces from text and storing them in players array
            for (int i = 0; i < Constants.NUMPLAYERS; i++) {
                st = br.readLine();
                pieces = new ArrayList<>();
                piecesString = st.split(",");
                Type type = stringToType(piecesString[piecesString.length - 1]);

                for (int j = 0; j < Constants.NUMPLAYERPIECES; j++) {
                    pieceString = piecesString[j].split("-");
                    pieces.add(new Piece(i, j, stringToArea(pieceString[0]), Integer.parseInt(pieceString[1]), type));
                }

                players.add(new Player(i, board, pieces, type));

            }

            // get turn
            curTurn = Integer.parseInt(br.readLine());

            // update board to reflect piece locations
            for (int i = 0; i < players.size(); i++) {
                for (int j = 0; j < players.get(i).getPieces().size(); j++) {
                    board.update(players.get(i).getPieces().get(j));
                }
            }

            // return loaded game
            return (new Game(players, board, curTurn));

            // on error, return new game
        } catch (IOException e) {
            System.out.println("Error reading file");
            return null;
        } catch (NullPointerException e) {
            System.out.println("Nothing saved");
            return null;
        }
    }

    private Area stringToArea(String s) {
        if (s.equals("HOME"))
            return Area.HOME;
        else if (s.equals("BOARD"))
            return Area.BOARD;
        else
            return Area.FINISH;
    }

    private Type stringToType(String s) {
        if (s.equals("PLAYER"))
            return Type.PLAYER;
        else if (s.equals("CPU"))
            return Type.CPU;
        else
            return Type.NULL;
    }

}
