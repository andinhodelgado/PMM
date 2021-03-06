package br.com.usinasantafe.pmm.control;

import java.util.ArrayList;
import java.util.List;

import br.com.usinasantafe.pmm.model.bean.estaticas.ParadaBean;
import br.com.usinasantafe.pmm.model.bean.variaveis.BoletimFertBean;
import br.com.usinasantafe.pmm.model.bean.variaveis.BoletimMMBean;
import br.com.usinasantafe.pmm.model.dao.CabecPneuDAO;
import br.com.usinasantafe.pmm.model.dao.ParadaDAO;
import br.com.usinasantafe.pmm.model.dao.RFuncaoAtivParDAO;
import br.com.usinasantafe.pmm.util.Tempo;
import br.com.usinasantafe.pmm.model.dao.ApontFertDAO;
import br.com.usinasantafe.pmm.model.dao.ApontMMDAO;
import br.com.usinasantafe.pmm.model.dao.BoletimFertDAO;
import br.com.usinasantafe.pmm.model.dao.BoletimMMDAO;
import br.com.usinasantafe.pmm.model.dao.MovLeiraDAO;
import br.com.usinasantafe.pmm.model.bean.variaveis.ApontFertBean;
import br.com.usinasantafe.pmm.model.bean.variaveis.ApontMMBean;

public class ApontCTR {

    private ApontMMBean apontMMBean;
    private ApontFertBean apontFertBean;

    public ApontCTR() {
        if (apontMMBean == null)
            apontMMBean = new ApontMMBean();
        if (apontFertBean == null)
            apontFertBean = new ApontFertBean();
    }

    //////////////////////////// SETAR CAMPOS ///////////////////////////////////////////////

    public void setOSApont(Long os){
        ConfigCTR configCTR = new ConfigCTR();
        if(configCTR.getEquip().getTipo() == 1) {
            apontMMBean.setOsApontMM(os);
        }
        else{
            apontFertBean.setOsApontFert(os);
        }
    }

    public void setAtivApont(Long ativ){
        ConfigCTR configCTR = new ConfigCTR();
        if(configCTR.getEquip().getTipo() == 1) {
            apontMMBean.setAtivApontMM(ativ);
            apontMMBean.setStatusConApontMM(configCTR.getConfig().getStatusConConfig());
            apontMMBean.setParadaApontMM(0L);
            apontMMBean.setTransbApontMM(0L);
        }
        else{
            apontFertBean.setAtivApontFert(ativ);
            apontFertBean.setStatusConApontFert(configCTR.getConfig().getStatusConConfig());
            apontFertBean.setParadaApontFert(0L);
        }
    }

    public void setParadaApont(Long parada){
        ConfigCTR configCTR = new ConfigCTR();
        if(configCTR.getEquip().getTipo() == 1) {
            apontMMBean.setParadaApontMM(parada);
        }
        else{
            apontFertBean.setBocalApontFert(0L);
            apontFertBean.setPressaoApontFert(0D);
            apontFertBean.setVelocApontFert(0L);
            apontFertBean.setParadaApontFert(parada);
        }
    }

    public void setTransb(Long transb){
        apontMMBean.setTransbApontMM(transb);
    }

    public void setBocal(Long bocal){
        apontFertBean.setBocalApontFert(bocal);
    }

    public void setPressaoBocal(Double pressaoBocal){
        apontFertBean.setPressaoApontFert(pressaoBocal);
    }

    public void setVelocApont(Long velocApont){
        apontFertBean.setVelocApontFert(velocApont);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////// GET DE CAMPOS ///////////////////////////////////////////

    public Long getAtivApont(){
        ConfigCTR configCTR = new ConfigCTR();
        if(configCTR.getEquip().getTipo() == 1) {
            return apontMMBean.getAtivApontMM();
        }
        else{
            return apontFertBean.getAtivApontFert();
        }
    }

    public Long getBocalApontFert(){
        return apontFertBean.getBocalApontFert();
    }

    public Double getPressaoApontFert(){
        return apontFertBean.getPressaoApontFert();
    }

    public List getListParada(){
        ConfigCTR configCTR = new ConfigCTR();
        ParadaDAO paradaDAO = new ParadaDAO();
        return paradaDAO.getListParada(configCTR.getConfig().getAtivConfig());
    }

    public Long getIdApont(){
        ConfigCTR configCTR = new ConfigCTR();
        if(configCTR.getEquip().getTipo() == 1) {
            ApontMMDAO apontMMDAO = new ApontMMDAO();
            return apontMMDAO.getIdApontAberto();
        }
        else{
            ApontFertDAO apontFertDAO = new ApontFertDAO();
            return apontFertDAO.getIdApontAberto();
        }
    }

    public ParadaBean getParadaBean(String paradaString){
        ParadaDAO paradaDAO = new ParadaDAO();
        return paradaDAO.getParadaBean(paradaString);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////// CRIAR E ATUALIZAR APONTAMENTO ////////////////////////////////////

    private ApontMMBean createApontMM(BoletimCTR boletimCTR){
        ApontMMDAO apontMMDAO = new ApontMMDAO();
        return apontMMDAO.createApont(boletimCTR);
    }

    private ApontFertBean createApontFert(BoletimCTR boletimCTR){
        ApontFertDAO apontFertDAO = new ApontFertDAO();
        return apontFertDAO.createApont(boletimCTR);
    }

    public void salvarApont(Long status, Long idParada, Long idTransb, Double longitude, Double latitude){
        BoletimCTR boletimCTR = new BoletimCTR();
        Long func = 0L;
        Long equip = 0L;
        ConfigCTR configCTR = new ConfigCTR();
        if(configCTR.getEquip().getTipo() == 1) {
            BoletimMMBean boletimMMBean = boletimCTR.getBolMMAberto();
            apontMMBean.setIdBolApontMM(boletimMMBean.getIdBolMM());
            apontMMBean.setIdExtBolApontMM(boletimMMBean.getIdExtBolMM());
            apontMMBean.setOsApontMM(configCTR.getConfig().getOsConfig());
            apontMMBean.setAtivApontMM(configCTR.getConfig().getAtivConfig());
            apontMMBean.setStatusConApontMM(configCTR.getConfig().getStatusConConfig());
            apontMMBean.setParadaApontMM(idParada);
            apontMMBean.setTransbApontMM(idTransb);
            apontMMBean.setStatusApontMM(status);
            apontMMBean.setLongitudeApontMM(longitude);
            apontMMBean.setLatitudeApontMM(latitude);
            apontMMBean.setDthrApontMM(Tempo.getInstance().dataComHora());
            salvarApontMM(apontMMBean);
            func = boletimMMBean.getMatricFuncBolMM();
            equip = boletimMMBean.getIdEquipBolMM();
        }
        else{
            BoletimFertBean boletimFertBean = boletimCTR.getBolFertAberto();
            apontFertBean.setIdBolApontFert(boletimFertBean.getIdBolFert());
            apontFertBean.setIdExtBolApontFert(boletimFertBean.getIdExtBolFert());
            apontFertBean.setOsApontFert(configCTR.getConfig().getOsConfig());
            apontFertBean.setAtivApontFert(configCTR.getConfig().getAtivConfig());
            apontFertBean.setStatusConApontFert(configCTR.getConfig().getStatusConConfig());
            apontFertBean.setParadaApontFert(idParada);
            apontFertBean.setStatusApontFert(status);
            apontFertBean.setLongitudeApontFert(longitude);
            apontFertBean.setLatitudeApontFert(latitude);
            apontFertBean.setDthrApontFert(Tempo.getInstance().dataComHora());
            salvarApontFert(apontFertBean);
            func = boletimFertBean.getMatricFuncBolFert();
            equip = boletimFertBean.getIdEquipBolFert();
        }
        if(status == 0L){
            CabecPneuDAO cabecPneuDAO = new CabecPneuDAO();
            cabecPneuDAO.salvarDados(func, equip, getIdApont());
        }
    }

    public void atualApont(){
        ConfigCTR configCTR = new ConfigCTR();
        if(configCTR.getEquip().getTipo() == 1) {
            ApontMMDAO apontMMDAO = new ApontMMDAO();
            ApontMMBean apontMMBean = apontMMDAO.getApontMMAberto();
            apontMMDAO.updApont(apontMMBean);
        }
        else{
            ApontFertDAO apontFertDAO = new ApontFertDAO();
            ApontFertBean apontFertBean = apontFertDAO.getApontFertAberto();
            apontFertDAO.updApont(apontFertBean);
        }
    }

    private void salvarApontMM(ApontMMBean apontMMBean){

        BoletimCTR boletimCTR = new BoletimCTR();
        boletimCTR.atualQtdeApontBol();

        ApontMMDAO apontMMDAO = new ApontMMDAO();
        apontMMDAO.salvarApont(apontMMBean);

        ConfigCTR configCTR = new ConfigCTR();
        configCTR.setDtUltApontConfig(Tempo.getInstance().dataComHora());
    }

    private void salvarApontFert(ApontFertBean apontFertBean){

        BoletimCTR boletimCTR = new BoletimCTR();
        boletimCTR.atualQtdeApontBol();

        ApontFertDAO apontFertDAO = new ApontFertDAO();
        apontFertDAO.salvarApont(apontFertBean);

        ConfigCTR configCTR = new ConfigCTR();
        configCTR.setDtUltApontConfig(Tempo.getInstance().dataComHora());
    }

    public void inserirParadaImplemento(BoletimCTR boletimCTR){
        RFuncaoAtivParDAO rFuncaoAtivParDAO = new RFuncaoAtivParDAO();
        ConfigCTR configCTR = new ConfigCTR();
        if(configCTR.getEquip().getTipo() == 1) {
            ApontMMBean apontMMBean = createApontMM(boletimCTR);
            apontMMBean.setDthrApontMM(Tempo.getInstance().dataComHora());
            apontMMBean.setParadaApontMM(rFuncaoAtivParDAO.idParadaImplemento());
            salvarApontMM(apontMMBean);
        }
        else{
            ApontFertBean apontFertBean = createApontFert(boletimCTR);
            apontFertBean.setDthrApontFert(Tempo.getInstance().dataComHora());
            apontFertBean.setParadaApontFert(rFuncaoAtivParDAO.idParadaImplemento());
            salvarApontFert(apontFertBean);
        }

    }

    public void inserirParadaCheckList(BoletimCTR boletimCTR){
        RFuncaoAtivParDAO rFuncaoAtivParDAO = new RFuncaoAtivParDAO();
        ConfigCTR configCTR = new ConfigCTR();
        if(configCTR.getEquip().getTipo() == 1) {
            ApontMMBean apontMMBean = createApontMM(boletimCTR);
            apontMMBean.setDthrApontMM(Tempo.getInstance().dataComHora());
            apontMMBean.setParadaApontMM(rFuncaoAtivParDAO.idParadaCheckList());
            salvarApontMM(apontMMBean);
        }
        else{
            ApontFertBean apontFertBean = createApontFert(boletimCTR);
            apontFertBean.setDthrApontFert(Tempo.getInstance().dataComHora());
            apontFertBean.setParadaApontFert(rFuncaoAtivParDAO.idParadaCheckList());
            salvarApontFert(apontFertBean);
        }
    }

    public void inserirApontTransb(BoletimCTR boletimCTR, Long idTransb){
        ApontMMBean apontMMBean = createApontMM(boletimCTR);
        apontMMBean.setDthrApontMM(Tempo.getInstance().dataComHora());
        apontMMBean.setTransbApontMM(idTransb);
        salvarApontMM(apontMMBean);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////// VERIFICAÇÃO APONT ///////////////////////////////////////////

    public boolean verifBackupApont(Long idParada) {

        boolean v = false;
        ConfigCTR configCTR = new ConfigCTR();
        if(configCTR.getEquip().getTipo() == 1) {
            BoletimMMDAO boletimMMDAO = new BoletimMMDAO();
            ApontMMDAO apontMMDAO = new ApontMMDAO();
            List apontMMList = apontMMDAO.getListAllApont(boletimMMDAO.getIdBolAberto());
            if(apontMMList.size() > 0){
                ApontMMBean apontMMBean = (ApontMMBean) apontMMList.get(apontMMList.size() - 1);
                if ((configCTR.getConfig().getOsConfig().equals(apontMMBean.getOsApontMM()))
                        && (configCTR.getConfig().getAtivConfig().equals(apontMMBean.getAtivApontMM()))
                        && (idParada.equals(apontMMBean.getParadaApontMM()))) {
                    v = true;
                }
            }
            apontMMList.clear();
        }
        else{
            BoletimFertDAO boletimFertDAO = new BoletimFertDAO();
            ApontFertDAO apontFertDAO = new ApontFertDAO();
            List apontFertList = apontFertDAO.getListAllApont(boletimFertDAO.getIdBolAberto());
            if(apontFertList.size() > 0){
                ApontFertBean apontFertBean = (ApontFertBean) apontFertList.get(apontFertList.size() - 1);
                if ((configCTR.getConfig().getOsConfig().equals(apontFertBean.getOsApontFert()))
                        && (configCTR.getConfig().getAtivConfig().equals(apontFertBean.getAtivApontFert()))
                        && (idParada.equals(apontFertBean.getParadaApontFert()))) {
                    v = true;
                }
            }
            apontFertList.clear();
        }
        return v;
    }

    public boolean verifBackupApontTransb(Long idParada, Long idTransb) {
        boolean v = false;
        ConfigCTR configCTR = new ConfigCTR();
        BoletimMMDAO boletimMMDAO = new BoletimMMDAO();
        ApontMMDAO apontMMDAO = new ApontMMDAO();
        List apontMMList = apontMMDAO.getListAllApont(boletimMMDAO.getIdBolAberto());
        if(apontMMList.size() > 0){
            ApontMMBean apontMMBean = (ApontMMBean) apontMMList.get(apontMMList.size() - 1);
            if ((configCTR.getConfig().getOsConfig().equals(apontMMBean.getOsApontMM()))
                    && (configCTR.getConfig().getAtivConfig().equals(apontMMBean.getAtivApontMM()))
                    && (idParada.equals(apontMMBean.getParadaApontMM()))
                    && (idTransb.equals(apontMMBean.getTransbApontMM()))) {
                v = true;
            }
        }
        apontMMList.clear();
        return v;
    }

    public int verTrocaTransb(){
        ConfigCTR configCTR = new ConfigCTR();
        ApontMMDAO apontMMDAO = new ApontMMDAO();
        return apontMMDAO.verTransbordo(configCTR.getConfig().getDtUltApontConfig());
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////// RETORNA TODOS APONTAMENTO PARA HISTORICO ////////////////////////////////

    public List getListAllApontHist(Long idBolAberto){
        ConfigCTR configCTR = new ConfigCTR();
        if(configCTR.getEquip().getTipo() == 1) {
            ApontMMDAO apontMMDAO = new ApontMMDAO();
            List apontMMList = apontMMDAO.getListAllApont(idBolAberto);
            return  apontMMList;
        }
        else{
            ApontFertDAO apontFertDAO = new ApontFertDAO();
            List apontFertList = apontFertDAO.getListAllApont(idBolAberto);
            return  apontFertList;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////// MANIPULAR APONT DADOS MOTOMEC ////////////////////////////////////

    ////////// VERIFICAÇÃO PRA ENVIO ///////////////

    public Boolean verEnvioDadosApontMM(){
        Boolean retorno = false;
        ApontMMDAO apontMMDAO = new ApontMMDAO();
        MovLeiraDAO movLeiraDAO = new MovLeiraDAO();
        if((apontMMDAO.getListApontEnvio().size() > 0) || movLeiraDAO.getListMovLeiraAberto().size() > 0){
            retorno = true;
        }
        return retorno;
    }

    ////////// DADOS PRA ENVIO ///////////////

    public String dadosEnvioApontBolMM(ArrayList<Long> idBolList){
        ApontMMDAO apontMMDAO = new ApontMMDAO();
        MovLeiraDAO movLeiraDAO = new MovLeiraDAO();
        return apontMMDAO.dadosEnvioApontMM(apontMMDAO.getListApontEnvio(idBolList), movLeiraDAO.getListMovLeiraAberto(idBolList));
    }

    public String dadosEnvioApontMM(){
        ApontMMDAO apontMMDAO = new ApontMMDAO();
        MovLeiraDAO movLeiraDAO = new MovLeiraDAO();
        return apontMMDAO.dadosEnvioApontMM(apontMMDAO.getListApontEnvio(), movLeiraDAO.getListMovLeiraAberto());
    }

    ////////// MANIPULAÇÃO RETORNO DE ENVIO ///////////////

    public void updateApontMM(String retorno) {
        ApontMMDAO apontMMDAO = new ApontMMDAO();
        apontMMDAO.updateApont(retorno);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////// MANIPULAR APONT DADOS FERTIRRIGAÇÃO ////////////////////////////////////

    ////////// VERIFICAÇÃO PRA ENVIO ///////////////

    public boolean verEnvioDadosApontFert(){
        ApontFertDAO apontFertDAO = new ApontFertDAO();
        return apontFertDAO.getListApontEnvio().size() > 0;
    }

    ////////// DADOS PRA ENVIO ///////////////

    public String dadosEnvioApontBolFert(ArrayList<Long> idBolList){
        ApontFertDAO apontFertDAO = new ApontFertDAO();
        return apontFertDAO.dadosEnvioApontFert(apontFertDAO.getListApontEnvio(idBolList));
    }

    public String dadosEnvioApontFert(){
        ApontFertDAO apontFertDAO = new ApontFertDAO();
        return apontFertDAO.dadosEnvioApontFert(apontFertDAO.getListApontEnvio());
    }

    ////////// MANIPULAÇÃO RETORNO DE ENVIO ///////////////

    public void updateApontaFert(String retorno) {
        ApontFertDAO apontFertDAO = new ApontFertDAO();
        apontFertDAO.updateApont(retorno);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

}
