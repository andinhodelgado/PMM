package br.com.usinasantafe.pmm.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.usinasantafe.pmm.PMMContext;
import br.com.usinasantafe.pmm.R;
import br.com.usinasantafe.pmm.model.bean.estaticas.RFuncaoAtivParBean;
import br.com.usinasantafe.pmm.util.ConexaoWeb;
import br.com.usinasantafe.pmm.model.bean.estaticas.AtividadeBean;
import br.com.usinasantafe.pmm.util.Tempo;

public class ListaAtividadeActivity extends ActivityGeneric {

    private ListView atividadeListView;
    private PMMContext pmmContext;
    private ProgressDialog progressBar;
    private ArrayList ativArrayList;
    private Long nroOS = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_atividade);

        pmmContext = (PMMContext) getApplication();

        Button buttonAtualAtividade = (Button) findViewById(R.id.buttonAtualAtividade);
        Button buttonRetAtividade = (Button) findViewById(R.id.buttonRetAtividade);
        TextView textViewTituloAtividade = (TextView) findViewById(R.id.textViewTituloAtividade);

        nroOS =  pmmContext.getConfigCTR().getConfig().getOsConfig();

        buttonAtualAtividade.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ConexaoWeb conexaoWeb = new ConexaoWeb();

                if (conexaoWeb.verificaConexao(ListaAtividadeActivity.this)) {

                    progressBar = new ProgressDialog(v.getContext());
                    progressBar.setCancelable(true);
                    progressBar.setMessage("Atualizando Atividades...");
                    progressBar.show();

                    pmmContext.getBoletimCTR().verAtiv( String.valueOf(nroOS), ListaAtividadeActivity.this, ListaAtividadeActivity.class, progressBar);

                } else {

                    AlertDialog.Builder alerta = new AlertDialog.Builder(ListaAtividadeActivity.this);
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

        buttonRetAtividade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ListaAtividadeActivity.this, OSActivity.class);
                startActivity(it);
                finish();
            }
        });

        if (pmmContext.getVerPosTela() == 1) {
            textViewTituloAtividade.setText("ATIVIDADE PRINCIPAL");
        } else {
            textViewTituloAtividade.setText("ATIVIDADE");
        }

        ativArrayList = pmmContext.getBoletimCTR().getAtivArrayList(nroOS);

        ArrayList<String> itens = new ArrayList<String>();
        for (int i = 0; i < ativArrayList.size(); i++) {
            AtividadeBean atividadeBean = (AtividadeBean) ativArrayList.get(i);
            itens.add(atividadeBean.getCodAtiv() + " - " + atividadeBean.getDescrAtiv());
        }

        AdapterList adapterList = new AdapterList(this, itens);
        atividadeListView = (ListView) findViewById(R.id.listAtividade);
        atividadeListView.setAdapter(adapterList);

        atividadeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> l, View v, int position,
                                    long id) {

                if(ativArrayList.size() == 0){

                    AlertDialog.Builder alerta = new AlertDialog.Builder(ListaAtividadeActivity.this);
                    alerta.setTitle("ATENÇÃO");
                    alerta.setMessage("FALHA NA SELEÇÃO DE ATIVIDADE. POR FAVOR, SELECIONE NOVAMENTE.");
                    alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent it = new Intent(ListaAtividadeActivity.this, ListaAtividadeActivity.class);
                            startActivity(it);
                            finish();
                        }
                    });
                    alerta.show();

                }
                else {

                    AtividadeBean atividadeBean = (AtividadeBean) ativArrayList.get(position);
                    ativArrayList.clear();

                    pmmContext.getConfigCTR().setAtivConfig(atividadeBean.getIdAtiv());

                    if (pmmContext.getVerPosTela() == 1) {

                        pmmContext.setTextoHorimetro("HORÍMETRO INICIAL:");

                        Intent it = new Intent(ListaAtividadeActivity.this, HorimetroActivity.class);
                        startActivity(it);
                        finish();

                    } else if ((pmmContext.getVerPosTela() == 2)) {

                        if (pmmContext.getConfigCTR().getConfig().getDtUltApontConfig().equals(Tempo.getInstance().dataComHora())) {

                            AlertDialog.Builder alerta = new AlertDialog.Builder(ListaAtividadeActivity.this);
                            alerta.setTitle("ATENÇÃO");
                            alerta.setMessage("POR FAVOR! ESPERE 1 MINUTO PARA REALIZAR UM NOVO APONTAMENTO.");
                            alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent it = new Intent(ListaAtividadeActivity.this, MenuPrincNormalActivity.class);
                                    startActivity(it);
                                    finish();
                                }
                            });
                            alerta.show();

                        } else {

                            if (pmmContext.getApontCTR().verifBackupApont(0L)) {

                                AlertDialog.Builder alerta = new AlertDialog.Builder(ListaAtividadeActivity.this);
                                alerta.setTitle("ATENÇÃO");
                                alerta.setMessage("OPERAÇÃO JÁ APONTADA PARA O EQUIPAMENTO!");
                                alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });

                                alerta.show();

                            } else {

                                if (pmmContext.getConfigCTR().getEquip().getTipo() == 1) {

                                    List rFuncaoAtividadeList = pmmContext.getBoletimCTR().getFuncaoAtividadeList();

                                    boolean transbordo = false;
                                    boolean rendimento = false;

                                    for (int i = 0; i < rFuncaoAtividadeList.size(); i++) {
                                        RFuncaoAtivParBean rFuncaoAtivParBean = (RFuncaoAtivParBean) rFuncaoAtividadeList.get(i);
                                        if (rFuncaoAtivParBean.getCodFuncao() == 2) {
                                            transbordo = true;
                                        }
                                        if (rFuncaoAtivParBean.getCodFuncao() == 1) {
                                            rendimento = true;
                                        }
                                    }
                                    rFuncaoAtividadeList.clear();

                                    if (transbordo) {
                                        Intent it = new Intent(ListaAtividadeActivity.this, TransbordoActivity.class);
                                        startActivity(it);
                                        finish();
                                    } else {

                                        pmmContext.getApontCTR().salvarApont(1L, 0L, 0L, getLongitude(), getLatitude());
                                        if (rendimento) {
                                            pmmContext.getBoletimCTR().insRendBD(nroOS);
                                        }

                                        Intent it = new Intent(ListaAtividadeActivity.this, MenuPrincNormalActivity.class);
                                        startActivity(it);
                                        finish();

                                    }

                                } else {
                                    Intent it = new Intent(ListaAtividadeActivity.this, ListaBocalFertActivity.class);
                                    startActivity(it);
                                    finish();
                                }

                            }

                        }

                    } else if (pmmContext.getVerPosTela() == 3) {

                        Intent it = new Intent(ListaAtividadeActivity.this, ListaParadaActivity.class);
                        startActivity(it);
                        finish();

                    }

                }
            }

        });

    }

    public void onBackPressed() {
    }

}
