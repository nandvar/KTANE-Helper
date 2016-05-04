package com.example.aanandvardhan.ktanehelper;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private TextView txtSpeechInput;
    private TextToSpeech t1;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String out="";
    private int level=0;
    private static String in[]=new String[]{"","","","",""};
    private boolean digit=false;
    private int battery=0;
    private String indi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtSpeechInput = (TextView) findViewById(R.id.result);
        Button btnSpeak = (Button) findViewById(R.id.start);

        t1=new TextToSpeech(this,this);

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            t1.setLanguage(Locale.US);
        }
    }

    private int say(String s) {
        out+="BOT: "+s+"\n";
        txtSpeechInput.setText(out);
        return t1.speak(s, TextToSpeech.QUEUE_FLUSH, null, null);
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    out+="USER: "+result.get(0);
                    out+="\n";
                    in[level]=result.get(0);

                    if(!in[level].toLowerCase().equals("exit"))
                    {
                        if(result.get(0).toLowerCase().equals("reset")){
                            level=0;
                            say("reset!");
                        }
                        else
                        {
                            compute();
                            level++;
                        }
                        promptSpeechInput();
                    }
                }
                break;
            }

        }
    }

    private void compute()
    {
        txtSpeechInput.setText(out);
        if(level==0) {
            if(in[0].equals("help")) {
                say("You're all alone!");
                level=-1;
            }
            else if(in[0].toLowerCase().equals("cable"))
                say("Wires");
            else if(in[0].toLowerCase().equals("big button"))
                say("Big button");
            else if(in[0].toLowerCase().equals("bomb check"))
                say("Check! Digit?");
            else
                level=-1;
        }

        else if(level==1) {
            if(in[0].toLowerCase().equals("cable"))
            {
                String[] temp=in[1].split(" ");
                if(temp.length==3)
                {
                    int s=0,pos=0;
                    for(int i=0;i<3;i++) {
                        if (temp[i].toLowerCase().equals("blue")) {
                            s++;
                            if (s == 2)
                                pos = i;
                        }
                    }
                    if(!(in[1].toLowerCase().contains("red")))
                        say("cut 2!");
                    else if(temp[2].equals("white"))
                        say("cut 3!");
                    else if(pos!=0)
                        say("cut "+(pos+1)+"!");
                    else
                        say("cut 3!");
                }
                else if(temp.length==4)
                {
                    int red=0,ri=0,yellow=0,yi=0,blue=0;
                    for(int i=0;i<4;i++) {
                        if (temp[i].toLowerCase().equals("blue")) {
                            blue++;
                        }
                        else if(temp[i].toLowerCase().equals("yellow")) {
                            yellow++;
                            yi=i+1;
                        }
                        else if(temp[i].toLowerCase().equals("red")) {
                            red++;
                            ri=i+1;
                        }
                    }

                    if(red>1 && !digit)
                        say("Cut wire "+ri+"!");
                    else if(yi==4 && red==0)
                        say("Cut first wire!");
                    else if(blue==1)
                        say("Cut first wire!");
                    else if(yellow>1)
                        say("Cut last wire!");
                    else
                        say("Cut second wire!");
                }

                else if(temp.length==5)
                {
                    int black=0,bi=0,yellow=0,red=0;
                    for(int i=0;i<5;i++) {
                        if (temp[i].toLowerCase().equals("black")) {
                            black++;
                            bi=i+1;
                        }
                        else if(temp[i].toLowerCase().equals("yellow")) {
                            yellow++;
                        }
                        else if(temp[i].toLowerCase().equals("red")) {
                            red++;
                        }
                    }

                    if(bi==4&& !digit)
                        say("Cut fourth wire!");
                    else if(red==1&&yellow>1)
                        say("Cut first wire!");
                    else if(black==0)
                        say("Cut second wire!");
                    else
                        say("Cut first wire!");
                }

                else if(temp.length==6)
                {
                    int white=0,yellow=0,red=0;
                    for(int i=0;i<4;i++) {
                        if (temp[i].toLowerCase().equals("white")) {
                            white++;
                        }
                        else if(temp[i].toLowerCase().equals("yellow")) {
                            yellow++;
                        }
                        else if(temp[i].toLowerCase().equals("red")) {
                            red++;
                        }
                    }


                    if(yellow==0&&!digit)
                        say("Cut third wire!");
                    else if(yellow==1&&white>1)
                        say("Cut fourth wire!");
                    else if(red==0)
                        say("Cut last wire!");
                    else
                        say("Cut fourth wire!");
                }
                level=-1;
            }
            else if(in[0].equalsIgnoreCase("bomb check"))
            {
                if(Integer.parseInt(in[1])%2==0)
                    digit=true;
                say("Battery?");
            }
            else if(in[0].equalsIgnoreCase("big button"))
            {
                String temp[]=in[1].split(" ");
                if(temp[0].equalsIgnoreCase("blue")&&temp[1].equalsIgnoreCase("abort"))
                    say("Hold!");
                else if(temp[1].equalsIgnoreCase("detonate")&&battery>1)
                {
                    say("Press and release!");
                    level=-1;
                }
                else if(temp[0].equalsIgnoreCase("white")&&indi.toLowerCase().contains("c a r"))
                    say("hold!");
                else if(battery>2&&indi.toLowerCase().contains("f r k"))
                {
                    say("Press and release!");
                    level=-1;
                }
                else if(temp[0].equalsIgnoreCase("yellow"))
                    say("hold");
                else if(temp[0].equalsIgnoreCase("red")&&temp[1].equalsIgnoreCase("hold"))
                {
                    say("Press and release!");
                    level=-1;
                }
                else
                    say("Hold!");
            }
        }

        else if(level==2)
        {
            if(in[0].equalsIgnoreCase("bomb check"))
            {
                battery=Integer.parseInt(in[2]);
                say("Indicators?");
            }
            if(in[0].equalsIgnoreCase("big button"))
            {
                if(in[2].equalsIgnoreCase("blue"))
                    say("Release at 4");
                else if(in[2].equalsIgnoreCase("white"))
                    say("Release at 1");
                else if(in[2].equalsIgnoreCase("yellow"))
                    say("Release at 5");
                else
                    say("Release at 1");
            }
        }

        else if(level==3)
        {
            if(in[0].equalsIgnoreCase("bomb check"))
            {
                indi = in[3];
                say(indi);

                say("K Thanks! Let's Start!");
                level=-1;
            }
        }

        else
            level=-1;
        SystemClock.sleep(2000);
    }
}