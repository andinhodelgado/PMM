package br.com.usinasantafe.pmm.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.com.usinasantafe.pmm.PMMContext;
import br.com.usinasantafe.pmm.R;
import br.com.usinasantafe.pmm.util.ConexaoWeb;
import br.com.usinasantafe.pmm.control.CheckListCTR;
import br.com.usinasantafe.pmm.model.bean.variaveis.ImpleMMBean;

public class ImplementoActivity extends ActivityGeneric {

    private PMMContext pmmContext;
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_implemento);

        pmmContext = (PMMContext) getApplication();

        Button buttonOkImplemento = (Button) findViewById(R.id.buttonOkPadrao);
        Button buttonCancImplemento = (Button) findViewById(R.id.buttonCancPadrao);
        TextView textViewImplemento = (TextView) findViewById(R.id.textViewImplemento);
        Button buttonAtualPadrao = (Button) findViewById(R.id.buttonAtualPadrao);

        buttonAtualPadrao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alerta = new AlertDialog.Builder(ImplementoActivity.this);
                alerta.setTitle("ATENÇÃO");
                alerta.setMessage("DESEJA REALMENTE ATUALIZAR BASE DE DADOS?");
                alerta.setNegativeButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ConexaoWeb conexaoWeb = new ConexaoWeb();

                        if (conexaoWeb.verificaConexao(ImplementoActivity.this)) {

                            progressBar = new ProgressDialog(ImplementoActivity.this);
                            progressBar.setCancelable(true);
                            progressBar.setMessage("Atualizando Implemento...");
                            progressBar.show();

                            pmmContext.getBoletimCTR().atualDadosEquipSeg(ImplementoActivity.this, ImplementoActivity.class, progressBar);

                        } else {

                            AlertDialog.Builder alerta = new AlertDialog.Builder(ImplementoActivity.this);
                            alerta.setTitle("ATENÇÃO");
                            alerta.setMessage("FALHA NA CONEXÃO DE DADOS. O CELULAR ESTA SEM SINAL. POR FAVOR, TENTE NOVAMENTE QUANDO O CELULAR ESTIVE COM SINAL.");
                            alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            alerta.show();

                        }


                    }
                });

                alerta.setPositiveButton("NÃO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alerta.show();

            }

        });

        textViewImplemento.setText("IMPLEMENTO " + pmmContext.getContImplemento() + ":");

        buttonOkImplemento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(pmmContext.getContImplemento() == 1){
                    if (!editTextPadrao.getText().toString().equals("")) {
                        Long impl = Long.parseLong(editTextPadrao.getText().toString());
                        if(pmmContext.getBoletimCTR().verImplemento(impl)){
                            ImpleMMBean impleMMBean = new ImpleMMBean();
                            impleMMBean.setPosImpleMM(1L);
                            impleMMBean.setCodEquipImpleMM(impl);
                            pmmContext.getBoletimCTR().setImplemento(impleMMBean);
                            pmmContext.setContImplemento(2);
                            Intent it = new Intent(ImplementoActivity.this, ImplementoActivity.class);
                            startActivity(it);
                            finish();
                        }
                        else{
                            msg("NUMERAÇÃO DE IMPLEMENTO INCORRETA. FAVOR, VERIFICAR A NUMERAÇÃO OU ATUALIZAR A BASE DE DADOS NOVAMENTE!");
                        }
                    }
                }
                else{
                    if (!editTextPadrao.getText().toString().equals("")) {
                        Long impl = Long.parseLong(editTextPadrao.getText().toString());
                        if(pmmContext.getBoletimCTR().verImplemento(impl) && (pmmContext.getBoletimCTR().verDuplicImpleMM(impl))){
                            ImpleMMBean impleMMBean = new ImpleMMBean();
                            impleMMBean.setPosImpleMM(2L);
                            impleMMBean.setCodEquipImpleMM(impl);
                            pmmContext.getBoletimCTR().setImplemento(impleMMBean);
                            verTela();
                        }
                        else{
                            msg("NUMERAÇÃO DE IMPLEMENTO INCORRETA OU REPETIDA. POR FAVOR, VERIFICAR A NUMERAÇÃO OU ATUALIZAR A BASE DE DADOS NOVAMENTE!");
                        }
                    }
                    else{
                        verTela();
                    }
                }
            }
        });

        buttonCancImplemento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextPadrao.getText().toString().length() > 0) {
                    editTextPadrao.setText(editTextPadrao.getText().toString().substring(0, editTextPadrao.getText().toString().length() - 1));
                }
            }
        });

    }

    public void verTela(){
        if (pmmContext.getVerPosTela() == 1) {
            salvarBoletimAberto();
        }
        else if (pmmContext.getVerPosTela() == 19) {
            Intent it = new Intent(ImplementoActivity.this, MenuPrincNormalActivity.class);
            startActivity(it);
            finish();
        }
    }

    public void msg(String msg){
        AlertDialog.Builder alerta = new AlertDialog.Builder(ImplementoActivity.this);
        alerta.setTitle("ATENÇÃO");
        alerta.setMessage(msg);
        alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alerta.show();
    }


    public void salvarBoletimAberto() {
        pmmContext.getBoletimCTR().salvarBolAbertoMM();
        CheckListCTR checkListCTR = new CheckListCTR();
        if(checkListCTR.verAberturaCheckList(pmmContext.getBoletimCTR().getTurno())){
            pmmContext.getApontCTR().inserirParadaCheckList(pmmContext.getBoletimCTR());
            pmmContext.setPosCheckList(1);
            checkListCTR.createCabecAberto(pmmContext.getBoletimCTR());
            if (pmmContext.getConfigCTR().getConfig().getAtualCheckList().equals(1L)) {
                Intent it = new Intent(ImplementoActivity.this, PergAtualCheckListActivity.class);
                startActivity(it);
                finish();
            } else {
                Intent it = new Intent(ImplementoActivity.this, ItemCheckListActivity.class);
                startActivity(it);
                finish();
            }
        }
        else{
            Intent it = new Intent(ImplementoActivity.this, EsperaInforActivity.class);
            startActivity(it);
            finish();
        }
    }

    public void onBackPressed() {
        if (pmmContext.getContImplemento() == 1) {
            if (pmmContext.getVerPosTela() == 1) {
                Intent it = new Intent(ImplementoActivity.this, HorimetroActivity.class);
                startActivity(it);
                finish();
            } else if (pmmContext.getVerPosTela() == 19) {
                Intent it = new Intent(ImplementoActivity.this, MenuPrincNormalActivity.class);
                startActivity(it);
                finish();
            }
        } else {
            pmmContext.setContImplemento(pmmContext.getContImplemento() - 1);
            Intent it = new Intent(ImplementoActivity.this, ImplementoActivity.class);
            startActivity(it);
            finish();
        }
    }

}
