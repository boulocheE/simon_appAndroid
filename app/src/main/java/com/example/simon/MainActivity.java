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



public class MainActivity extends AppCompatActivity {
    private int bestScore;
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


        Button buttonNiveau2 = (Button) findViewById(R.id.buttonNiveau2);
        buttonNiveau2.setBackground(null);
        buttonNiveau2.setBackgroundResource( R.drawable.button_niveau_non_select );


        this.temps      = 500;
        this.coeffTemps = 1;

        this.niveauActuel = 1;
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
        this.temps      = 500;
        this.coeffTemps = 1;

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
                    if ( (int)( temps * coeffTemps ) > 100 ) temps = (int)( temps * coeffTemps );
                    // Retour à la couleur par défaut ou toute autre couleur souhaitée
                    textView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.violet));
                    // Continue with the next color after a delay
                    displayColorRecursive(handler, index + 1, temps   );
                }
            }, delayBetweenColors * 2 + 100); // Introduce a small additional delay for resetting
        }

        else
        {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    casesACoul();

                    debutPartieJoueur = true;
                }
            }, delayBetweenColors * 3 + 100); // Introduce a small additional delay for resetting
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