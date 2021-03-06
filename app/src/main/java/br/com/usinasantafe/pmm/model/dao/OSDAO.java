package br.com.usinasantafe.pmm.model.dao;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import br.com.usinasantafe.pmm.control.ConfigCTR;
import br.com.usinasantafe.pmm.model.bean.estaticas.OSBean;
import br.com.usinasantafe.pmm.model.bean.estaticas.ROSAtivBean;
import br.com.usinasantafe.pmm.util.VerifDadosServ;

public class OSDAO {

    public OSDAO() {
    }

    public void verOS(String dado, Context telaAtual, Class telaProx, ProgressDialog progressDialog){
        VerifDadosServ.getInstance().setVerTerm(false);
        VerifDadosServ.getInstance().verDados(dado, "OS", telaAtual, telaProx, progressDialog);
    }

    public void recDadosOS(String result){

        ConfigCTR configCTR = new ConfigCTR();

        try {

            if (!result.contains("exceeded")) {

                int posicao = result.indexOf("#") + 1;
                String objPrinc = result.substring(0, result.indexOf("#"));
                String objSeg = result.substring(posicao);

                JSONObject jObj = new JSONObject(objPrinc);
                JSONArray jsonArray = jObj.getJSONArray("dados");

                if (jsonArray.length() > 0) {

                    OSBean osTO = new OSBean();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject objeto = jsonArray.getJSONObject(i);
                        Gson gson = new Gson();
                        osTO = gson.fromJson(objeto.toString(), OSBean.class);
                        osTO.insert();

                    }

                    jObj = new JSONObject(objSeg);
                    jsonArray = jObj.getJSONArray("dados");

                    for (int j = 0; j < jsonArray.length(); j++) {

                        JSONObject objeto = jsonArray.getJSONObject(j);
                        Gson gson = new Gson();
                        ROSAtivBean rosAtivBean = gson.fromJson(objeto.toString(), ROSAtivBean.class);
                        rosAtivBean.insert();

                    }

                    configCTR.setStatusConConfig(1L);
                    VerifDadosServ.getInstance().pulaTelaComTerm();

                } else {

                    configCTR.setStatusConConfig(0L);
                    VerifDadosServ.getInstance().msgComTerm("OS INEXISTENTE NA BASE DE DADOS! FAVOR VERIFICA A NUMERAÇÃO.");

                }

            } else {

                configCTR.setStatusConConfig(0L);
                VerifDadosServ.getInstance().msgComTerm("EXCEDEU TEMPO LIMITE DE PESQUISA! POR FAVOR, PROCURE UM PONTO MELHOR DE CONEXÃO DOS DADOS.");

            }

        } catch (Exception e) {
            LogErroDAO.getInstance().insert(e);
            configCTR.setStatusConConfig(0L);
            VerifDadosServ.getInstance().msgComTerm("FALHA DE PESQUISA DE OS! POR FAVOR, TENTAR NOVAMENTE COM UM SINAL MELHOR.");
        }

    }

    public boolean verTipoOS(Long nroOS){
        boolean ret = true;
        OSBean osTO = new OSBean();
        if(osTO.hasElements()){
            List osList = osTO.get("nroOS", nroOS);
            if(osList.size() > 0){
                osTO = (OSBean) osList.get(0);
                if(osTO.getTipoOS() == 0){
                    ret = false;
                }
            }
        }
        return ret;
    }

    public boolean verOS(Long nroOS){
        OSBean osTO = new OSBean();
        List osList = osTO.get("nroOS", nroOS);
        boolean ret = osList.size() > 0;
        osList.clear();
        return ret;
    }

}
