package dk.andbb.andshobber.database;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class getText extends Activity {
    private EditText et;

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.get_text_entry);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        // title
        try {
            String s = getIntent().getExtras().getString("title");
            if (s.length() > 0) {
                this.setTitle(s);
            }
        } catch (Exception e) {
        }
        // value

        try {
            et = ((EditText) findViewById(R.id.txtValue));
            et.setText(getIntent().getExtras().getString("value"));
            et.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        } catch (Exception e) {
        }
        // button
        Button buttonDone = ((Button) findViewById(R.id.btnDone));
        buttonDone.setOnClickListener(new View.OnClickListener() {

            //            @Override
            public void onClick(View v) {
                executeDone();
            }
        });
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        executeDone();
        super.onBackPressed();
    }

    /**
     *
     */
    private void executeDone() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("value", getText.this.et.getText().toString());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }


}