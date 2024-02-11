package com.example.simon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;

import android.os.Bundle;
import android.os.Handler;

import android.widget.TextView;
import android.view.View;

import java.util.List;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {
    private int bestScore;
    private int score;

    private int nbCase;
    private List<Integer> lstCasesGenerees;
    private List<Integer> lstCasesUser;

    private int[] tabIdCouleurs;


    private boolean debutPartieJoueur;
    private boolean finPartie;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.bestScore = 0;
        this.score     = 0;

        this.nbCase    = 0;
        this.lstCasesGenerees = new ArrayList<Integer>();
        this.lstCasesUser     = new ArrayList<Integer>();

        this.tabIdCouleurs = new int[] { ContextCompat.getColor(this, R.color.bleu  ),
                                         ContextCompat.getColor(this, R.color.vert  ),
                                         ContextCompat.getColor(this, R.color.jaune ),
                                         ContextCompat.getColor(this, R.color.rouge )};


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

        this.debutPartieJoueur = false;
        this.finPartie         = true;



        TextView textView;

        textView = (TextView) findViewById(R.id.textViewBestScore);
        textView.setText("Best score : " + this.bestScore);

        textView = (TextView) findViewById(R.id.textViewScore);
        textView.setText("Score : " + this.score);
    }


    public void debutJeu (View view) { this.genererCase(); }


    private void genererCase ()
    {
        this.nbCase += 1;
        this.lstCasesGenerees.add( (int) (Math.random() * 4) );

        this.modifierCoul();
    }


    private void modifierCoul()
    {
        final Handler handler = new Handler();
        final int delayBetweenColors = 200;

        // Start with the first index
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
                    // Retour à la couleur par défaut ou toute autre couleur souhaitée
                    textView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.violet));
                    // Continue with the next color after a delay
                    displayColorRecursive(handler, index + 1, delayBetweenColors);
                }
            }, delayBetweenColors * 2 + 100); // Introduce a small additional delay for resetting
        }

        else
            this.debutPartieJoueur = true;
    }



    // Helper method to get TextView by its ID
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

            if ( this.score > this.bestScore ) this.bestScore = this.score;
            this.score = 0;

            this.lstCasesGenerees.clear();


            TextView textView;

            textView = (TextView) findViewById(R.id.textViewBestScore);
            textView.setText( "Best score : " + this.bestScore );

            textView = (TextView) findViewById(R.id.textViewScore);
            textView.setText( "Score : " + this.score );

            valCorrectes = false;
        }

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