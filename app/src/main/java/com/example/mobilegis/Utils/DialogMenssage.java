package com.example.mobilegis.Utils;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;


import com.example.mobilegis.R;

import java.util.List;

public class DialogMenssage {
    private Activity activity;
    private String menssage;
    private Button btnCancelar;
    private Button btnConfirmar;
    private ImageView image;
    private TextView txtMenssage;
    private AlertDialog dialog;
    private RadioGroup radioGroup;
    private int selecionado = -1;



    public DialogMenssage(Activity activity){
        this.activity = activity;
        createDialog();
    }
    public DialogMenssage(Activity activity,String menssage){
        this.activity = activity;
        this.menssage = menssage;
        createDialog();
    }


    private void createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_personalizado, null);

        builder.setView(view);

        dialog = builder.create();
        dialog.setCancelable(false);

        btnCancelar = view.findViewById(R.id.btnCancelar);
        btnConfirmar = view.findViewById(R.id.btnConfirm);
        txtMenssage = view.findViewById(R.id.text_dialog);
        image = view.findViewById(R.id.image_dialog_personalizado);
        radioGroup = view.findViewById(R.id.radio_dialog_personalizado);
    }


    public void mostrarDialogoPersonalizado() {
        dialog.show();

        if(menssage!=null){
            txtMenssage.setText(menssage);
        }
    }

    public void setRadioOptions(List<String> options){
        for (int i = 0; i< options.size(); i++){
            RadioButton rb = new RadioButton(activity);
            rb.setText(options.get(i));
            rb.setId(i);
            radioGroup.addView(rb);
        }
    }


    public String getMenssage() {
        return menssage;
    }

    public void setMenssage(String menssage) {
        this.menssage = menssage;
    }

    public Button getBtnCancelar() {
        return btnCancelar;
    }

    public Button getBtnConfirmar() {
        return btnConfirmar;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image.setImageDrawable(ContextCompat.getDrawable(activity,image));
    }

    public RadioGroup getRadioGroup(){
        return radioGroup;
    }


    public int getSelecionado(){
        selecionado = radioGroup.getCheckedRadioButtonId();
        return selecionado;
    }

    public void limpaRadio(){
        radioGroup.removeAllViews();
    }

    public void dismiss() {
        this.dialog.dismiss();
    }

    public void goneCancelar(){
        this.btnCancelar.setVisibility(View.GONE);
    }

    public void goneConfirmar(){
        this.btnConfirmar.setVisibility(View.GONE);
    }

    public void setTextConfirmar(String text){
        this.btnConfirmar.setText(text);
    }
}
