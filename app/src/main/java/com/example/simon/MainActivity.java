package com.example.simon;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.os.Handler;

import android.widget.Button;
import android.widget.TextView;
import android.view.View;

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



public class MainActivity extends AppCompatActivity {
    private int bestScoreLvl1;
    private int bestScoreLvl2;
    private int score;

    private int nbCase;
    private List<Integer> lstCasesGenerees;
    private List<Integer> lstCasesUser;

    private int[] tabIdCouleurs;


    private int     temps;
    private double coeffTemps;
    private int    niveauActuel;


    private boolean debutPartieJoueur;
    private boolean finPartie;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.bestScoreLvl1 = 0;
        this.bestScoreLvl2 = 0;
        this.score         = 0;

        this.nbCase    = 0;
        this.lstCasesGenerees = new ArrayList<Integer>();
        this.lstCasesUser     = new ArrayList<Integer>();

        this.tabIdCouleurs = new int[] { ContextCompat.getColor(this, R.color.bleu  ),
                                         ContextCompat.getColor(this, R.color.vert  ),
                                         ContextCompat.getColor(this, R.color.jaune ),
                                         ContextCompat.getColor(this, R.color.rouge )};

        this.debutPartieJoueur = false;
        this.finPartie         = true;

        this.temps      = 300;
        this.coeffTemps = 1;

        this.niveauActuel = 1;


        setContentView(R.layout.activity_main);

        for ( int cpt = 0; cpt < 4; cpt ++ )
        {
            final int id = cpt;

            String idView = "textView" + (id + 1);
            int ressourceId = getResources().getIdentifier(idView, "id", getPackageName());
            TextView myTextView = findViewById(ressourceId);

            myTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { clickView(id); }
            });
        }


        if ( !this.fichierPropertiesExiste() )
            this.sauvegarderProprietes();
        else
        {
            this.bestScoreLvl1 = this.chargerProprietes()[0];
            this.bestScoreLvl2 = this.chargerProprietes()[1];
        }


        TextView textView;

        textView = (TextView) findViewById(R.id.textViewBestScore);
        textView.setText("Best score : " + this.bestScoreLvl1);

        textView = (TextView) findViewById(R.id.textViewScore);
        textView.setText("Score : " + this.score);


        Button buttonNiveau2 = (Button) findViewById(R.id.buttonNiveau2);
        buttonNiveau2.setBackground(null);
        buttonNiveau2.setBackgroundResource( R.drawable.button_niveau_non_select );
    }


    private void sauvegarderProprietes() {
        Properties properties = new Properties();

        // Ajouter des propriétés
        properties.setProperty("bestScore1", this.bestScoreLvl1 + "");
        properties.setProperty("bestScore2", this.bestScoreLvl2 + "");

        // Écrire dans le fichier
        try (OutputStream output = new FileOutputStream(getFilesDir() + "/config.properties")) {
            properties.store(output, "Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Integer[] chargerProprietes() {
        Properties properties = new Properties();

        // Charger à partir du fichier
        try (InputStream input = new FileInputStream(getFilesDir() + "/config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Accéder aux propriétés
        String bestScore1 = properties.getProperty("bestScore1");
        String bestScore2 = properties.getProperty("bestScore2");

        return new Integer[] { Integer.parseInt(bestScore1), Integer.parseInt(bestScore2) };
    }

    private boolean fichierPropertiesExiste() {
        File file = new File(getFilesDir(), "config.properties");
        return file.exists();
    }


    public void debutJeu (View view)
    {
        if ( this.finPartie )
        {
            this.genererCase();
            this.finPartie = false;

            if ( this.niveauActuel == 1 ) this.niveau1();
            else                          this.niveau2();
        }
    }


    public void clickNiveau1 (View view)
    {
        if ( this.finPartie )
        {
            this.niveauActuel = 1;
            this.niveau1();
        }
    }


    public void clickNiveau2 (View view)
    {
        if ( this.finPartie )
        {
            this.niveauActuel = 2;
            this.niveau2();
        }
    }


    public void niveau1 ()
    {
        this.temps      = 300;
        this.coeffTemps = 1;

        TextView textView = (TextView) findViewById(R.id.textViewBestScore);
        textView.setText("Best score : " + this.bestScoreLvl1);

        Button buttonNiveau1 = (Button) findViewById(R.id.buttonNiveau1);
        buttonNiveau1.setBackground(null);
        buttonNiveau1.setBackgroundResource( R.drawable.button );

        Button buttonNiveau2 = (Button) findViewById(R.id.buttonNiveau2);
        buttonNiveau2.setBackground(null);
        buttonNiveau2.setBackgroundResource( R.drawable.button_niveau_non_select );
    }


    public void niveau2 ()
    {
        this.temps      = 500;
        this.coeffTemps = 0.95;

        TextView textView = (TextView) findViewById(R.id.textViewBestScore);
        textView.setText("Best score : " + this.bestScoreLvl2);

        Button buttonNiveau1 = (Button) findViewById(R.id.buttonNiveau1);
        buttonNiveau1.setBackground(null);
        buttonNiveau1.setBackgroundResource( R.drawable.button_niveau_non_select );

        Button buttonNiveau2 = (Button) findViewById(R.id.buttonNiveau2);
        buttonNiveau2.setBackground(null);
        buttonNiveau2.setBackgroundResource( R.drawable.button );
    }


    private void genererCase ()
    {
        this.nbCase += 1;
        this.lstCasesGenerees.add( (int) (Math.random() * 4) );

        this.modifierCoul();
    }


    private void modifierCoul()
    {
        final Handler handler = new Handler();
        final int delayBetweenColors = (int)( this.temps * this.coeffTemps );


        this.casesAViolet();


        // Commencer avec le premier index
        displayColorRecursive(handler, 0, delayBetweenColors);

        this.lstCasesUser.clear();
    }

    private void displayColorRecursive(final Handler handler, final int index, final int delayBetweenColors)
    {
        if (index < this.lstCasesGenerees.size()) {
            final int idCoul = this.lstCasesGenerees.get(index);
            final TextView textView = getTextViewById(idCoul + 1);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Appliquer la couleur
                    textView.setBackgroundColor(tabIdCouleurs[idCoul]);
                }
            }, delayBetweenColors * 1); // Délai entre chaque changement


            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if ( (int)( temps * coeffTemps ) > 100 ) temps = (int)( temps * coeffTemps );
                    // Retour à la couleur par défaut ou toute autre couleur souhaitée
                    textView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.violet));
                    // Continuer avec la couleur suivante après le délai
                    displayColorRecursive(handler, index + 1, temps   );
                }
            }, delayBetweenColors * 2 + 100); // Ajouter un petit délai pour le reset
        }

        else
        {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    casesACoul();

                    debutPartieJoueur = true;
                }
            }, delayBetweenColors * 3 + 100); // Ajouter un petit délai pour le reset
        }
    }


    private void casesAViolet()
    {
        TextView[] tabTextView = new TextView[4];

        tabTextView[0] = (TextView) findViewById(R.id.textView1);
        tabTextView[1] = (TextView) findViewById(R.id.textView2);
        tabTextView[2] = (TextView) findViewById(R.id.textView3);
        tabTextView[3] = (TextView) findViewById(R.id.textView4);

        for ( TextView t : tabTextView )
        {
            t.setBackground(null);
            t.setBackgroundResource( R.color.violet );
        }
    }


    private void casesACoul ()
    {
        TextView textView;

        textView = (TextView) findViewById(R.id.textView1);
        textView.setBackground(null);
        textView.setBackgroundResource( R.drawable.text_view_bleu );

        textView = (TextView) findViewById(R.id.textView2);
        textView.setBackground(null);
        textView.setBackgroundResource( R.drawable.text_view_vert );

        textView = (TextView) findViewById(R.id.textView3);
        textView.setBackground(null);
        textView.setBackgroundResource( R.drawable.text_view_jaune );

        textView = (TextView) findViewById(R.id.textView4);
        textView.setBackground(null);
        textView.setBackgroundResource( R.drawable.text_view_rouge );
    }



    private TextView getTextViewById(int id)
    {
        String idView = "textView" + id;
        int ressourceId = getResources().getIdentifier(idView, "id", getPackageName());
        if (ressourceId != 0) {
            return findViewById(ressourceId);
        }
        return null;
    }


    private void clickView ( int idView )
    {
        boolean valCorrectes = true;

        if ( !this.debutPartieJoueur ) valCorrectes = false;

        if ( valCorrectes ) this.lstCasesUser.add(idView);


        if ( this.lstCasesUser.size() == 0 || this.lstCasesGenerees.size() == 0 ) valCorrectes = false;

        if ( valCorrectes && this.lstCasesGenerees.get(this.lstCasesUser.size() - 1) != idView )
        {
            this.finPartie = true;
            this.nbCase    = 0;

            if ( niveauActuel == 1 && this.score > this.bestScoreLvl1 ) this.bestScoreLvl1 = this.score;
            if ( niveauActuel == 2 && this.score > this.bestScoreLvl2 ) this.bestScoreLvl2 = this.score;

            this.score = 0;

            this.sauvegarderProprietes();

            this.lstCasesGenerees.clear();


            int bestScore = this.niveauActuel == 1 ? this.bestScoreLvl1 : this.bestScoreLvl2;

            TextView textView;
            textView = (TextView) findViewById(R.id.textViewBestScore);
            textView.setText( "Best score : " + bestScore );

            textView = (TextView) findViewById(R.id.textViewScore);
            textView.setText( "Score : " + this.score );

            valCorrectes = false;
        }


        if ( !valCorrectes )
            this.casesAViolet();


        if ( valCorrectes && this.lstCasesGenerees.size() == this.lstCasesUser.size() )
        {
            this.score ++;

            TextView textView = (TextView) findViewById(R.id.textViewScore);
            textView.setText( "Score : " + this.score );


            this.debutPartieJoueur = false;
            this.genererCase();
        }
    }
}