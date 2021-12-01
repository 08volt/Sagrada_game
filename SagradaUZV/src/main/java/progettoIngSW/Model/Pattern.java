package progettoIngSW.Model;

import java.io.*;

public class Pattern implements java.io.Serializable {

    //
    //ATTRIBUTES
    //
    private int[] numbers;
    private Colors[] colors;
    private int difficult;
    private String name;
    private int col;   //length
    private int row;        //height

    //
    //CONSTRUCTORS
    //

    /**
     * Costruttore personalizzato che carica da file uno tra i 24 pattern standard
     * @param numPattern numero pattern da caricare (numPattern compreso tra 0 e 23)
     * @throws IOException problemi con la lettura da file
     */
    public Pattern(int numPattern) throws IOException {
        this.loadFromResource(numPattern);
    }

    public Pattern() {
    }

    /**
     * Costruttore personalizzato che carica da file uno tra i pattern personalizzati
     * @param name nome pattern personalizzato
     * @throws IOException problemi lettura da file
     */
    public Pattern(String name) throws IOException{
        this.loadFromCustom(name);
    }

    /**
     * Esegue il caricamento da file di un pattern personalizzato
     * @param name nome pattern personalizzato
     * @throws IOException problemi lettura da file
     */
    private void loadFromCustom(String name) throws IOException {
        String path = "/"+name;
        FileReader f;
        f = new FileReader(path);
        loadFromBuffer(new BufferedReader(f));


    }

    /**
     * Legge un file e inizializza le restrizioni di valore e colore per ogni singola cella
     * @param filebuf BufferedReader del file relativo al pattern da caricare
     * @throws IOException problemi lettura da file
     */
    private void loadFromBuffer(BufferedReader filebuf) throws IOException {

        String stringPattern = filebuf.readLine();    //lettura delle dimensioni del pattern
        String[] vector = stringPattern.split(" ");
        row = Integer.parseInt(vector[0]);
        col = Integer.parseInt(vector[1]);
        this.numbers = new int[col * row];
        this.colors = new Colors[col * row];

        this.name = filebuf.readLine();   //lettura del nome

        stringPattern = filebuf.readLine(); //lettura della stringa delle varie restrizioni
        this.difficult = Integer.parseInt(filebuf.readLine());
        for (int i = 0; i < row*col; i++){

            switch (stringPattern.charAt(i)){
                case '1': numbers[i] = 1; colors[i] = Colors.WHITE;break;
                case '2': numbers[i] = 2; colors[i] = Colors.WHITE;break;
                case '3': numbers[i] = 3; colors[i] = Colors.WHITE;break;
                case '4': numbers[i] = 4; colors[i] = Colors.WHITE;break;
                case '5': numbers[i] = 5; colors[i] = Colors.WHITE;break;
                case '6': numbers[i] = 6; colors[i] = Colors.WHITE;break;

                case 'b': colors[i] = Colors.BLUE; numbers[i] = 0;break;
                case 'y': colors[i] = Colors.YELLOW; numbers[i] = 0;break;
                case 'r': colors[i] = Colors.RED; numbers[i] = 0;break;
                case 'g': colors[i] = Colors.GREEN; numbers[i] = 0;break;
                case 'p': colors[i] = Colors.PURPLE; numbers[i] = 0;break;
                default: numbers[i] = 0; colors[i] = Colors.WHITE;break;
            }
        }

    }


    /**
     * Esegue il caricamento da file di un pattern standard
     * @param numPattern numero pattern da caricare
     * @throws IOException problemi lettura da file
     */
    private void loadFromResource(int numPattern) throws IOException {

        String path = "Pattern/pattern"+numPattern+".txt";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        loadFromBuffer(new BufferedReader(streamReader));

    }


    //
    //GET AND SET METHODS
    //
    public String getName() {
        return name;
    }

    public int getDifficult() {
        return difficult;
    }

    public void setDifficult(int difficult) {
        this.difficult = difficult;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getNumbers() {
        return numbers;
    }

    public Colors[] getColors() {
        return colors;
    }

    public void setNumbers(int[] numbers) {
        this.numbers = numbers;
    }

    public void setColors(Colors[] colors) {
        this.colors = colors;
    }

    public int getCol() {
        return this.col;
    }
    public int getRow() {
        return this.row;
    }
}
