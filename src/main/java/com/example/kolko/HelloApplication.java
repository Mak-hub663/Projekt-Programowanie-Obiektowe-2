package com.example.kolko;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    private Button[][] plansza = new Button[3][3];
    private boolean czyturax = true;
    private int liczbauygranychx = 0;
    private int liczbauygranycho = 0;
    private int liczbaremisow = 0;
    private int liczbarozegranychgier = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage oknogry) {
        wyswietlmenu(oknogry);
    }

    private void wyswietlmenu(Stage oknogry) {
        VBox menu = new VBox(10);
        menu.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Button przyciskstart = new Button("Start");
        Button przyciskwyniki = new Button("Wyniki");
        Button przyciskwyjscie = new Button("Wyjście");

        przyciskstart.setMinWidth(150);
        przyciskwyniki.setMinWidth(150);
        przyciskwyjscie.setMinWidth(150);

        przyciskstart.setOnAction(e -> rozpocznijgre(oknogry));
        przyciskwyniki.setOnAction(e -> pokazwyniki());
        przyciskwyjscie.setOnAction(e -> System.exit(0));

        menu.getChildren().addAll(przyciskstart, przyciskwyniki, przyciskwyjscie);

        Scene scenamenu = new Scene(menu, 300, 200);
        oknogry.setScene(scenamenu);
        oknogry.setTitle("kółko i krzyżyk - menu");
        oknogry.show();
    }

    private void rozpocznijgre(Stage oknogry) {
        BorderPane glowneokno = new BorderPane();

        MenuBar menubar = new MenuBar();
        Menu menugra = new Menu("Gra");
        MenuItem menustart = new MenuItem("Nowa gra");
        MenuItem menupowrot = new MenuItem("Powrót do menu");
        Menu menuopcje = new Menu("Opcje");
        MenuItem menuwyniki = new MenuItem("Wyniki");
        MenuItem menuwyjscie = new MenuItem("Wyjście");

        menugra.getItems().addAll(menustart, menupowrot);
        menuopcje.getItems().addAll(menuwyniki, menuwyjscie);
        menubar.getMenus().addAll(menugra, menuopcje);

        glowneokno.setTop(menubar);

        GridPane siatka = new GridPane();
        inicjalizujplansze(siatka);
        glowneokno.setCenter(siatka);

        Scene scenagry = new Scene(glowneokno, 300, 350);
        oknogry.setScene(scenagry);
        oknogry.setTitle("kółko i krzyżyk - gra");

        menustart.setOnAction(e -> zresetujplansze(siatka));
        menupowrot.setOnAction(e -> wyswietlmenu(oknogry));
        menuwyniki.setOnAction(e -> pokazwyniki());
        menuwyjscie.setOnAction(e -> System.exit(0));
    }

    private void inicjalizujplansze(GridPane siatka) {
        siatka.getChildren().clear();
        for (int wiersz = 0; wiersz < 3; wiersz++) {
            for (int kolumna = 0; kolumna < 3; kolumna++) {
                Button przycisk = new Button("");
                przycisk.setMinSize(100, 100);
                przycisk.setOnAction(e -> wykonajruch(przycisk));
                plansza[wiersz][kolumna] = przycisk;
                siatka.add(przycisk, kolumna, wiersz);
            }
        }
    }

    private void wykonajruch(Button przycisk) {
        if (!przycisk.getText().isEmpty()) {
            return;
        }

        przycisk.setText(czyturax ? "X" : "O");

        if (sprawdzczywygrana()) {
            liczbarozegranychgier++;
            if (czyturax) {
                liczbauygranychx++;
                pokazzwyciezce("X");
            } else {
                liczbauygranycho++;
                pokazzwyciezce("O");
            }
            zresetujplansze();
        } else if (czyplanszapelna()) {
            liczbarozegranychgier++;
            liczbaremisow++;
            pokazzwyciezce("Remis");
            zresetujplansze();
        }

        czyturax = !czyturax;
    }

    private boolean sprawdzczywygrana() {
        for (int i = 0; i < 3; i++) {
            if (plansza[i][0].getText().equals(plansza[i][1].getText()) &&
                    plansza[i][1].getText().equals(plansza[i][2].getText()) &&
                    !plansza[i][0].getText().isEmpty()) {
                return true;
            }
            if (plansza[0][i].getText().equals(plansza[1][i].getText()) &&
                    plansza[1][i].getText().equals(plansza[2][i].getText()) &&
                    !plansza[0][i].getText().isEmpty()) {
                return true;
            }
        }
        if (plansza[0][0].getText().equals(plansza[1][1].getText()) &&
                plansza[1][1].getText().equals(plansza[2][2].getText()) &&
                !plansza[0][0].getText().isEmpty()) {
            return true;
        }
        if (plansza[0][2].getText().equals(plansza[1][1].getText()) &&
                plansza[1][1].getText().equals(plansza[2][0].getText()) &&
                !plansza[0][2].getText().isEmpty()) {
            return true;
        }

        return false;
    }

    private boolean czyplanszapelna() {
        for (int wiersz = 0; wiersz < 3; wiersz++) {
            for (int kolumna = 0; kolumna < 3; kolumna++) {
                if (plansza[wiersz][kolumna].getText().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void pokazzwyciezce(String zwyciezca) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("koniec gry");
        alert.setHeaderText(null);
        if ("Remis".equals(zwyciezca)) {
            alert.setContentText("gra zakończona remisem!");
        } else {
            alert.setContentText("gracz " + zwyciezca + " wygrywa!");
        }
        alert.showAndWait();
    }

    private void pokazwyniki() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("wyniki");
        alert.setHeaderText("statystyki gry");
        alert.setContentText(
                "liczba wygranych x: " + liczbauygranychx + "\n" +
                        "liczba wygranych o: " + liczbauygranycho + "\n" +
                        "liczba remisów: " + liczbaremisow + "\n" +
                        "liczba rozegranych gier: " + liczbarozegranychgier
        );
        alert.showAndWait();
    }

    private void zresetujplansze(GridPane siatka) {
        inicjalizujplansze(siatka);
        czyturax = true;
    }

    private void zresetujplansze() {
        for (int wiersz = 0; wiersz < 3; wiersz++) {
            for (int kolumna = 0; kolumna < 3; kolumna++) {
                plansza[wiersz][kolumna].setText("");
            }
        }
        czyturax = true;
    }
}
